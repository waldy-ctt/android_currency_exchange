package com.waldy.androidcurrencyexchange.data.repository

import android.util.Log
import com.google.gson.JsonObject
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyHistoryDao
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyOfflineDao
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyHistory
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyOffline
import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import com.waldy.androidcurrencyexchange.domain.repository.GetConversionResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Concrete implementation of the CurrencyRepository from the Domain layer.
 * This class is responsible for fetching data from the remote API and caching it.
 */
class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val currencyOfflineDao: CurrencyOfflineDao,
    private val currencyHistoryDao: CurrencyHistoryDao
) : CurrencyRepository {

    override fun getConversionRate(from: Currency, to: Currency): Flow<GetConversionResult> = flow {
        val oneHourInMillis = 3600 * 1000
        var localData: CurrencyOffline? = null

        // 1. Emit cached value if it exists and is not stale.
        try {
            localData = currencyOfflineDao.getCurrencyRatio(from.name.lowercase(), to.name.lowercase()).firstOrNull()
            val isStale = (System.currentTimeMillis() - (localData?.timestamp ?: 0)) > oneHourInMillis

            if (localData != null && !isStale) {
                emit(GetConversionResult(rate = localData.ratio, isOffline = false))
                return@flow // We have fresh data, no need to fetch from network.
            }
        } catch (e: Exception) {
            // Cached data might be corrupt, ignore it.
        }

        // 2. Fetch live value from network (if no data, or data is stale).
        try {
            val response = apiService.getLatestRates(from.name.lowercase())

            val ratesObject = response.getAsJsonObject(from.name.lowercase())
            val ratesToSave = ratesObject.entrySet().map { (currencyCode, rateElement) ->
                CurrencyOffline(
                    baseCurrency = from.name.lowercase(),
                    targetCurrency = currencyCode,
                    ratio = rateElement.asDouble,
                    timestamp = System.currentTimeMillis()
                )
            }
            currencyOfflineDao.upsertAll(ratesToSave)

            val rate = parseRateFromResponse(response, from, to)
            emit(GetConversionResult(rate = rate, isOffline = false))
        } catch (e: Exception) {
            if (localData != null) {
                // If network fails, emit the stale data but flag it as offline.
                emit(GetConversionResult(rate = localData.ratio, isOffline = true))
            } else {
                throw e
            }
        }
    }

    override fun getHistory(from: Currency, to: Currency): Flow<List<CurrencyHistory>> {
        val fromCurrencyCode = from.name.lowercase()
        val toCurrencyCode = to.name.lowercase()

        return currencyHistoryDao.getHistory(fromCurrencyCode, toCurrencyCode)
            .onStart {
                coroutineScope {
                    val today = LocalDate.now()
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                    // 1. Get all history from DB and identify missing dates in the last 30 days
                    val existingHistory = currencyHistoryDao.getHistory(fromCurrencyCode, toCurrencyCode).first()
                    val existingDates = existingHistory.map { it.date }.toSet()
                    val requiredDates = (0..29).map { today.minusDays(it.toLong()).format(formatter) }

                    // Always include today to be fetched to get the latest rate
                    val missingDates = requiredDates.filter { it !in existingDates || it == today.format(formatter) }

                    // 2. Fetch missing historical data in parallel
                    if (missingDates.isNotEmpty()) {
                        val fetchJobs = missingDates.map { dateString ->
                            async {
                                try {
                                    // "latest" for today, specific date string otherwise
                                    val apiDateString = if (LocalDate.parse(dateString, formatter).isEqual(today)) "latest" else dateString
                                    val response = apiService.getHistoricalRates(apiDateString, fromCurrencyCode)
                                    val rate = response.getAsJsonObject(fromCurrencyCode)?.get(toCurrencyCode)?.asDouble

                                    if (rate != null) {
                                        CurrencyHistory(
                                            baseCurrency = fromCurrencyCode,
                                            targetCurrency = toCurrencyCode,
                                            date = dateString,
                                            ratio = rate
                                        )
                                    } else {
                                        null
                                    }
                                } catch (e: Exception) {
                                    Log.e("CurrencyRepository", "Failed to fetch history for $dateString", e)
                                    null // Don't block everything if one day fails
                                }
                            }
                        }
                        val newHistoryEntries = fetchJobs.awaitAll().filterNotNull()
                        if (newHistoryEntries.isNotEmpty()) {
                            currencyHistoryDao.upsertAll(newHistoryEntries)
                        }
                    }

                    // 3. Prune old data (older than 35 days)
                    val pruneDate = today.minusDays(35).format(formatter)
                    clearHistory(pruneDate)
                }
            }
    }

    override suspend fun clearHistory(date: String) {
        currencyHistoryDao.clearHistoryData(date = date)
    }

    private fun parseRateFromResponse(response: JsonObject, from: Currency, to: Currency): Double {
        val fromCurrencyCode = from.name.lowercase()
        val toCurrencyCode = to.name.lowercase()

        val ratesObject = response.getAsJsonObject(fromCurrencyCode)
            ?: throw Exception("Malformed API response: '$fromCurrencyCode' object not found")

        return ratesObject.get(toCurrencyCode)?.asDouble
            ?: throw Exception("Rate for '$toCurrencyCode' not found in response")
    }
}

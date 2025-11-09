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

        var localData: CurrencyOffline? = null

        // 1. Emit cached value if it exists.
        try {
            localData = currencyOfflineDao.getCurrencyRatio(from.name.lowercase(), to.name.lowercase()).firstOrNull()
            if (localData != null) {
                emit(GetConversionResult(rate = localData.ratio, isOffline = false))
            }

        } catch (e: Exception) {
            // Cached data might be corrupt, ignore it.
        }

        // 2. Fetch live value from network.
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
            .onStart { // This block runs in a coroutine when the flow is first collected
                coroutineScope { // Creates a scope for our parallel jobs
                    val today = LocalDate.now()

                    // 1. Always fetch the latest (today's) ratio to keep it fresh.
                    try {
                        val latestResponse = apiService.getHistoricalRates("latest", fromCurrencyCode)
                        val ratesObject = latestResponse.getAsJsonObject(fromCurrencyCode)
                        val latestRate = ratesObject?.get(toCurrencyCode)?.asDouble

                        if (latestRate != null) {
                            val todayHistory = CurrencyHistory(
                                baseCurrency = fromCurrencyCode,
                                targetCurrency = toCurrencyCode,
                                date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                ratio = latestRate
                            )
                            // Using upsert will insert or update today's record.
                            currencyHistoryDao.upsertAll(listOf(todayHistory))
                        }
                    } catch (e: Exception) {
                        Log.e("CurrencyRepository", "Failed to fetch latest rate", e)
                        // Could not fetch today's rate, proceed with cached data.
                    }

                    // 2. Check if we need to backfill the history (e.g., first launch).
                    val existingHistory = currencyHistoryDao.getHistory(fromCurrencyCode, toCurrencyCode).first()
                    if (existingHistory.size < 30) {
                        val existingDates = existingHistory.map { it.date }.toSet()

                        // Fetch the last 29 days (plus today makes 30).
                        val jobs = (1..29).map { i ->
                            async {
                                val date = today.minusDays(i.toLong())
                                val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

                                // Don't re-fetch data we already have.
                                if (dateString in existingDates) {
                                    return@async null
                                }

                                try {
                                    val response = apiService.getHistoricalRates(dateString, fromCurrencyCode)
                                    val rate = response.getAsJsonObject(fromCurrencyCode)
                                        ?.get(toCurrencyCode)?.asDouble

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
                                    null // Ignore errors for single past days.
                                }
                            }
                        }
                        val newHistoryEntries = jobs.awaitAll().filterNotNull()
                        if (newHistoryEntries.isNotEmpty()) {
                            currencyHistoryDao.upsertAll(newHistoryEntries)
                        }
                    }
                }
            }
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

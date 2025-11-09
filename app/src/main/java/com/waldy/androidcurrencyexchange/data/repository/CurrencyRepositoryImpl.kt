package com.waldy.androidcurrencyexchange.data.repository

import com.google.gson.Gson
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

    private val gson = Gson()

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
        return currencyHistoryDao.getHistory(from.name.lowercase(), to.name.lowercase())
            .onStart { // This block runs in a coroutine when the flow is first collected
                coroutineScope { // Creates a scope for our parallel jobs
                    val today = LocalDate.now()
                    val existingDates = currencyHistoryDao.getHistory(from.name.lowercase(), to.name.lowercase())
                        .first().map { it.date }.toSet()

                    val jobs = (0..29).map { i ->
                        async { // Launch each network call in parallel
                            val date = today.minusDays(i.toLong())
                            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

                            if (dateString in existingDates) {
                                return@async null // Don't re-fetch data we already have
                            }

                            try {
                                val response = apiService.getHistoricalRates(dateString, from.name.lowercase())

                                val ratesObject = response.getAsJsonObject(from.name.lowercase())
                                val rate = ratesObject?.get(to.name.lowercase())?.asDouble

                                if (rate != null) {
                                    CurrencyHistory(
                                        baseCurrency = from.name.lowercase(),
                                        targetCurrency = to.name.lowercase(),
                                        date = dateString,
                                        ratio = rate
                                    )
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                null // Ignore errors for single days
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

    private fun parseRateFromResponse(response: JsonObject, from: Currency, to: Currency): Double {
        val fromCurrencyCode = from.name.lowercase()
        val toCurrencyCode = to.name.lowercase()

        val ratesObject = response.getAsJsonObject(fromCurrencyCode)
            ?: throw Exception("Malformed API response: '$fromCurrencyCode' object not found")

        return ratesObject.get(toCurrencyCode)?.asDouble
            ?: throw Exception("Rate for '$toCurrencyCode' not found in response")
    }
}

package com.waldy.androidcurrencyexchange.data.repository

import android.util.Log
import com.google.gson.JsonObject
import com.waldy.androidcurrencyexchange.common.NetworkManager
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyHistoryDao
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyOfflineDao
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyHistory
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyOffline
import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import com.waldy.androidcurrencyexchange.domain.repository.GetConversionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val currencyOfflineDao: CurrencyOfflineDao,
    private val currencyHistoryDao: CurrencyHistoryDao,
    private val networkManager: NetworkManager
) : CurrencyRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val historyFetchSemaphore = Semaphore(5) // Limit to 5 concurrent history fetches

    override fun getConversionRate(from: Currency, to: Currency): Flow<GetConversionResult> = flow {
        val oneHourInMillis = 3600 * 1000
        val localData: CurrencyOffline? = currencyOfflineDao.getCurrencyRatio(from.name.lowercase(), to.name.lowercase()).firstOrNull()

        if (!networkManager.isNetworkAvailable()) {
            if (localData != null) {
                emit(GetConversionResult(rate = localData.ratio, isOffline = true))
            } else {
                throw Exception("No internet connection and no cached data available.")
            }
            return@flow
        }

        val isStale = (System.currentTimeMillis() - (localData?.timestamp ?: 0)) > oneHourInMillis
        if (localData != null && !isStale) {
            emit(GetConversionResult(rate = localData.ratio, isOffline = false))
            return@flow
        }

        try {
            val response = apiService.getLatestRates(from.name.lowercase())
            val rate = parseRateFromResponse(response, from, to)
            emit(GetConversionResult(rate = rate, isOffline = false))
            repositoryScope.launch {
                try {
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
                } catch (e: Exception) {
                    Log.e("CurrencyRepository", "Failed to cache offline rates", e)
                }
            }
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
            .onStart {
                if (!networkManager.isNetworkAvailable()) return@onStart

                repositoryScope.launch {
                    val today = LocalDate.now()
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                    val existingHistory = currencyHistoryDao.getHistory(fromCurrencyCode, toCurrencyCode).first()
                    val existingDates = existingHistory.map { it.date }.toSet()
                    val requiredDates = (0..29).map { today.minusDays(it.toLong()).format(formatter) }

                    val missingDates = requiredDates.filter { it !in existingDates || it == today.format(formatter) }

                    if (missingDates.isNotEmpty()) {
                        val priorityDates = missingDates.take(7)
                        val backgroundDates = missingDates.drop(7)

                        suspend fun fetchAndStoreDates(dates: List<String>) {
                            if (dates.isEmpty()) return
                            val fetchJobs = dates.map { dateString ->
                                async {
                                    historyFetchSemaphore.withPermit {
                                        try {
                                            val apiDateString = if (LocalDate.parse(dateString, formatter).isEqual(today)) "latest" else dateString
                                            val response = apiService.getHistoricalRates(apiDateString, fromCurrencyCode)
                                            val rate = response.getAsJsonObject(fromCurrencyCode)?.get(toCurrencyCode)?.asDouble

                                            if (rate != null) {
                                                CurrencyHistory(fromCurrencyCode, toCurrencyCode, dateString, rate)
                                            } else {
                                                null
                                            }
                                        } catch (e: Exception) {
                                            Log.e("CurrencyRepository", "Failed to fetch history for $dateString", e)
                                            null
                                        }
                                    }
                                }
                            }
                            val newHistoryEntries = fetchJobs.awaitAll().filterNotNull()
                            if (newHistoryEntries.isNotEmpty()) {
                                currencyHistoryDao.upsertAll(newHistoryEntries)
                            }
                        }

                        fetchAndStoreDates(priorityDates)
                        launch { fetchAndStoreDates(backgroundDates) }
                    }

                    val pruneDate = today.minusDays(35).format(formatter)
                    clearHistory(pruneDate)
                }
            }
    }


    override suspend fun clearHistory(date: String) {
        currencyHistoryDao.clearHistoryData(date = date)
    }

    override fun hasHistory(): Flow<Int> {
        return currencyHistoryDao.hasHistory()
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

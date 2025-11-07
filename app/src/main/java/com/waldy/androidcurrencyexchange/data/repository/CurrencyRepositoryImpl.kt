package com.waldy.androidcurrencyexchange.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyOfflineDao
import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import com.waldy.androidcurrencyexchange.domain.repository.GetConversionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Concrete implementation of the CurrencyRepository from the Domain layer.
 * This class is responsible for fetching data from the remote API and caching it.
 */
class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val currencyOfflineDao: CurrencyOfflineDao
) : CurrencyRepository {

    private val gson = Gson()

    override fun getConversionRate(from: Currency, to: Currency): Flow<GetConversionResult> = flow {

        // 1. Emit cached value if it exists, but don't mark as offline yet.
        try {
            // Try here since the getCurrencyRatio might throw exception when failed instead of return null
            currencyOfflineDao.getCurrencyRatio(from.name, to.name).collect { (baseCurrency, targetCurrency, ratio, timestamp) ->
                emit(GetConversionResult(rate = ratio, isOffline = false))
            }

        } catch (e: Exception) {
            // Cached data might be corrupt, ignore it.
        }

        // 2. Fetch live value from network.
        try {
            val response = apiService.getLatestRates(from.name.lowercase())
            currencyOfflineDao.upsertAll(response.map { (baseCurrency, ratio) => return  })
            val rate = parseRateFromResponse(response, from, to)
            emit(GetConversionResult(rate = rate, isOffline = false))
        } catch (e: Exception) {
            // If network fails, check if we already served a cached value.
            if (cachedJson != null) {
                // Re-emit the cached value, but this time, flag it as offline.
                // This will make the offline badge appear without flickering.
                val cachedResponse = gson.fromJson(cachedJson, JsonObject::class.java)
                val cachedRate = parseRateFromResponse(cachedResponse, from, to)
                emit(GetConversionResult(rate = cachedRate, isOffline = true))
            } else {
                // No cache and network failed, so propagate the error.
                throw e
            }
        }
    }

    private fun parseRateFromResponse(response: JsonObject, from: Currency, to: Currency): Double {
        val fromCurrencyCode = from.name.lowercase()
        val toCurrencyCode = to.name.lowercase()

        val ratesObject = response.getAsJsonObject(fromCurrencyCode)
            ?: throw Exception("Malformed API response: '$$fromCurrencyCode' object not found")

        return ratesObject.get(toCurrencyCode)?.asDouble
            ?: throw Exception("Rate for '$$toCurrencyCode' not found in response")
    }
}

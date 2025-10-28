package com.waldy.androidcurrencyexchange.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.waldy.androidcurrencyexchange.data.local.CurrencyCache
import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import com.waldy.androidcurrencyexchange.domain.repository.GetConversionResult

/**
 * Concrete implementation of the CurrencyRepository from the Domain layer.
 * This class is responsible for fetching data from the remote API and caching it.
 */
class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val cache: CurrencyCache
) : CurrencyRepository {

    private val gson = Gson()

    override suspend fun getConversionRate(from: Currency, to: Currency): GetConversionResult {
        return try {
            // Try to fetch live rates
            val response = apiService.getLatestRates(from.name.lowercase())

            // Save the successful response to the cache
            cache.saveRates(from, response.toString())

            // Parse and extract the rate
            val rate = parseRateFromResponse(response, from, to)
            GetConversionResult(rate = rate, isOffline = false)

        } catch (e: Exception) {
            // On any network failure, try to fall back to the cache
            val cachedJson = cache.getRates(from)
            if (cachedJson != null) {
                val cachedResponse = gson.fromJson(cachedJson, JsonObject::class.java)
                val cachedRate = parseRateFromResponse(cachedResponse, from, to)
                GetConversionResult(rate = cachedRate, isOffline = true)
            } else {
                // If there's no cache, we must throw the original exception
                throw e
            }
        }
    }

    private fun parseRateFromResponse(response: JsonObject, from: Currency, to: Currency): Double {
        val fromCurrencyCode = from.name.lowercase()
        val toCurrencyCode = to.name.lowercase()

        // The API nests the rates inside an object with the from_currency code as the key
        val ratesObject = response.getAsJsonObject(fromCurrencyCode)
            ?: throw Exception("Malformed API response: '${fromCurrencyCode}' object not found")

        return ratesObject.get(toCurrencyCode)?.asDouble
            ?: throw Exception("Rate for '${toCurrencyCode}' not found in response")
    }
}

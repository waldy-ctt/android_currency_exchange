package com.waldy.androidcurrencyexchange.data.repository

import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository

/**
 * Concrete implementation of the CurrencyRepository from the Domain layer.
 * This class is responsible for fetching data from the remote API and mapping it to the domain model.
 */
class CurrencyRepositoryImpl(private val apiService: CurrencyApiService) : CurrencyRepository {

    override suspend fun getConversionRate(from: Currency, to: Currency): Double {
        // Fetch the rates from the API for the given base currency
        val response = apiService.getLatestRates(from.name.lowercase())

        // Find the specific rate for the target currency from the response map
        // The key in the map is the lowercase currency code (e.g., "eur").
        val rate = response.rates[to.name.lowercase()]

        // If the rate is not found, throw an exception. This can be handled by a higher-level error state.
        return rate ?: throw Exception("Conversion rate for ${to.name} not found")
    }
}

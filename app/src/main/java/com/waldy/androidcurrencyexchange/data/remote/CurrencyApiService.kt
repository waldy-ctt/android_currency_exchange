package com.waldy.androidcurrencyexchange.data.remote

import com.waldy.androidcurrencyexchange.data.remote.dto.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines the API endpoints for fetching currency data using Retrofit.
 */
interface CurrencyApiService {

    /**
     * Fetches the latest conversion rates for a given base currency.
     * Example endpoint: "currencies/usd.json"
     *
     * @param fromCurrencyCode The 3-letter code of the base currency (e.g., "usd").
     * @return A [CurrencyResponse] object containing the rates.
     */
    @GET("currencies/{from_currency_code}.json")
    suspend fun getLatestRates(
        @Path("from_currency_code") fromCurrencyCode: String
    ): CurrencyResponse
}

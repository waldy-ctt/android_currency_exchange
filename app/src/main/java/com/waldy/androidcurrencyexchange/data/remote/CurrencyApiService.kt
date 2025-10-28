package com.waldy.androidcurrencyexchange.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines the API endpoints for fetching currency data using Retrofit.
 */
interface CurrencyApiService {

    /**
     * Fetches the latest conversion rates for a given base currency.
     * We use JsonObject here because the response contains a dynamic key (e.g., "eur", "usd").
     *
     * @param fromCurrencyCode The 3-letter code of the base currency (e.g., "usd").
     * @return A [JsonObject] containing the full, raw response.
     */
    @GET("currencies/{from_currency_code}.json")
    suspend fun getLatestRates(
        @Path("from_currency_code") fromCurrencyCode: String
    ): JsonObject
}

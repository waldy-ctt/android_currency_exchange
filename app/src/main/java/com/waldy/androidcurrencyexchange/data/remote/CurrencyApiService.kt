package com.waldy.androidcurrencyexchange.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for the Currency API.
 */
interface CurrencyApiService {

    /**
     * Fetches the latest conversion rates for a given base currency.
     */
    @GET("@latest/v1/currencies/{currency}.json")
    suspend fun getLatestRates(@Path("currency") currency: String): JsonObject

    /**
     * Fetches historical conversion rates for a given date and base currency.
     */
    @GET("@{date}/v1/currencies/{currency}.json")
    suspend fun getHistoricalRates(
        @Path("date") date: String, // YYYY-MM-DD
        @Path("currency") currency: String
    ): JsonObject
}

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
     * Generates URL like: https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json
     */
    @GET("@fawazahmed0/currency-api@latest/v1/currencies/{currency}.json")
    suspend fun getLatestRates(@Path("currency") currency: String): JsonObject

    /**
     * Fetches historical conversion rates for a given date and base currency.
     * The date can be "latest" or a specific date in "YYYY-MM-DD" format.
     * Generates URL like: https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2024-03-06/v1/currencies/eur.json
     */
    @GET("@fawazahmed0/currency-api@{date}/v1/currencies/{currency}.json")
    suspend fun getHistoricalRates(
        @Path("date") date: String, // "latest" or YYYY-MM-DD
        @Path("currency") currency: String
    ): JsonObject
}

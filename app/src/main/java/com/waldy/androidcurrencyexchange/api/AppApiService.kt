package com.waldy.androidcurrencyexchange.api

import retrofit2.Call
import retrofit2.http.GET

interface AppApiService {
    // GET: All currency list
    @GET("currencies.json")
    fun getCurrencyList(): Call<Map<String, String>>
}
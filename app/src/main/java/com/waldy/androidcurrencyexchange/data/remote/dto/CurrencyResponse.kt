package com.waldy.androidcurrencyexchange.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for the API's currency response.
 * This is a data-layer-specific model and should not be used in other layers.
 */
data class CurrencyResponse(
    @SerializedName("date") val date: String,
    // The key for the rates can vary (e.g., "eur", "usd"), so we use a map.
    val rates: Map<String, Double>
)

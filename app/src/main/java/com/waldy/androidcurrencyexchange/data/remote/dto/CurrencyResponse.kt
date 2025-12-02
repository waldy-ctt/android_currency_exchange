package com.waldy.androidcurrencyexchange.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for the API's currency response.
 * This is a data-layer-specific model and should not be used in other layers.
 * The structure is designed to handle the nested JSON object where the key is the currency code.
 */
data class CurrencyResponse(
    @SerializedName("date") val date: String,
    // Use a map to dynamically capture the nested object (e.g., "eur", "usd").
    // The value of this map is another map containing the actual conversion rates.
    val rates: Map<String, Map<String, Double>>
)

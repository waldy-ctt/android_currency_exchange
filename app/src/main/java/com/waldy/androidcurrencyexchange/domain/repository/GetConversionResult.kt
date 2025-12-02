package com.waldy.androidcurrencyexchange.domain.repository

/**
 * A wrapper class for the result of a conversion rate fetch.
 * This allows the UI to know if the data is fresh or from an offline cache.
 */
data class GetConversionResult(
    val rate: Double,
    val isOffline: Boolean
)

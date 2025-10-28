package com.waldy.androidcurrencyexchange.domain.repository

import com.waldy.androidcurrencyexchange.domain.model.Currency

/**
 * Interface for the currency data repository.
 * This is part of the Domain layer and defines the contract that the Data layer must implement.
 */
interface CurrencyRepository {

    /**
     * Fetches the conversion rate from one currency to another.
     *
     * @param from The base currency.
     * @param to The target currency.
     * @return A [GetConversionResult] containing the rate and the data source status (online/offline).
     */
    suspend fun getConversionRate(from: Currency, to: Currency): GetConversionResult
}

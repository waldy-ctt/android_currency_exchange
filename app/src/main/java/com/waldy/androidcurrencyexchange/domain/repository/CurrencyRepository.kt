package com.waldy.androidcurrencyexchange.domain.repository

import com.waldy.androidcurrencyexchange.domain.model.Currency
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the currency data repository.
 * This is part of the Domain layer and defines the contract that the Data layer must implement.
 */
interface CurrencyRepository {

    /**
     * Fetches the conversion rate from one currency to another, providing cached data first,
     * then fresh data from the network.
     *
     * @param from The base currency.
     * @param to The target currency.
     * @return A Flow that emits [GetConversionResult], first with cached data, then with live data.
     */
    fun getConversionRate(from: Currency, to: Currency): Flow<GetConversionResult>
}

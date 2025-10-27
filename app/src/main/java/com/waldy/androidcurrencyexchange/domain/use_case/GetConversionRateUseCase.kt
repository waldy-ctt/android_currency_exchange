package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * This use case encapsulates the business logic for converting a currency value.
 * It relies on the CurrencyRepository to fetch the necessary conversion rate.
 */
class GetConversionRateUseCase(private val currencyRepository: CurrencyRepository) {

    suspend operator fun invoke(from: Currency, to: Currency, amount: BigDecimal): BigDecimal {
        // Fetch the raw rate from the repository
        val rate = currencyRepository.getConversionRate(from, to)

        // Perform the calculation using BigDecimal for precision
        val conversionRate = BigDecimal(rate.toString())
        val result = amount.multiply(conversionRate)

        // Return the result rounded to 2 decimal places, which is standard for currency
        return result.setScale(2, RoundingMode.HALF_UP)
    }
}

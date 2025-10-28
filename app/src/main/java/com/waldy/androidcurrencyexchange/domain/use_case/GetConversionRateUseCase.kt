package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode

data class ConversionOutput(
    val convertedAmount: BigDecimal,
    val isOffline: Boolean
)

/**
 * This use case encapsulates the business logic for converting a currency value.
 * It relies on the CurrencyRepository to fetch the necessary conversion rate.
 */
class GetConversionRateUseCase(private val currencyRepository: CurrencyRepository) {

    operator fun invoke(from: Currency, to: Currency, amount: BigDecimal): Flow<ConversionOutput> {
        // Fetch the rate from the repository as a Flow
        return currencyRepository.getConversionRate(from, to).map { result ->
            // Perform the calculation using BigDecimal for precision
            val conversionRate = BigDecimal(result.rate.toString())
            val convertedAmount = amount.multiply(conversionRate)

            // Round the result to 2 decimal places, which is standard for currency
            val finalAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP)

            ConversionOutput(finalAmount, result.isOffline)
        }
    }
}

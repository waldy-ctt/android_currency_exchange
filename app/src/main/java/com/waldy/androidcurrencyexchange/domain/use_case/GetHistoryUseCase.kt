package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository

class GetHistoryUseCase(private val repository: CurrencyRepository) {

    operator fun invoke(from: Currency, to: Currency) = repository.getHistory(from, to)
}

package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetHasHistoryUseCase(private val repository: CurrencyRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.hasHistory().map { it > 0 }
    }
}

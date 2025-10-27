package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * A use case to get the current theme from user preferences.
 */
class GetThemeUseCase(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<String> = userPreferencesRepository.theme
}

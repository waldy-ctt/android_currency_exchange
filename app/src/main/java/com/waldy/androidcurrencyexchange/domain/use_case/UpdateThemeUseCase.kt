package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.data.preferences.UserPreferencesRepository

/**
 * A use case to save the user's selected theme.
 */
class UpdateThemeUseCase(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(theme: String) = userPreferencesRepository.saveTheme(theme)
}

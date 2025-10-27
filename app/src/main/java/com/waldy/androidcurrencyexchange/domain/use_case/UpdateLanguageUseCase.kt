package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.data.preferences.UserPreferencesRepository
import com.waldy.androidcurrencyexchange.ui.util.Language

/**
 * A use case to save the user's selected language.
 */
class UpdateLanguageUseCase(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(language: Language) = userPreferencesRepository.saveLanguage(language)
}

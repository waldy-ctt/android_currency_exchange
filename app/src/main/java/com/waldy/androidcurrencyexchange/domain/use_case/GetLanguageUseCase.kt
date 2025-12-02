package com.waldy.androidcurrencyexchange.domain.use_case

import com.waldy.androidcurrencyexchange.data.preferences.UserPreferencesRepository
import com.waldy.androidcurrencyexchange.ui.util.Language
import kotlinx.coroutines.flow.Flow

/**
 * A use case to get the current language from user preferences.
 */
class GetLanguageUseCase(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Language> = userPreferencesRepository.language
}

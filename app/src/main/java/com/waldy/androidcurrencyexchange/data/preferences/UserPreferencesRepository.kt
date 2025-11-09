package com.waldy.androidcurrencyexchange.data.preferences

import com.waldy.androidcurrencyexchange.data.db.dao.UserPreferencesDao
import com.waldy.androidcurrencyexchange.data.db.model.UserPreferences
import com.waldy.androidcurrencyexchange.ui.util.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * A repository for managing user preferences, such as theme and language.
 * It uses Room for persistence.
 */
class UserPreferencesRepository(private val userPreferencesDao: UserPreferencesDao) {

    // Flow to observe the current theme
    val theme: Flow<String> = userPreferencesDao.getPreferences().map { it?.theme ?: "Device" }

    // Flow to observe the current language
    val language: Flow<Language> = userPreferencesDao.getPreferences().map {
        val langTag = it?.language ?: Language.ENGLISH.tag
        Language.entries.first { l -> l.tag == langTag }
    }

    /**
     * Saves the user's selected theme.
     */
    suspend fun saveTheme(theme: String) {
        val currentPrefs = userPreferencesDao.getPreferences().firstOrNull()
        val newPrefs = currentPrefs?.copy(theme = theme) ?: UserPreferences(theme = theme, language = Language.ENGLISH.tag)
        userPreferencesDao.savePreferences(newPrefs)
    }

    /**
     * Saves the user's selected language.
     */
    suspend fun saveLanguage(language: Language) {
        val currentPrefs = userPreferencesDao.getPreferences().firstOrNull()
        val newPrefs = currentPrefs?.copy(language = language.tag) ?: UserPreferences(theme = "Device", language = language.tag)
        userPreferencesDao.savePreferences(newPrefs)
    }
}

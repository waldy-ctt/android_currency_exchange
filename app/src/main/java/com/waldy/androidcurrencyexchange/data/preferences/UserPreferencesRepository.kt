package com.waldy.androidcurrencyexchange.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.waldy.androidcurrencyexchange.ui.util.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance, tied to the application context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * A repository for managing user preferences, such as theme and language.
 * It uses Jetpack DataStore for persistence.
 */
class UserPreferencesRepository(private val context: Context) {

    // Define keys for the preferences
    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    // Flow to observe the current theme
    val theme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.APP_THEME] ?: "Device"
        }

    // Flow to observe the current language
    val language: Flow<Language> = context.dataStore.data
        .map { preferences ->
            val langTag = preferences[PreferencesKeys.APP_LANGUAGE] ?: Language.ENGLISH.tag
            Language.entries.first { it.tag == langTag }
        }

    /**
     * Saves the user's selected theme.
     */
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit {
            it[PreferencesKeys.APP_THEME] = theme
        }
    }

    /**
     * Saves the user's selected language.
     */
    suspend fun saveLanguage(language: Language) {
        context.dataStore.edit {
            it[PreferencesKeys.APP_LANGUAGE] = language.tag
        }
    }
}

package com.waldy.androidcurrencyexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.waldy.androidcurrencyexchange.ui.theme.AndroidCurrencyExchangeTheme
import com.waldy.androidcurrencyexchange.ui.util.Language
import com.waldy.androidcurrencyexchange.ui.util.Localization

/**
 * This is the main and only Activity in the application.
 * Its responsibility is to observe theme/language settings and provide them to the UI.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = (application as CurrencyApplication).container

        setContent {
            // Observe language and theme from the repository
            val language by container.getLanguageUseCase().collectAsState(initial = Language.ENGLISH)
            val theme by container.getThemeUseCase().collectAsState(initial = "Device")

            // nen tach rieng composable cho nay ra khong?
            CompositionLocalProvider(Localization.currentLanguage provides language) {
                AndroidCurrencyExchangeTheme(
                    darkTheme = when (theme) {
                        "Light" -> false
                        "Dark" -> true
                        else -> isSystemInDarkTheme()
                    }
                ) {
                    MainActivityScreen()
                }
            }
        }
    }
}

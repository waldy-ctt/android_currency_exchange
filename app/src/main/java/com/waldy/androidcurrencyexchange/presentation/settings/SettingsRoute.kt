package com.waldy.androidcurrencyexchange.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waldy.androidcurrencyexchange.CurrencyApplication

/**
 * The stateful entry point for the Settings feature.
 * This composable is responsible for creating the ViewModel and connecting it to the screen.
 */
@Composable
fun SettingsRoute() {
    val application = LocalContext.current.applicationContext as CurrencyApplication
    val container = application.container

    // cho nay hoi dai dong. co the don gian hoa viec tao ViewModel khong?
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            getThemeUseCase = container.getThemeUseCase,
            getLanguageUseCase = container.getLanguageUseCase,
            updateThemeUseCase = container.updateThemeUseCase,
            updateLanguageUseCase = container.updateLanguageUseCase
        )
    )

    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()

    SettingsScreen(
        theme = theme,
        language = language,
        onThemeChange = viewModel::onThemeChange,
        onLanguageChange = viewModel::onLanguageChange
    )
}

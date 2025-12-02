package com.waldy.androidcurrencyexchange.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waldy.androidcurrencyexchange.domain.use_case.GetLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetThemeUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateThemeUseCase

/**
 * Factory for creating instances of SettingsViewModel.
 */
class SettingsViewModelFactory(
    private val getThemeUseCase: GetThemeUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                getThemeUseCase,
                updateThemeUseCase,
                getLanguageUseCase,
                updateLanguageUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

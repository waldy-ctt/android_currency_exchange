package com.waldy.androidcurrencyexchange.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldy.androidcurrencyexchange.domain.use_case.GetLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetThemeUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateThemeUseCase
import com.waldy.androidcurrencyexchange.ui.util.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen.
 */
class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase
) : ViewModel() {

    private val _theme = MutableStateFlow("Device")
    val theme = _theme.asStateFlow()

    private val _language = MutableStateFlow(Language.ENGLISH)
    val language = _language.asStateFlow()

    init {
        getThemeUseCase().onEach { _theme.value = it }.launchIn(viewModelScope)
        getLanguageUseCase().onEach { _language.value = it }.launchIn(viewModelScope)
    }

    fun onThemeChange(theme: String) {
        viewModelScope.launch {
            updateThemeUseCase(theme)
        }
    }

    fun onLanguageChange(language: Language) {
        viewModelScope.launch {
            updateLanguageUseCase(language)
        }
    }
}

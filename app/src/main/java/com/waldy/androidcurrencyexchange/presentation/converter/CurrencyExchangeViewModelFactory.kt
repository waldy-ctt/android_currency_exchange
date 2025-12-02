package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waldy.androidcurrencyexchange.domain.use_case.GetConversionRateUseCase

/**
 * Factory for creating instances of CurrencyExchangeViewModel.
 * This is necessary to pass the GetConversionRateUseCase to the ViewModel's constructor.
 */
class CurrencyExchangeViewModelFactory(private val getConversionRateUseCase: GetConversionRateUseCase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyExchangeViewModel::class.java)) {
            return CurrencyExchangeViewModel(getConversionRateUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

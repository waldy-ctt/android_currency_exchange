package com.waldy.androidcurrencyexchange.presentation.converter.convert_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waldy.androidcurrencyexchange.domain.use_case.GetHistoryUseCase

/**
 * Factory for creating instances of ConvertHistoryViewModel.
 * This is necessary to pass the GetHistoryUseCase to the ViewModel's constructor.
 */
class RatioHistoryViewModelFactory(private val getHistoryUseCase: GetHistoryUseCase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatioHistoryViewModel::class.java)) {
            return RatioHistoryViewModel(getHistoryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.waldy.androidcurrencyexchange.presentation.converter.convert_history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyHistory
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.use_case.GetHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RatioHistoryViewModel(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    data class RatioHistoryState(
        val fromCurrency: Currency = Currency.USD,
        val toCurrency: Currency = Currency.VND,
        val history: List<CurrencyHistory> = emptyList(),
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(RatioHistoryState())
    val uiState: StateFlow<RatioHistoryState> = _uiState.asStateFlow()

    private var historyJob: Job? = null

    init {
        // Automatically load the history for the default currencies when the ViewModel is created.
        loadHistory(_uiState.value.fromCurrency, _uiState.value.toCurrency)
    }

    fun onFromCurrencyChanged(currency: Currency) {
        val currentTo = _uiState.value.toCurrency
        // If the user selects the same currency, swap them
        if (currency == currentTo) {
            val newTo = _uiState.value.fromCurrency
            _uiState.update { it.copy(fromCurrency = currency, toCurrency = newTo) }
            loadHistory(currency, newTo)
        } else {
            _uiState.update { it.copy(fromCurrency = currency) }
            loadHistory(currency, currentTo)
        }
    }

    fun onToCurrencyChanged(currency: Currency) {
        val currentFrom = _uiState.value.fromCurrency
        // If the user selects the same currency, swap them
        if (currency == currentFrom) {
            val newFrom = _uiState.value.toCurrency
            _uiState.update { it.copy(toCurrency = currency, fromCurrency = newFrom) }
            loadHistory(newFrom, currency)
        } else {
            _uiState.update { it.copy(toCurrency = currency) }
            loadHistory(currentFrom, currency)
        }
    }

    private fun loadHistory(from: Currency, to: Currency) {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, history = emptyList()) }

            getHistoryUseCase(from, to)
                .onEach { historyData ->
                    Log.d("Load History", "loadHistory: $historyData")
                    _uiState.update {
                        it.copy(
                            history = historyData,
                            isLoading = false
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }
}

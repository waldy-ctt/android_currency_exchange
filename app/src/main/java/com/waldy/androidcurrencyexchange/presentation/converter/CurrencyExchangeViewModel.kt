package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.use_case.GetConversionRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * UI state for the CurrencyExchangeScreen.
 */
data class CurrencyExchangeUiState(
    val fromCurrency: Currency = Currency.USD,
    val toCurrency: Currency = Currency.EUR,
    val fromAmount: String = "1",
    val toAmount: String = "...",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the CurrencyExchangeScreen.
 * It manages the UI state and interacts with the domain layer.
 */
class CurrencyExchangeViewModel(private val getConversionRateUseCase: GetConversionRateUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyExchangeUiState())
    val uiState: StateFlow<CurrencyExchangeUiState> = _uiState.asStateFlow()

    init {
        convert()
    }

    fun onFromAmountChange(amount: String) {
        _uiState.update { it.copy(fromAmount = amount) }
        convert()
    }

    fun onFromCurrencyChange(currency: Currency) {
        _uiState.update { it.copy(fromCurrency = currency) }
        convert()
    }

    fun onToCurrencyChange(currency: Currency) {
        _uiState.update { it.copy(toCurrency = currency) }
        convert()
    }

    fun onSwapCurrencies() {
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency,
                fromAmount = it.toAmount.takeIf { it != "..." } ?: "",
                toAmount = it.fromAmount
            )
        }
        convert()
    }

    private fun convert() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val amount = _uiState.value.fromAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO
                if (amount == BigDecimal.ZERO) {
                    _uiState.update { it.copy(toAmount = "0", isLoading = false) }
                    return@launch
                }

                val result = getConversionRateUseCase(
                    from = _uiState.value.fromCurrency,
                    to = _uiState.value.toCurrency,
                    amount = amount
                )
                _uiState.update { it.copy(toAmount = result.toPlainString(), isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false, toAmount = "") }
            }
        }
    }
}

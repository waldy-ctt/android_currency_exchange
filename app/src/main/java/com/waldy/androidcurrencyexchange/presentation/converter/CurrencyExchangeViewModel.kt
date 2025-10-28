package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.use_case.GetConversionRateUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

/**
 * UI state for the CurrencyExchangeScreen.
 */
data class CurrencyExchangeUiState(
    val fromCurrency: Currency = Currency.USD,
    val toCurrency: Currency = Currency.EUR,
    val fromAmount: String = "1",
    val toAmount: String = "...",
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the CurrencyExchangeScreen.
 * It manages the UI state and interacts with the domain layer.
 */
class CurrencyExchangeViewModel(private val getConversionRateUseCase: GetConversionRateUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyExchangeUiState())
    val uiState: StateFlow<CurrencyExchangeUiState> = _uiState.asStateFlow()

    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    private val symbols = (numberFormat as? DecimalFormat)?.decimalFormatSymbols ?: DecimalFormatSymbols(Locale.US)
    private val decimalSeparator = symbols.decimalSeparator
    private val groupingSeparator = symbols.groupingSeparator
    private var conversionJob: Job? = null
    private var debounceJob: Job? = null

    init {
        convert()
    }

    fun onFromAmountChange(amount: String) {
        _uiState.update { it.copy(fromAmount = sanitizeAmount(amount), toAmount = "...") }

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(300L) // Debounce delay
            convert()
        }
    }

    private fun sanitizeAmount(amount: String): String {
        // Remove grouping separators (e.g., ',' in 1,000) first.
        val sanitized = amount.replace(groupingSeparator.toString(), "")
        // Then, replace the locale-specific decimal separator with a dot for internal consistency.
        val standardizedAmount = sanitized.replace(decimalSeparator, '.')

        val filteredChars = standardizedAmount.filterIndexed { index, c ->
            c.isDigit() || (c == '.' && standardizedAmount.indexOf('.') == index)
        }

        val integerPart = filteredChars.split(".").getOrNull(0) ?: ""
        if (integerPart.length > 12) return _uiState.value.fromAmount

        val decimalPart = filteredChars.split(".").getOrNull(1)
        if (decimalPart != null && decimalPart.length > 2) return _uiState.value.fromAmount

        return when {
            filteredChars.startsWith("0") && !filteredChars.startsWith("0.") && filteredChars.length > 1 ->
                filteredChars.drop(1)
            else -> filteredChars
        }
    }

    fun onFromCurrencyChange(currency: Currency) {
        debounceJob?.cancel()
        _uiState.update { it.copy(fromCurrency = currency) }
        convert()
    }

    fun onToCurrencyChange(currency: Currency) {
        debounceJob?.cancel()
        _uiState.update { it.copy(toCurrency = currency) }
        convert()
    }

    fun onSwapCurrencies() {
        debounceJob?.cancel()
        _uiState.update {
            val currentToAmount = it.toAmount
            val newFromAmount = if (currentToAmount != "...") {
                try {
                    val parsedNumber = numberFormat.parse(currentToAmount)
                    if (parsedNumber != null) BigDecimal(parsedNumber.toString()).toPlainString() else ""
                } catch (e: ParseException) { "" }
            } else { "" }

            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency,
                fromAmount = newFromAmount,
                toAmount = it.fromAmount
            )
        }
        convert()
    }

    private fun convert() {
        conversionJob?.cancel()
        conversionJob = viewModelScope.launch {
            val amount = _uiState.value.fromAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO
            if (_uiState.value.fromAmount.isBlank() || amount == BigDecimal.ZERO) {
                _uiState.update { it.copy(toAmount = "0", isLoading = false) }
                return@launch
            }

            getConversionRateUseCase(
                from = _uiState.value.fromCurrency,
                to = _uiState.value.toCurrency,
                amount = amount
            )
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false, toAmount = "") } }
            .collect { result ->
                val formattedAmount = numberFormat.format(result.convertedAmount)
                _uiState.update { it.copy(toAmount = formattedAmount, isLoading = false, isOffline = result.isOffline) }
            }
        }
    }
}

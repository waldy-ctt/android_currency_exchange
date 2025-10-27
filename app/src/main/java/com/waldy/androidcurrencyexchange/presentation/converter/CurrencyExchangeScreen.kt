package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waldy.androidcurrencyexchange.CurrencyApplication
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.ui.util.StringKeys
import com.waldy.androidcurrencyexchange.ui.util.t

/**
 * This is the stateful entry point for the Currency Converter feature.
 */
@Composable
fun CurrencyExchangeRoute() {
    val application = LocalContext.current.applicationContext as CurrencyApplication
    val container = application.container

    val viewModel: CurrencyExchangeViewModel = viewModel(
        factory = CurrencyExchangeViewModelFactory(container.getConversionRateUseCase)
    )

    val uiState by viewModel.uiState.collectAsState()

    CurrencyExchangeScreen(
        state = uiState,
        onFromAmountChange = viewModel::onFromAmountChange,
        onFromCurrencyChange = viewModel::onFromCurrencyChange,
        onToCurrencyChange = viewModel::onToCurrencyChange,
        onSwapCurrencies = viewModel::onSwapCurrencies
    )
}

/**
 * This is the stateless UI for the Currency Converter.
 */
@Composable
fun CurrencyExchangeScreen(
    state: CurrencyExchangeUiState,
    onFromAmountChange: (String) -> Unit,
    onFromCurrencyChange: (Currency) -> Unit,
    onToCurrencyChange: (Currency) -> Unit,
    onSwapCurrencies: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.fromAmount,
            onValueChange = onFromAmountChange,
            label = { Text(t(StringKeys.FROM_AMOUNT)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        CurrencySelector(state.fromCurrency, onFromCurrencyChange)

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = onSwapCurrencies, enabled = !state.isLoading) {
            Icon(Icons.Default.SwapVert, contentDescription = t(StringKeys.SWAP_CURRENCIES))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = if (state.isLoading) "..." else state.toAmount)
        CurrencySelector(state.toCurrency, onToCurrencyChange)

        state.error?.let {
            Text(
                text = "${t(StringKeys.ERROR_PREFIX)} $it",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun CurrencySelector(selectedCurrency: Currency, onCurrencySelected: (Currency) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val currencyNameKey = "currency_${selectedCurrency.name.lowercase()}"

    Button(onClick = { showDialog = true }) {
        Text(t(currencyNameKey))
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                items(Currency.entries) {
                    val nameKey = "currency_${it.name.lowercase()}"
                    Text(
                        text = t(nameKey),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(it); showDialog = false }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

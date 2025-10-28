package com.waldy.androidcurrencyexchange.presentation.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waldy.androidcurrencyexchange.CurrencyApplication
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.ui.util.StringKeys
import com.waldy.androidcurrencyexchange.ui.util.t

/**
 * Stateful entry point for the Currency Converter feature.
 */
@Composable
fun CurrencyExchangeRoute() {
    val application = LocalContext.current.applicationContext as CurrencyApplication
    val viewModel: CurrencyExchangeViewModel = viewModel(
        factory = CurrencyExchangeViewModelFactory(application.container.getConversionRateUseCase)
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
 * Stateless UI for the Currency Converter.
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
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isOffline) {
            OfflineBadge()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // "From" currency row
        CurrencyRow(
            amount = state.fromAmount,
            onAmountChange = onFromAmountChange,
            selectedCurrency = state.fromCurrency,
            onCurrencySelected = onFromCurrencyChange,
            isEditable = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Swap button
        IconButton(onClick = onSwapCurrencies, enabled = !state.isLoading) {
            Icon(Icons.Default.SwapVert, contentDescription = t(StringKeys.SWAP_CURRENCIES), modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "To" currency row (read-only)
        CurrencyRow(
            amount = if (state.isLoading) "..." else state.toAmount,
            onAmountChange = {}, // No-op
            selectedCurrency = state.toCurrency,
            onCurrencySelected = onToCurrencyChange,
            isEditable = false
        )

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
private fun CurrencyRow(
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit,
    isEditable: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
            enabled = isEditable,
            readOnly = !isEditable,
            visualTransformation = if (isEditable) ThousandSeparatorVisualTransformation() else VisualTransformation.None
        )

        Spacer(modifier = Modifier.width(16.dp))

        CurrencySelector(selectedCurrency, onCurrencySelected)
    }
}

@Composable
private fun CurrencySelector(selectedCurrency: Currency, onCurrencySelected: (Currency) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.clickable { showDialog = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(selectedCurrency.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select currency")
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                LazyColumn {
                    items(Currency.entries) {
                        val nameKey = "currency_${it.name.lowercase()}"
                        Text(
                            text = "${it.name} - ${t(nameKey)}",
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
}

@Composable
private fun OfflineBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = t(StringKeys.OFFLINE_MODE),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

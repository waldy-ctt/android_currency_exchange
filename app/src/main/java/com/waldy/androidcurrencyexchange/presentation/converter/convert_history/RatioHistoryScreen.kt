package com.waldy.androidcurrencyexchange.presentation.converter.convert_history

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waldy.androidcurrencyexchange.CurrencyApplication
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyHistory
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.ui.util.StringKeys
import com.waldy.androidcurrencyexchange.ui.util.t

@Composable
fun RatioHistoryScreen() {
    val appContainer = (LocalContext.current.applicationContext as CurrencyApplication).container
    val viewModel: RatioHistoryViewModel = viewModel(
        factory = RatioHistoryViewModelFactory(appContainer.getHistoryUseCase)
    )
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        CurrencySelectorRow(uiState, viewModel)

        Spacer(modifier = Modifier.height(10.dp))

        // bang lich su nay hoi lon xon. can xem xet thiet ke lai
        HistoryTable(uiState)
    }
}

@Composable
private fun CurrencySelectorRow(uiState: RatioHistoryUiState, viewModel: RatioHistoryViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HistoryRatioCurrencySelector(
            selectedCurrency = uiState.fromCurrency,
            onCurrencySelected = viewModel::onFromCurrencyChanged
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "to",
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        HistoryRatioCurrencySelector(
            selectedCurrency = uiState.toCurrency,
            onCurrencySelected = viewModel::onToCurrencyChanged
        )
    }
}

@Composable
private fun ColumnScope.HistoryTable(uiState: RatioHistoryUiState) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .border(
                color = MaterialTheme.colorScheme.outline,
                width = 1.dp,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        if (uiState.isLoading && uiState.history.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (!uiState.isLoading && uiState.history.isEmpty()) {
            Text(
                text = t(StringKeys.NO_DATA),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                item {
                    HistoryTableHeader()
                }
                items(uiState.history) { historyItem ->
                    HistoryTableRow(historyItem)
                }
            }
        }
    }
}

@Composable
private fun HistoryTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = t(StringKeys.DATE),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = t(StringKeys.RATIO),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider()
}

@Composable
private fun HistoryTableRow(historyItem: CurrencyHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = historyItem.date,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "%.2f".format(historyItem.ratio),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider()
}

@Composable
private fun HistoryRatioCurrencySelector(
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clickable { showDialog = true }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            selectedCurrency.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select currency")
    }

    if (showDialog) {
        CurrencySelectionDialog(onCurrencySelected) { showDialog = false }
    }
}

@Composable
private fun CurrencySelectionDialog(onCurrencySelected: (Currency) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn {
                items(Currency.entries) { currency ->
                    val nameKey = "currency_${currency.name.lowercase()}"
                    Text(
                        text = "${currency.name} - ${t(nameKey)}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(currency); onDismiss() }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

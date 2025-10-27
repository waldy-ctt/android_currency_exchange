import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.waldy.androidcurrencyexchange.shared.CurrencyList
import com.waldy.androidcurrencyexchange.ui.theme.AndroidCurrencyExchangeTheme

/**
 * Represents the UI state for the CurrencyExchangeScreen.
 * This makes the Composable function signature clean and easy to manage.
 */
data class CurrencyExchangeState(
    val fromAmount: String = "1",
    val toAmount: String = "",
    val fromCurrency: CurrencyList = CurrencyList.USD,
    val toCurrency: CurrencyList = CurrencyList.VND
)

/**
 * Defines the events (user actions) that can be triggered from the UI.
 * This is how the View communicates with the ViewModel.
 */
interface CurrencyExchangeEvents {
    fun onFromAmountChange(amount: String)
    fun onToAmountChange(amount: String) // Added for completeness, though the field is read-only
    fun onFromCurrencyChange(currency: CurrencyList)
    fun onToCurrencyChange(currency: CurrencyList)
}

/**
 * This is the main UI for the currency exchange feature.
 * It is completely stateless and relies on the state and events provided to it.
 * IT CONTAINS NO LOGIC.
 */
@Composable
fun CurrencyExchangeScreen(
    state: CurrencyExchangeState,
    events: CurrencyExchangeEvents
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        // "From" currency input block
        CurrencyInputCard(
            amount = state.fromAmount,
            onAmountChange = events::onFromAmountChange,
            selectedCurrency = state.fromCurrency,
            onCurrencySelected = events::onFromCurrencyChange,
            // Exclude the 'to' currency from this dropdown
            excludedCurrency = state.toCurrency
        )

        // "To" currency input block (read-only)
        CurrencyDisplayCard(
            amount = state.toAmount,
            selectedCurrency = state.toCurrency,
            onCurrencySelected = events::onToCurrencyChange,
            // Exclude the 'from' currency from this dropdown
            excludedCurrency = state.fromCurrency
        )
    }
}

@Composable
fun CurrencyInputCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedCurrency: CurrencyList,
    onCurrencySelected: (CurrencyList) -> Unit,
    excludedCurrency: CurrencyList
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.width(16.dp))
            CurrencySelector(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = onCurrencySelected,
                excludedCurrency = excludedCurrency
            )
        }
    }
}

@Composable
fun CurrencyDisplayCard(
    amount: String,
    selectedCurrency: CurrencyList,
    onCurrencySelected: (CurrencyList) -> Unit,
    excludedCurrency: CurrencyList
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = amount,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.width(16.dp))
            CurrencySelector(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = onCurrencySelected,
                excludedCurrency = excludedCurrency
            )
        }
    }
}

@Composable
fun CurrencySelector(
    selectedCurrency: CurrencyList,
    onCurrencySelected: (CurrencyList) -> Unit,
    excludedCurrency: CurrencyList? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable { showDialog = true }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = selectedCurrency.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Currency",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDialog) {
        CurrencySelectionDialog(
            onDismiss = { showDialog = false },
            onCurrencySelected = onCurrencySelected,
            excludedCurrency = excludedCurrency
        )
    }
}

@Composable
fun CurrencySelectionDialog(
    onDismiss: () -> Unit,
    onCurrencySelected: (CurrencyList) -> Unit,
    excludedCurrency: CurrencyList?
) {
    val currencies = remember { CurrencyList.entries.filter { it != excludedCurrency } }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp), // Set a max height for the dialog
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Currency", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Clear, contentDescription = "Close")
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    items(currencies) { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCurrencySelected(currency)
                                    onDismiss()
                                }
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currency.name,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * A Preview function to visualize the stateless UI.
 * We create a fake state and empty events to render it.
 */
@Preview(showBackground = true)
@Composable
fun CurrencyExchangeScreenPreview() {
    AndroidCurrencyExchangeTheme {
        val fakeState = CurrencyExchangeState(fromAmount = "1", toAmount = "25,400.00")
        val fakeEvents = object : CurrencyExchangeEvents {
            override fun onFromAmountChange(amount: String) {}
            override fun onToAmountChange(amount: String) {}
            override fun onFromCurrencyChange(currency: CurrencyList) {}
            override fun onToCurrencyChange(currency: CurrencyList) {}
        }
        CurrencyExchangeScreen(state = fakeState, events = fakeEvents)
    }
}
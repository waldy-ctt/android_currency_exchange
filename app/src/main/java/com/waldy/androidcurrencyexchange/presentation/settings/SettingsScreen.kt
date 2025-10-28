package com.waldy.androidcurrencyexchange.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.waldy.androidcurrencyexchange.ui.util.Language
import com.waldy.androidcurrencyexchange.ui.util.StringKeys
import com.waldy.androidcurrencyexchange.ui.util.t

@Composable
fun SettingsScreen(
    theme: String,
    language: Language,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (Language) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // --- General Section ---
        SettingsGroup(title = t(StringKeys.GENERAL)) {
            ThemeSelector(currentTheme = theme, onThemeChange = onThemeChange)
            Divider()
            LanguageSelector(currentLanguage = language, onLanguageChange = onLanguageChange)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Feedback Section ---
        SettingsGroup(title = t(StringKeys.FEEDBACK)) {
            FeedbackButton(context = context)
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- About Section ---
        AboutSection(context = context)
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ThemeSelector(currentTheme: String, onThemeChange: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val themes = mapOf("Light" to t(StringKeys.LIGHT), "Dark" to t(StringKeys.DARK), "Device" to t(StringKeys.DEVICE))

    SettingsItem(title = t(StringKeys.THEME), value = themes[currentTheme] ?: currentTheme) {
        showDialog = true
    }

    if (showDialog) {
        SelectionDialog(
            title = t(StringKeys.CHOOSE_THEME),
            options = themes.values.toList(),
            onSelect = { selected -> onThemeChange(themes.entries.first { it.value == selected }.key) },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun LanguageSelector(currentLanguage: Language, onLanguageChange: (Language) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val languages = mapOf(Language.ENGLISH to t(StringKeys.ENGLISH), Language.VIETNAMESE to t(StringKeys.VIETNAMESE))

    SettingsItem(title = t(StringKeys.LANGUAGE), value = languages[currentLanguage] ?: currentLanguage.name) {
        showDialog = true
    }

    if (showDialog) {
        SelectionDialog(
            title = t(StringKeys.CHOOSE_LANGUAGE),
            options = languages.values.toList(),
            onSelect = { selected -> onLanguageChange(languages.entries.first { it.value == selected }.key) },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun FeedbackButton(context: Context) {
    SettingsItem(title = t(StringKeys.SEND_FEEDBACK), value = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("lethanhhieu.dev@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}

@Composable
private fun AboutSection(context: Context) {
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: Exception) {
        "1.0"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${t(StringKeys.VERSION)} $versionName",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = t(StringKeys.AUTHOR_WALDY),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = t(StringKeys.AUTHOR_HAU),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SelectionDialog(title: String, options: List<String>, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                options.forEach {
                    TextButton(onClick = { onSelect(it); onDismiss() }) {
                        Text(it, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

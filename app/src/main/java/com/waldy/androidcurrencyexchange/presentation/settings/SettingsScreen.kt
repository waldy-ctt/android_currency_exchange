package com.waldy.androidcurrencyexchange.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.waldy.androidcurrencyexchange.ui.util.Language
import com.waldy.androidcurrencyexchange.ui.util.StringKeys
import com.waldy.androidcurrencyexchange.ui.util.t

/**
 * The stateless UI for the Settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    theme: String,
    language: Language,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(t(StringKeys.SETTINGS)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t(StringKeys.BACK))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            ThemeSettings(currentTheme = theme, onThemeChange = onThemeChange)
            LanguageSettings(currentLanguage = language, onLanguageChange = onLanguageChange)
        }
    }
}

@Composable
private fun ThemeSettings(currentTheme: String, onThemeChange: (String) -> Unit) {
    val themes = listOf("Light", "Dark", "Device")
    SettingsGroup(title = t(StringKeys.THEME)) {
        themes.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeChange(theme) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentTheme == theme,
                    onClick = { onThemeChange(theme) }
                )
                Text(text = t(theme.lowercase()), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun LanguageSettings(currentLanguage: Language, onLanguageChange: (Language) -> Unit) {
    val languages = Language.entries
    SettingsGroup(title = t(StringKeys.LANGUAGE)) {
        languages.forEach { language ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLanguageChange(language) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentLanguage == language,
                    onClick = { onLanguageChange(language) }
                )
                Text(text = t(language.name.lowercase()), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content()
            }
        }
    }
}

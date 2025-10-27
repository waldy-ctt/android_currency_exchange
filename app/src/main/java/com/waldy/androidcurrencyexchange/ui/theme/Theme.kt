package com.waldy.androidcurrencyexchange.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Updated Dark Theme - Espresso & Cream Aesthetic
private val DarkColorScheme = darkColorScheme(
    primary = LightTan,
    onPrimary = OnLightTan,
    background = DarkEspresso,
    surface = DarkEspresso,
    onSurface = OffWhiteText,
    surfaceVariant = DarkCream,
    onSurfaceVariant = OffWhiteText,
    secondary = DarkCream, // Assigning a sensible default
    onSecondary = OffWhiteText,
    tertiary = LightTan, // Assigning a sensible default
    onTertiary = OnLightTan
)

// Updated Light Theme - Milk Coffee Aesthetic
private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    onPrimary = OnCoffeeBrown,
    background = CreamyBeige,
    surface = CreamyBeige,
    onSurface = DarkRoast,
    surfaceVariant = MilkyWhite,
    onSurfaceVariant = DarkRoast,
    secondary = LatteFoam, // Assigning a sensible default
    onSecondary = DarkRoast,
    tertiary = LatteFoam, // Assigning a sensible default
    onTertiary = DarkRoast
)

@Composable
fun AndroidCurrencyExchangeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Let's disable dynamic color by default to enforce our custom theme.
    // You can set it to `true` if you want to allow system colors on Android 12+.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Use background for status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

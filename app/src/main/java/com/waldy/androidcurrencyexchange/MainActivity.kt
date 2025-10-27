package com.waldy.androidcurrencyexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.waldy.androidcurrencyexchange.MainActivityScreen
import com.waldy.androidcurrencyexchange.api.AppApiService
import com.waldy.androidcurrencyexchange.ui.theme.AndroidCurrencyExchangeTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This is the main entry point of your application.
 * Its responsibilities are:
 * 1. Handle Android lifecycle events.
 * 2. Initialize app-wide dependencies (like API services).
 * 3. Set the root Composable UI.
 */
class MainActivity: ComponentActivity() {

    // You can keep the instance here for now.
    // In a more advanced setup, this would be handled by a dependency injection library like Hilt.
    lateinit var appApiServiceInstance: AppApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // --- Logic and Initialization ---
        initializeApiService()

        // --- Setting the UI ---
        // setContent is the bridge between the Activity and Jetpack Compose.
        // It hosts your Composable UI.
        setContent {
            // Here, you just call your top-level screen Composable.
            // You could also wrap it in your app's theme.
            // AppTheme {
            AndroidCurrencyExchangeTheme {
                MainActivityScreen()
            }
            // }
        }
    }

    private fun initializeApiService() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        appApiServiceInstance = retrofit.create(AppApiService::class.java)
    }
}
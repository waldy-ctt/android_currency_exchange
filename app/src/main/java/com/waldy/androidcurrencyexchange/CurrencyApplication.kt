package com.waldy.androidcurrencyexchange

import android.app.Application
import com.waldy.androidcurrencyexchange.di.AppContainer

/**
 * Custom Application class to hold the application-wide dependency container.
 */
class CurrencyApplication : Application() {

    // The single, app-wide instance of our DI container.
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Initialize the container with the application context.
        container = AppContainer(this)
    }
}

package com.waldy.androidcurrencyexchange.data.local

import android.content.Context
import com.waldy.androidcurrencyexchange.domain.model.Currency

/**
 * A simple cache to store the latest successful API response for each currency.
 * This uses SharedPreferences for simplicity.
 */
class CurrencyCache(context: Context) {

    private val prefs = context.getSharedPreferences("currency_cache", Context.MODE_PRIVATE)

    /**
     * Saves the raw JSON string response for a given base currency.
     */
    fun saveRates(from: Currency, jsonString: String) {
        prefs.edit().putString(from.name, jsonString).apply()
    }

    /**
     * Retrieves the cached JSON string for a given base currency, if it exists.
     */
    fun getRates(from: Currency): String? {
        return prefs.getString(from.name, null)
    }
}

package com.waldy.androidcurrencyexchange.data.db.model

import androidx.room.Entity

@Entity(tableName = "currency_history", primaryKeys = ["baseCurrency", "targetCurrency", "date"])
data class CurrencyHistory(
    val baseCurrency: String,
    val targetCurrency: String,
    val date: String, // Stored in "YYYY-MM-DD" format
    val ratio: Double
)

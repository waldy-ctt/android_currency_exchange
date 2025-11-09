package com.waldy.androidcurrencyexchange.data.db.model

import androidx.room.Entity

@Entity(tableName = "currency_offline", primaryKeys = ["baseCurrency", "targetCurrency"])
data class CurrencyOffline (
    val baseCurrency: String,
    val targetCurrency: String,
    val ratio: Double,
    val timestamp: Long // In milliseconds 
)

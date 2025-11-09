package com.waldy.androidcurrencyexchange.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1, // Use a fixed ID for a single row of settings
    val theme: String,
    val language: String
)

package com.waldy.androidcurrencyexchange.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(history: List<CurrencyHistory>)

    @Query("SELECT * FROM currency_history WHERE baseCurrency = :baseCurrency AND targetCurrency = :targetCurrency ORDER BY date DESC")
    fun getHistory(baseCurrency: String, targetCurrency: String): Flow<List<CurrencyHistory>>

    @Query("DELETE FROM currency_history WHERE date < :date")
    fun clearHistoryData(date: String) // date in YYYY-MM-DD
}

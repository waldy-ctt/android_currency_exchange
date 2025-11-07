package com.waldy.androidcurrencyexchange.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waldy.androidcurrencyexchange.data.db.model.CurrencyOffline
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyOfflineDao {
    @Query("SELECT * FROM currency_offline WHERE baseCurrency = :baseCurrency AND targetCurrency = :targetCurrency")
    fun getCurrencyRatio(baseCurrency: String, targetCurrency: String): Flow<CurrencyOffline?>
       
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rates: List<CurrencyOffline>)
}

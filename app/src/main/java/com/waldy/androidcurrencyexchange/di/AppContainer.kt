package com.waldy.androidcurrencyexchange.di

import android.content.Context
import com.waldy.androidcurrencyexchange.common.NetworkManager
import com.waldy.androidcurrencyexchange.data.db.AppDatabase
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyHistoryDao
import com.waldy.androidcurrencyexchange.data.db.dao.CurrencyOfflineDao
import com.waldy.androidcurrencyexchange.data.preferences.UserPreferencesRepository
import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.data.repository.CurrencyRepositoryImpl
import com.waldy.androidcurrencyexchange.domain.model.Currency
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import com.waldy.androidcurrencyexchange.domain.use_case.GetConversionRateUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetHasHistoryUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetHistoryUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetThemeUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateThemeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple, manual dependency injection container.
 */
class AppContainer(private val context: Context) {

    // --- Network Dependencies ---
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://cdn.jsdelivr.net/npm/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val currencyApiService: CurrencyApiService = retrofit.create(CurrencyApiService::class.java)
    private val networkManager = NetworkManager(context)

    // --- Local Data Dependencies ---
    private val appDatabase: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    private val currencyOfflineDao: CurrencyOfflineDao by lazy { appDatabase.currencyOfflineDao() }
    private val currencyHistoryDao: CurrencyHistoryDao by lazy { appDatabase.currencyHistoryDao() }
    private val userPreferencesRepository: UserPreferencesRepository by lazy { UserPreferencesRepository(appDatabase.userPreferencesDao()) }

    // --- Repository Dependencies ---
    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(currencyApiService, currencyOfflineDao, currencyHistoryDao, networkManager)

    init {
        // On app start, check if we need to perform an initial data fetch.
        // This is done in the AppContainer's init to decouple it from the UI lifecycle.
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            // If the database is empty, trigger a fetch for a default currency pair.
            if (currencyRepository.hasHistory().first() == 0) {
                // By collecting the flow once, we trigger the .onStart block in the repository
                // which contains the data fetching logic.
                currencyRepository.getHistory(Currency.USD, Currency.VND).first()
            }
        }
    }

    // --- Use Case Dependencies ---
    val getConversionRateUseCase: GetConversionRateUseCase
        get() = GetConversionRateUseCase(currencyRepository)

    val getHistoryUseCase: GetHistoryUseCase
        get() = GetHistoryUseCase(currencyRepository)

    val getHasHistoryUseCase: GetHasHistoryUseCase
        get() = GetHasHistoryUseCase(currencyRepository)

    val getThemeUseCase: GetThemeUseCase
        get() = GetThemeUseCase(userPreferencesRepository)

    val getLanguageUseCase: GetLanguageUseCase
        get() = GetLanguageUseCase(userPreferencesRepository)

    val updateThemeUseCase: UpdateThemeUseCase
        get() = UpdateThemeUseCase(userPreferencesRepository)

    val updateLanguageUseCase: UpdateLanguageUseCase
        get() = UpdateLanguageUseCase(userPreferencesRepository)
}

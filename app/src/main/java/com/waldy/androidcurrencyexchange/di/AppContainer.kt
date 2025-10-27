package com.waldy.androidcurrencyexchange.di

import android.content.Context
import com.waldy.androidcurrencyexchange.data.preferences.UserPreferencesRepository
import com.waldy.androidcurrencyexchange.data.remote.CurrencyApiService
import com.waldy.androidcurrencyexchange.data.repository.CurrencyRepositoryImpl
import com.waldy.androidcurrencyexchange.domain.repository.CurrencyRepository
import com.waldy.androidcurrencyexchange.domain.use_case.GetConversionRateUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.GetThemeUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateLanguageUseCase
import com.waldy.androidcurrencyexchange.domain.use_case.UpdateThemeUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple, manual dependency injection container.
 */
class AppContainer(private val context: Context) {

    // --- Network Dependencies ---
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val currencyApiService: CurrencyApiService = retrofit.create(CurrencyApiService::class.java)

    // --- Repository Dependencies ---
    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(currencyApiService)
    private val userPreferencesRepository: UserPreferencesRepository = UserPreferencesRepository(context)

    // --- Use Case Dependencies ---
    val getConversionRateUseCase: GetConversionRateUseCase
        get() = GetConversionRateUseCase(currencyRepository)

    val getThemeUseCase: GetThemeUseCase
        get() = GetThemeUseCase(userPreferencesRepository)

    val getLanguageUseCase: GetLanguageUseCase
        get() = GetLanguageUseCase(userPreferencesRepository)

    val updateThemeUseCase: UpdateThemeUseCase
        get() = UpdateThemeUseCase(userPreferencesRepository)

    val updateLanguageUseCase: UpdateLanguageUseCase
        get() = UpdateLanguageUseCase(userPreferencesRepository)
}

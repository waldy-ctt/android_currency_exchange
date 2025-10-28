package com.waldy.androidcurrencyexchange.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * A singleton object that holds all translations and manages the current language.
 */
object Localization {

    // The current language, wrapped in a CompositionLocal to trigger recomposition on change.
    val currentLanguage = staticCompositionLocalOf { Language.ENGLISH }

    private val strings = mapOf(
        // --- English Strings ---
        Language.ENGLISH to mapOf(
            StringKeys.APP_NAME to "Currency Exchange",
            StringKeys.BACK to "Back",
            StringKeys.GENERAL to "General",
            StringKeys.FEEDBACK to "Feedback",
            StringKeys.SETTINGS to "Settings",
            StringKeys.OFFLINE_MODE to "Offline Mode",
            StringKeys.CURRENCY_CONVERTER to "Converter",
            StringKeys.FROM_AMOUNT to "From Amount",
            StringKeys.SWAP_CURRENCIES to "Swap currencies",
            StringKeys.ERROR_PREFIX to "Error:",
            StringKeys.THEME to "Theme",
            StringKeys.LIGHT to "Light",
            StringKeys.DARK to "Dark",
            StringKeys.DEVICE to "Device",
            StringKeys.CHOOSE_THEME to "Choose Theme",
            StringKeys.LANGUAGE to "Language",
            StringKeys.CHOOSE_LANGUAGE to "Choose Language",
            StringKeys.SEND_FEEDBACK to "Send Feedback",
            StringKeys.REPORT_ISSUES to "Report issues or suggest features",
            StringKeys.VERSION to "Version",
            StringKeys.AUTHOR_WALDY to "Le Thanh Hieu - Waldy",
            StringKeys.AUTHOR_HAU to "Nguyen Phuc Hau",
            StringKeys.ENGLISH to "English",
            StringKeys.VIETNAMESE to "Vietnamese",
            StringKeys.CURRENCY_USD to "United States Dollar",
            StringKeys.CURRENCY_EUR to "Euro",
            StringKeys.CURRENCY_JPY to "Japanese Yen",
            StringKeys.CURRENCY_GBP to "Great British Pound",
            StringKeys.CURRENCY_AUD to "Australian Dollar",
            StringKeys.CURRENCY_CAD to "Canadian Dollar",
            StringKeys.CURRENCY_CHF to "Swiss Franc",
            StringKeys.CURRENCY_CNY to "Chinese Yuan",
            StringKeys.CURRENCY_VND to "Vietnamese Dong",
            StringKeys.CURRENCY_KRW to "Korean Won"
            ),
        // --- Vietnamese Strings ---
        Language.VIETNAMESE to mapOf(
            StringKeys.APP_NAME to "Chuyển đổi tiền tệ",
            StringKeys.BACK to "Trở lại",
            StringKeys.GENERAL to "Chung",
            StringKeys.FEEDBACK to "Phản hồi",
            StringKeys.SETTINGS to "Cài đặt",
            StringKeys.OFFLINE_MODE to "Chế độ ngoại tuyến",
            StringKeys.CURRENCY_CONVERTER to "Chuyển đổi",
            StringKeys.FROM_AMOUNT to "Số tiền gửi",
            StringKeys.SWAP_CURRENCIES to "Hoán đổi tiền tệ",
            StringKeys.ERROR_PREFIX to "Lỗi:",
            StringKeys.THEME to "Chủ đề",
            StringKeys.LIGHT to "Sáng",
            StringKeys.DARK to "Tối",
            StringKeys.DEVICE to "Thiết bị",
            StringKeys.CHOOSE_THEME to "Chọn chủ đề",
            StringKeys.LANGUAGE to "Ngôn ngữ",
            StringKeys.CHOOSE_LANGUAGE to "Chọn ngôn ngữ",
            StringKeys.SEND_FEEDBACK to "Gửi phản hồi",
            StringKeys.REPORT_ISSUES to "Báo cáo sự cố hoặc đề xuất tính năng",
            StringKeys.VERSION to "Phiên bản",
            StringKeys.AUTHOR_WALDY to "Lê Thanh Hiếu - Waldy",
            StringKeys.AUTHOR_HAU to "Nguyễn Phúc Hậu",
            StringKeys.ENGLISH to "Tiếng Anh",
            StringKeys.VIETNAMESE to "Tiếng Việt",
            StringKeys.CURRENCY_USD to "Đô la Mỹ",
            StringKeys.CURRENCY_EUR to "Euro",
            StringKeys.CURRENCY_JPY to "Yên Nhật",
            StringKeys.CURRENCY_GBP to "Bảng Anh",
            StringKeys.CURRENCY_AUD to "Đô la Úc",
            StringKeys.CURRENCY_CAD to "Đô la Canada",
            StringKeys.CURRENCY_CHF to "Franc Thụy Sĩ",
            StringKeys.CURRENCY_CNY to "Nhân dân tệ Trung Quốc",
            StringKeys.CURRENCY_VND to "Việt Nam Đồng",
            StringKeys.CURRENCY_KRW to "Won Hàn Quốc"
            )
    )

    /**
     * Retrieves a string for the current language.
     *
     * @param key The [StringKeys] identifier for the string.
     * @return The localized string.
     */
    fun getString(language: Language, key: String): String {
        return strings[language]?.get(key) ?: strings[Language.ENGLISH]?.get(key) ?: ""
    }
}

/**
 * A composable function to get a translated string reactively.
 * It uses the [Localization.currentLanguage] to recompose whenever the language changes.
 */
@Composable
fun t(key: String): String {
    val lang = Localization.currentLanguage.current
    return Localization.getString(lang, key)
}

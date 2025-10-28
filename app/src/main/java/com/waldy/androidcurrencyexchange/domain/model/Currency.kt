package com.waldy.androidcurrencyexchange.domain.model

/**
 * Represents the currencies supported by the application.
 * Using an enum provides type safety and a single source of truth for currency information.
 */
enum class Currency(val fullName: String) {
    USD("United States Dollar"),
    EUR("Euro"),
    JPY("Japanese Yen"),
    GBP("Great British Pound"),
    AUD("Australian Dollar"),
    CAD("Canadian Dollar"),
    CHF("Swiss Franc"),
    CNY("Chinese Yuan"),
    VND("Vietnamese Dong"),
    KRW("Korean Won")
}

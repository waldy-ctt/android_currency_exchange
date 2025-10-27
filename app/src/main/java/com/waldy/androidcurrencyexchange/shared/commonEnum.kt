package com.waldy.androidcurrencyexchange.shared

enum class commonEnum {

}
enum class CurrencyList(fullname: String) {
    USD("US Dollar"),
    EUR("Euro"),
    JPY("Japanese Yen"),
    GBP("British Pound"),
    AUD("Australian Dollar"),
    CAD("Canadian Dollar"),
    CHF("Swiss Franc"),
    CNY("Chinese Yuan"),
    HKD("Hong Kong Dollar"),
    NZD("New Zealand Dollar"),
    RUB("Russian Ruble"),
    SGD("Singapore Dollar"),
    SEK("Swedish Krona"),
    KRW("South Korean Won"),
    INR("Indian Rupee"),
    MXN("Mexican Peso"),
    BRL("Brazilian Real"),
    TRY("Turkish Lira"),
    ZAR("South African Rand"),
    VND("Viet Nam Dong");

    fun getCode(): String {
        return this.name;
    }
}

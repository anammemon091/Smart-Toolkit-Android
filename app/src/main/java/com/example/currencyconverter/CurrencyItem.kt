package com.example.currencyconverter

data class CurrencyItem(
    val code: String,      // e.g., "USD"
    val flagUrl: String    // URL of the flag image
)
val currencyList = listOf(
    CurrencyItem("USD", "https://flagsapi.com/US/flat/64.png"),
    CurrencyItem("PKR", "https://flagsapi.com/PK/flat/64.png"),
    CurrencyItem("EUR", "https://flagsapi.com/EU/flat/64.png")
)
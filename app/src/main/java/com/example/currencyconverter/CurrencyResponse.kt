package com.example.currencyconverter

data class CurrencyResponse(
    val result: String,
    val rates: Map<String, Double>
)
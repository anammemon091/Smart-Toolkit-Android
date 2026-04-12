package com.example.currencyconverter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("latest/{base}")
    fun getRates(@Path("base") base: String): Call<CurrencyResponse>
}
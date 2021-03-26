package com.example.stocks.api

import com.example.stocks.service.StockService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api() {

    val BASE_URL = "https://finnhub.io"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val listStockService: StockService = retrofit.create(StockService::class.java)

}
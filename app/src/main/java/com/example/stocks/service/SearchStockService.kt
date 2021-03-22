package com.example.stocks.service

import com.example.stocks.BuildConfig
import com.example.stocks.model.ListStocks
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchStockService {

        @GET("/api/v1/search?token=${BuildConfig.CLUCH}")
        fun getQueryStock(
            @Query("q")
            query: String
        ): Call<ListStocks>
}
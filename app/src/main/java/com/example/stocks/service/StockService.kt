package com.example.stocks.service

import com.example.stocks.BuildConfig
import com.example.stocks.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockService {

    @GET("/api/v1/stock/profile2?token=${BuildConfig.CLUCH}")
    fun getStock(
        @Query("symbol")
        symbol: String
    ) : Call<Stock>

    @GET("api/v1/quote?token=${BuildConfig.CLUCH}")
    fun getStockPrice(
        @Query("symbol")
        symbol: String
    ) : Call<Quote>

    @GET("/api/v1/search?token=${BuildConfig.CLUCH}")
    fun getQueryStock(
            @Query("q")
            query: String
    ): Call<ListStocks>

    @GET("/api/v1/stock/candle?token=${BuildConfig.CLUCH}")
    fun getChart(
        @Query("symbol")
        symbol: String,
        @Query("resolution")
        resolution: String,
        @Query("from")
        from: String,
        @Query("to")
        to: String
    ) : Call<ChartStock>

    @GET("/api/v1/company-news?token=${BuildConfig.CLUCH}")
    fun getCompanyNews(
            @Query("symbol")
            symbol: String,
            @Query("from")
            from: String,
            @Query("to")
            to: String
    ) : Call<CompanyNews>

}
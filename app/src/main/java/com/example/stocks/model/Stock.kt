package com.example.stocks.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_stock")
data class Stock(
    val country: String,
    val currency: String,
    val exchange: String,
    val finnhubIndustry: String,
    val ipo: String,
    val logo: String,
    val marketCapitalization: Double,
    val name: String,
    val phone: String,
    val shareOutstanding: Double,
    @PrimaryKey
    val ticker: String,
    val weburl: String,
    val favorites: Boolean = false,
    val currentPrice: Double = 0.0,
    val openPriceOfTheDay: Double = 0.0


)

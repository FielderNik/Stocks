package com.example.stocks.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stocks.model.Stock


@Dao
interface DaoStock {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStock(stock: Stock)

    @Query("SELECT * FROM table_stock ORDER BY ticker ASC")
    fun readAllData(): LiveData<List<Stock>>

    @Query("SELECT * FROM table_stock WHERE favorites = :f")
    fun getFavoritesStocks(f: Boolean): LiveData<List<Stock>>

    @Query("UPDATE table_stock SET favorites = :value WHERE ticker = :ticker")
    fun addToFavoritesList(ticker: String, value: Boolean)

    @Query("UPDATE table_stock SET favorites = :value WHERE ticker = :ticker")
    fun removeStockFromFavoritesList(ticker: String, value: Boolean)

    @Query("UPDATE table_stock SET favorites = :value WHERE ticker = :ticker")
    fun switchFavorites(ticker: String, value: Boolean)

    @Query("SELECT * FROM table_stock WHERE ticker = :ticker")
    suspend fun getStock(ticker: String) : Stock

    @Query("SELECT * FROM table_stock WHERE ticker = :ticker")
    fun getLiveDataStock(ticker: String) : LiveData<Stock>

    @Query("UPDATE table_stock SET currentPrice = :currentPrice WHERE ticker = :ticker")
    fun setCurrentPriceToStock(ticker: String, currentPrice: Double)

    @Query("UPDATE table_stock SET openPriceOfTheDay = :openPrice WHERE ticker = :ticker")
    fun setOpenPriceToStock(ticker: String, openPrice: Double)

    @Query("SELECT * FROM table_stock")
    suspend fun getStockListFromDatabase(): List<Stock>
}
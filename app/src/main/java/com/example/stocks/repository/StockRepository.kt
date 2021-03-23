package com.example.stocks.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.example.stocks.api.Api
import com.example.stocks.data.DaoStock
import com.example.stocks.model.Quote
import com.example.stocks.model.Stock
import com.example.stocks.service.StockService
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class StockRepository(private val daoStock: DaoStock) {

    val readAllData: LiveData<List<Stock>> = daoStock.readAllData()
    val readFavoritesStock: LiveData<List<Stock>> = daoStock.getFavoritesStocks(true)

    val api = Api()
    val stockFromApiService: StockService = api.retrofit.create(StockService::class.java)

    suspend fun addStock(stock: Stock){
            daoStock.addStock(stock)
    }

    suspend fun addStockToFavoritesList(ticker: String){
        daoStock.switchFavorites(ticker, true)
    }

    suspend fun removeStockFromFavoritesList(ticker: String){
        daoStock.switchFavorites(ticker, false)
    }

    suspend fun getStock(ticker: String): Stock{
        return daoStock.getStock(ticker)
    }

    fun getLiveDataStock(ticker: String): LiveData<Stock>{
        CoroutineScope(Dispatchers.IO).launch {
            if (getStock(ticker) == null){
                val stockFromApi = getStockFromApi(ticker) //получаем акцию из АПИ
                addStock(stockFromApi) // записали в бд
            }
            val quote = getStockPriceFromApi(ticker) // получаем квот из АПИ
            setCurrentPriceToStock(ticker, quote.c)
            setOpenPriceToStock(ticker, quote.o)
        }

        return daoStock.getLiveDataStock(ticker)
    }

    suspend fun checkAndAddStock(stock: Stock){

        try {
            val stockFromDatabase = getStock(stock.ticker)

            Log.d("milk", "Stock is in Database $stockFromDatabase")
        } catch (e: Exception) {
            Log.d("milk", "Database have not this stock")
        }


    }

    fun getStockFromApi(ticker: String): Stock{
        return stockFromApiService.getStock(ticker).execute().body()!! //TODO (приложение крашится из за екзекьюте. Изменить на енкью?)
    }

    fun getStockPriceFromApi(ticker: String):Quote{
        var quote = Quote(0.0,0.0,0.0,0.0, 0.0, 0) // TODO(подставляю 0, а надо брать из БД)
        try {
            quote = stockFromApiService.getStockPrice(ticker).execute().body()!!
            return quote
        } catch (e: Exception){
            return quote
        }
    }

    suspend fun setCurrentPriceToStock(ticker: String, currentPrice: Double){
        daoStock.setCurrentPriceToStock(ticker, currentPrice)
    }

    fun setOpenPriceToStock(ticker: String, openPrice: Double){
        daoStock.setOpenPriceToStock(ticker, openPrice)
    }

    fun refreshAllPrice(stocks: List<Stock>){
        CoroutineScope(Dispatchers.IO).launch {
            for (stock in stocks){
                val quote = getStockPriceFromApi(stock.ticker)
                daoStock.setCurrentPriceToStock(stock.ticker, quote.c)
                daoStock.setOpenPriceToStock(stock.ticker, quote.o)
            }
        }
    }


}
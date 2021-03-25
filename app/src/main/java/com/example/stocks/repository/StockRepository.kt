package com.example.stocks.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocks.MainActivity
import com.example.stocks.MyApplication

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
import java.util.logging.Handler

class StockRepository(private val daoStock: DaoStock) {

    val app: Stock = Stock("US","USD","NASDAQ NMS - GLOBAL MARKET","Technology","1980-12-12","https://finnhub.io/api/logo?symbol=AAPL",
            2014404.0, "Apple Inc", "14089961010.0", 16788.096, "AAPL","https://www.apple.com/", true, 123.7914, 120.33)

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

    suspend fun getStockFromDatabase(ticker: String): Stock{
        return daoStock.getStock(ticker)
    }

    fun getLiveDataStock(ticker: String): LiveData<Stock>{
        CoroutineScope(Dispatchers.IO).launch {
            if (getStockFromDatabase(ticker) == null){
                val stockFromApi = getStockFromApi(ticker) //получаем акцию из АПИ
                addStock(stockFromApi)// записали в бд
                val quote = getStockPriceFromApi(ticker) // получаем квот из АПИ
                setCurrentPriceToStock(ticker, quote.c)
                setOpenPriceToStock(ticker, quote.o)
            } else {
                val stockFromDatabase = getStockFromDatabase(ticker)
                var quote: Quote
                try {
                    quote = stockFromApiService.getStockPrice(ticker).execute().body()!!
                } catch (e: Exception){
                    quote = Quote(stockFromDatabase.currentPrice, 0.0, 0.0, stockFromDatabase.openPriceOfTheDay, 0.0, 0)
                }
                setCurrentPriceToStock(ticker, quote.c)
                setOpenPriceToStock(ticker, quote.o)
            }

        }

        return daoStock.getLiveDataStock(ticker)
    }

    suspend fun checkAndAddStock(stock: Stock){

        try {
            val stockFromDatabase = getStockFromDatabase(stock.ticker)

            Log.d("milk", "Stock is in Database $stockFromDatabase")
        } catch (e: Exception) {
            Log.d("milk", "Database have not this stock")
        }


    }

    fun getStockFromApi(ticker: String): Stock{
        try {
            return stockFromApiService.getStock(ticker).execute().body()!!
        } catch (e: Exception){
            CoroutineScope(Dispatchers.Main).launch{
                Toast.makeText(MyApplication.cont, "Error: $e", Toast.LENGTH_LONG).show()
            }
            return app
        }
    }

    fun getStockPrice(ticker: String): Quote {
        var quote = Quote(0.0, 0.0, 0.0, 0.0, 0.0, 0)
        CoroutineScope(Dispatchers.IO).launch {
            val stock = getStockFromDatabase(ticker)
            if (stock != null) {
                quote = Quote(stock.currentPrice, 0.0, 0.0, stock.openPriceOfTheDay, 0.0, 0)
            }
        }
        return quote
    }

    fun getStockPriceFromApi(ticker: String):Quote{
        var quote = Quote(0.0,0.0,0.0,0.0, 0.0, 0) // TODO(подставляю 0, а надо брать из БД)

        try {
            quote = stockFromApiService.getStockPrice(ticker).execute().body()!!
            return quote
        } catch (e: Exception){
            CoroutineScope(Dispatchers.Main).launch{
                Toast.makeText(MyApplication.cont, "Error: $e", Toast.LENGTH_LONG).show()
            }
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
        if (isOnline(MyApplication.cont)){
            CoroutineScope(Dispatchers.IO).launch {
                for (stock in stocks){
                    val quote: Quote = stockFromApiService.getStockPrice(stock.ticker).execute().body()!!
                    daoStock.setCurrentPriceToStock(stock.ticker, quote.c)
                    daoStock.setOpenPriceToStock(stock.ticker, quote.o)
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch{
                Toast.makeText(MyApplication.cont, "Server not responding", Toast.LENGTH_SHORT).show()
            }
        }
/*        CoroutineScope(Dispatchers.IO).launch {
            for (stock in stocks){

                val stockFromDatabase = getStockFromDatabase(stock.ticker)
                var quote: Quote

                try {
                    quote = stockFromApiService.getStockPrice(stock.ticker).execute().body()!!
                } catch (e: Exception){
                    quote = Quote(stockFromDatabase.currentPrice, 0.0, 0.0, stockFromDatabase.openPriceOfTheDay, 0.0, 0)
                    CoroutineScope(Dispatchers.Main).launch{
                        Toast.makeText(MyApplication.cont, "server not responding", Toast.LENGTH_SHORT).show()
                    }
                }

                daoStock.setCurrentPriceToStock(stock.ticker, quote.c)
                daoStock.setOpenPriceToStock(stock.ticker, quote.o)
            }

        }*/
    }

    @SuppressLint("ServiceCast")
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                MyApplication.cont.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


}
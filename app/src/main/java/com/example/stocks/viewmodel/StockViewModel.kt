package com.example.stocks.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.stocks.api.Api
import com.example.stocks.data.DataBaseStock
import com.example.stocks.model.Quote
import com.example.stocks.model.Stock
import com.example.stocks.repository.StockRepository
import com.example.stocks.service.StockService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


public class StockViewModel(application: Application): AndroidViewModel(application){

    val readAllData: LiveData<List<Stock>>
    val favoritesStocks: LiveData<List<Stock>>
    val repository: StockRepository
    val api = Api()
    val stockFromApiService: StockService = api.retrofit.create(StockService::class.java)

    init {
        val stockDao = DataBaseStock.getDataBase(application).stockDao()
        repository = StockRepository(stockDao)
        readAllData = repository.readAllData
        favoritesStocks = repository.readFavoritesStock
        addFirstData()
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStock(stock)
        }
    }

    fun favoritesStocks(): LiveData<List<Stock>> {
        return repository.readFavoritesStock
    }

    fun addStockToFavoritesList(ticker: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStockToFavoritesList(ticker)
        }
    }

    fun removeStockFromFavoritesList(ticker: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeStockFromFavoritesList(ticker)
        }
    }

    fun getLiveDataStock(ticker: String) : LiveData<Stock>{
        return repository.getLiveDataStock(ticker)
    }

    suspend fun getStock(ticker: String): Stock{
        return repository.getStock(ticker)
    }

    fun switchFavorites(ticker: String){
        viewModelScope.launch(Dispatchers.IO){
            val st = repository.getStock(ticker)
            if (st.favorites == true){
                repository.removeStockFromFavoritesList(ticker)
            } else {
                repository.addStockToFavoritesList(ticker)
            }
        }
    }

    fun checkAndAddStock(stock: Stock){
        viewModelScope.launch (Dispatchers.IO){
            repository.checkAndAddStock(stock)
        }
    }

    fun setDataToDatabase() {

        val constituents =
            arrayListOf<String>("DOW", "FBHS", "CDNS", "CBOE", "AJG", "REG", "ADM", "BWA")

        val retrofit = api.retrofit

        val stockService = retrofit.create(StockService::class.java)

        for (ticker in constituents) {
            val stockCallback: Call<Stock> = stockService.getStock(ticker)
            stockCallback.enqueue(object : Callback<Stock> {
                override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                    addStock(response.body()!!)
                    Log.d("milk", "Stock add to list - ${response.body()}")
                }

                override fun onFailure(call: Call<Stock>, t: Throwable) {
                    Log.d("milk", "Fail - STOCK")
                }

            })
        }
    }

    fun getStockFromApi(ticker : String){
        stockFromApiService.getStock(ticker).enqueue(object : Callback<Stock>{
            override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
//                stockFromApiLiveData.value = response.body()
                addStock(response.body()!!)

            }

            override fun onFailure(call: Call<Stock>, t: Throwable) {
                Log.d("milk", "err: $t")
            }

        })
    }

    fun refreshAllPrice(stocks : List<Stock>){
        viewModelScope.launch (Dispatchers.IO) {
            repository.refreshAllPrice(stocks)
        }
    }


    fun addFirstData(){
        val app: Stock = Stock("US","USD","NASDAQ NMS - GLOBAL MARKET","Technology","1980-12-12","https://finnhub.io/api/logo?symbol=AAPL",
                2014404.0, "Apple Inc", "14089961010.0", 16788.096, "AAPL","https://www.apple.com/", true, 123.7914, 120.33)
        val amzn = Stock("US","USD","NASDAQ NMS - GLOBAL MARKET","Retail","1997-05-15", "https://finnhub.io/api/logo?symbol=AMZN",
                1548442.0,"Amazon.com Inc","12062661000.0",503.564743,"AMZN","https://www.amazon.com/", false, 3123.34, 3067.85)
        val etsy = Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Retail", "2015-04-16", "https://finnhub.io/api/logo?symbol=ETSY", 27152.28,
                "ETSY Inc", "17188557956.0", 126.049276, "ETSY", "https://www.etsy.com/", false, 219.82, 210.312)
        val ford = Stock("US", "USD","NEW YORK STOCK EXCHANGE, INC.", "Automobiles", "1956-03-07", "https://finnhub.io/api/logo?symbol=F",
                50137.63, "Ford Motor Co", "13133223000.0", 3978.695017, "F", "https://www.ford.com/", false, 12.845, 12.85)
        val fb = Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Media", "2012-05-18", "https://finnhub.io/api/logo?symbol=FB",
                826137.5, "Facebook Inc", "16506187714.0", 2847.669951, "FB", "https://www.facebook.com", false, 297.255, 290.45)
        val goog = Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Media", "2004-08-19", "https://finnhub.io/api/logo?symbol=GOOGL",
                1371768.0, "Alphabet Inc", "16502530000.0", 674.136665, "GOOGL", "https://abc.xyz/", true, 2041.07, 2027.63)
        val lyft = Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Road & Rail", "2019-03-29", "https://finnhub.io/api/logo?symbol=LYFT",
                21735.74, "Lyft Inc", "18442502773.0", 328.930746, "LYFT", "https://www.lyft.com/", true, 64.89, 66.68)
        val msft = Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Technology", "1986-03-13", "https://finnhub.io/api/logo?symbol=MSFT",
                marketCapitalization=1737349.0, name="Microsoft Corp", phone="14258828080.0", shareOutstanding=7542.215767, ticker="MSFT", weburl="https://www.microsoft.com/en-us", favorites=true, currentPrice=236.46, openPriceOfTheDay=230.27)
        val oracle = Stock("US", currency="USD", exchange="NEW YORK STOCK EXCHANGE, INC.", finnhubIndustry="Technology", ipo="1986-03-12", logo="https://finnhub.io/api/logo?symbol=ORCL",
                marketCapitalization=191063.0, name="Oracle Corp", phone="16505067000.0", shareOutstanding=2883.535, ticker="ORCL", weburl="https://www.oracle.com/", favorites=true, currentPrice=66.33, 66.0)


        addStock(app)
        addStock(amzn)
        addStock(etsy)
        addStock(ford)
        addStock(fb)
        addStock(goog)
        addStock(lyft)
        addStock(msft)
        addStock(oracle)
    }

}
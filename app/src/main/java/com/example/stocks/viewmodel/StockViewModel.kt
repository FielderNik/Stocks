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

}
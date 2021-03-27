package com.example.stocks.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.example.stocks.MainActivity
import com.example.stocks.MyApplication
import com.example.stocks.api.Api
import com.example.stocks.data.DataBaseStock
import com.example.stocks.model.Quote
import com.example.stocks.model.Stock
import com.example.stocks.model.Result
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


class StockViewModel(application: Application): AndroidViewModel(application){

    val readAllData: LiveData<List<Stock>>
    val favoritesStocks: LiveData<List<Stock>>
    val repository: StockRepository

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
        return repository.getStockFromDatabase(ticker)
    }

    fun switchFavorites(ticker: String){
        viewModelScope.launch(Dispatchers.IO){
            val st = repository.getStockFromDatabase(ticker)
            if (st.favorites == true){
                repository.removeStockFromFavoritesList(ticker)
            } else {
                repository.addStockToFavoritesList(ticker)
            }
        }
    }

    suspend fun getStockListFromDatabase() : List<Stock>{
        return repository.getStockListFromDatabase()
    }

    fun refreshAllPrice(stocks : List<Stock>){
        viewModelScope.launch (Dispatchers.IO) {
            repository.refreshAllPrice(stocks)
        }
    }

    fun getSearchStockList(query: String): LiveData<List<Result>>{
        repository.getSearchStockList(query)
        return repository.searchStockListLiveData
    }


}
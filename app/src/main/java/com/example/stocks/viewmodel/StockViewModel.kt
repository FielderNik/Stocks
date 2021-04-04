package com.example.stocks.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.stocks.data.DataBaseStock
import com.example.stocks.model.ChartStock
import com.example.stocks.model.CompanyNews
import com.example.stocks.model.Stock
import com.example.stocks.model.Result
import com.example.stocks.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    fun getLiveDataStock(ticker: String) : LiveData<Stock>{
        return repository.getLiveDataStock(ticker)
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

    fun getLiveDataToChartStock(symbol: String, resolution: String, from: String, to: String): LiveData<ChartStock>{
        repository.getLiveDataToChartStockFromApi(symbol, resolution, from, to)
        return repository.liveDataToChartStock
    }

    fun getLiveDataCompanyNewsList(ticker: String, from: String, to: String): LiveData<CompanyNews>{
        repository.getCompanyNews(ticker, from, to)
        return repository.liveDataCompanyNewsList
    }


    //service function

    fun deleteElementFromDB(ticker: String){
        viewModelScope.launch (Dispatchers.IO) {
            repository.deleteElementFromDB(ticker)
        }

    }

}
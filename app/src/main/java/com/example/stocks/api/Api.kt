package com.example.stocks.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stocks.model.ListStocks
import com.example.stocks.model.Quote
import com.example.stocks.service.SearchStockService

import com.example.stocks.model.Result
import com.example.stocks.model.Stock
import com.example.stocks.service.StockService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api {
    val BASE_URL = "https://finnhub.io"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val searchListLiveData = MutableLiveData<List<Result>>()
    val searchResponse = MutableLiveData<ListStocks>()
    val listStockService: SearchStockService = retrofit.create(SearchStockService::class.java)

    fun getSearchStockList(query: String){
        listStockService.getQueryStock(query).enqueue(object : Callback<ListStocks>{
            override fun onResponse(call: Call<ListStocks>, response: Response<ListStocks>) {
                searchListLiveData.value = response.body()?.result
                searchResponse.value = response.body()
            }

            override fun onFailure(call: Call<ListStocks>, t: Throwable) {
                Log.d("milk", "err: $t")
            }

        })
    }

    val stockFromApiLiveData = MutableLiveData<Stock>()
    val stockFromApiService: StockService = retrofit.create(StockService::class.java)

    fun getStockFromApi(ticker : String){
        stockFromApiService.getStock(ticker).enqueue(object : Callback<Stock>{
            override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                stockFromApiLiveData.value = response.body()
            }

            override fun onFailure(call: Call<Stock>, t: Throwable) {
                Log.d("milk", "err: $t")
            }

        })
    }

    val stockPriceLiveData = MutableLiveData<Quote>()
    fun getStockPrice(ticker: String){
        stockFromApiService.getStockPrice(ticker).enqueue(object : Callback<Quote>{
            override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                stockPriceLiveData.value = response.body()
            }

            override fun onFailure(call: Call<Quote>, t: Throwable) {
                Log.d("milk", "err: $t")
            }
        })
    }


    var quote: Quote? = null
    fun getStockPriceExecute(ticker:String){
        CoroutineScope(Dispatchers.IO).launch {
            val stockPriceFromApi = stockFromApiService.getStockPrice(ticker).execute().body()
            quote = stockPriceFromApi
            Log.d("milkApi", "quote: $quote")
        }
    }


    val readQuoteData = MutableLiveData<List<Quote>>()
    fun readQuote(stocks: List<Stock>){
        val quoteList = mutableListOf<Quote>()
        for (stock in stocks){
            stockFromApiService.getStockPrice(stock.ticker).enqueue(object : Callback<Quote>{
                override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                    quoteList.add(response.body()!!)
                    readQuoteData.value = quoteList
                    Log.d("milkApi", "quote: $quoteList")
                }

                override fun onFailure(call: Call<Quote>, t: Throwable) {
                    Log.d("milkApi", "quote: $quote")
                }

            })
        }

    }

}
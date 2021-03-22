package com.example.stocks.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stocks.model.Stock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StockProvider {
    private val BASE_URL = "https://finnhub.io"

    val constituents = arrayListOf<String>("DOW","FBHS","CDNS","CBOE","AJG","REG","ADM","BWA")
/*    var quoteList: MutableLiveData<ArrayList<Quote>> = MutableLiveData()
    val list: ArrayList<Quote> = ArrayList()*/

    var stocksList: MutableLiveData<ArrayList<Stock>> = MutableLiveData()
    val ls: ArrayList<Stock> = ArrayList()


/*    fun loadQuoteList() : LiveData<ArrayList<Quote>> {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val quoteService = retrofit.create(QuotesService::class.java)

        for (ticker in constituents){
            val quoteCallback: Call<Quote> = quoteService.getQuote(ticker)
            quoteCallback.enqueue(object: Callback<Quote> {
                override fun onResponse(call: Call<Quote>, response: Response<Quote>) {
                    list.add(response.body()!!)
                    quoteList.value = list
                    Log.d("milk", "add to list - ${response.body()}")
                }

                override fun onFailure(call: Call<Quote>, t: Throwable) {
                    Log.d("milk", "Fail")
                }
            })
        }
        Log.d("milk", "list: $list")
        return quoteList
    }*/

    fun loadStockList(): LiveData<ArrayList<Stock>> {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val stockService = retrofit.create(StockService::class.java)

        for (ticker in constituents){
            val stockCallback: Call<Stock> = stockService.getStock(ticker)
            stockCallback.enqueue(object: Callback<Stock> {
                override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                    ls.add(response.body()!!)
                    stocksList.postValue(ls)
                    Log.d("milk", "Stock add to list - ${response.body()}")
                }

                override fun onFailure(call: Call<Stock>, t: Throwable) {
                    Log.d("milk", "Fail - STOCK")
                }

            })
        }

        return stocksList
    }
}
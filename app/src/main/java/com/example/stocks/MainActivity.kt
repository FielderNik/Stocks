package com.example.stocks

import android.app.Application
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.adapters.AdapterListStock
import com.example.stocks.adapters.AdapterRecyclerViewSearchStock
import com.example.stocks.api.Api
import com.example.stocks.model.ListStocks
import com.example.stocks.model.Quote
import com.example.stocks.model.Result
import com.example.stocks.model.Stock
import com.example.stocks.viewmodel.StockViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {


    lateinit var recyclerView: RecyclerView
    lateinit var btnRefreshPrice: ImageButton
    lateinit var tvNavStock: TextView
    lateinit var tvNavFavorite: TextView
    lateinit var tvSearch: TextView
    lateinit var searchView: SearchView
    lateinit var progressBar: ProgressBar
    lateinit var adapterListStock: AdapterListStock
    lateinit var adapterRecyclerViewStock: AdapterRecyclerViewSearchStock
    val apiSearch = Api()
    val liveDataSearchStock = MutableLiveData<List<Result>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        btnRefreshPrice = findViewById(R.id.btnRefreshPrice)
        tvNavStock = findViewById(R.id.tvNavStocks)
        tvNavFavorite = findViewById(R.id.tvNavFavorites)
        progressBar = findViewById(R.id.progressBar)
        tvSearch = findViewById(R.id.tvSearch)

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(this)


//        val liveDataSearchStock = apiSearch.searchListLiveData
/*        val searchResponse = apiSearch.searchResponse*/

        //stockViewModel.setDataToDatabase() TODO(не удалять)

        adapterListStock = AdapterListStock(this)
        adapterRecyclerViewStock = AdapterRecyclerViewSearchStock(this)

        recyclerView = findViewById(R.id.recycler)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterListStock

        var stockList = emptyList<Stock>()

        stockViewModel.readAllData.observe(this, Observer {
            it?.let {
                adapterListStock.refreshData(it)
                stockList = it
                Log.d("milkMain", "stockList: $stockList")
            }
        })

        liveDataSearchStock.observe(this, Observer {
            progressBar.isVisible = false
            adapterRecyclerViewStock.refreshData(it.filter { it.type != "" })
            Log.d("milkQuery", "query: ${liveDataSearchStock.value}")
        })

/*        searchResponse.observe(this, Observer {
            Log.d("milkQuery", "query: ${searchResponse.value}")
        })*/

        tvNavStock.setOnClickListener {
            recyclerView.adapter = adapterListStock
            stockViewModel.readAllData.observe(this, Observer {
                it?.let {
                    adapterListStock.refreshData(it)
                }
            })
            switchActiveTextView(tvNavStock, tvNavFavorite, tvSearch)
        }

        tvNavFavorite.setOnClickListener {
            recyclerView.adapter = adapterListStock
            stockViewModel.favoritesStocks.observe(this, Observer {
                it?.let {
                    adapterListStock.refreshData(it)
                }
            })
            switchActiveTextView(tvNavFavorite, tvSearch, tvNavStock)
        }

        tvSearch.setOnClickListener {
            recyclerView.adapter = adapterRecyclerViewStock
            switchActiveTextView(tvSearch, tvNavFavorite, tvNavStock)
        }

        btnRefreshPrice.setOnClickListener{
            stockViewModel.refreshAllPrice(stockList)
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        progressBar.isVisible = true
        recyclerView.adapter = adapterRecyclerViewStock
        getSearchStockList(query.toString())
        Log.d("milkSearch", "response OK")

        switchActiveTextView(tvSearch, tvNavFavorite, tvNavStock)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    fun getSearchStockList(query: String){
        apiSearch.listStockService.getQueryStock(query).enqueue(object : Callback<ListStocks> {
            override fun onResponse(call: Call<ListStocks>, response: Response<ListStocks>) {
                Log.d("milkApi", "response: ${response.headers()}")
                liveDataSearchStock.value = response.body()?.result
//                searchResponse.value = response.body()
            }

            override fun onFailure(call: Call<ListStocks>, t: Throwable) {
                Log.d("milk", "err: $t")
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                progressBar.isVisible = false
            }

        })
    }

    fun switchActiveTextView(activeTv: TextView, passTvFirst: TextView, passTvSecond: TextView){
        activeTv.textSize = 24f
        activeTv.setTextColor(Color.parseColor("#000000"))

        passTvFirst.textSize = 16f
        passTvFirst.setTextColor(Color.parseColor("#DBE2EA"))

        passTvSecond.textSize = 16f
        passTvSecond.setTextColor(Color.parseColor("#DBE2EA"))
    }




}
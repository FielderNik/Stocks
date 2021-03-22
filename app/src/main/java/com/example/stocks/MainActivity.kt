package com.example.stocks

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.adapters.AdapterListStock
import com.example.stocks.adapters.AdapterRecyclerViewSearchStock
import com.example.stocks.api.Api
import com.example.stocks.model.Quote
import com.example.stocks.model.Stock
import com.example.stocks.viewmodel.StockViewModel

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

        val liveDataSearchStock = apiSearch.searchListLiveData
        val searchResponse = apiSearch.searchResponse

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

        searchResponse.observe(this, Observer {
            Log.d("milkQuery", "query: ${searchResponse.value}")
        })

        tvNavStock.setOnClickListener {
            recyclerView.adapter = adapterListStock
            stockViewModel.readAllData.observe(this, Observer {
                it?.let {
                    adapterListStock.refreshData(it)
                }
            })
            tvNavStock.textSize = 24f
            tvNavFavorite.textSize = 16f
            tvSearch.textSize = 16f
            tvNavStock.setTextColor(Color.parseColor("#000000"))
            tvNavFavorite.setTextColor(Color.parseColor("#DBE2EA"))
            tvSearch.setTextColor(Color.parseColor("#DBE2EA"))
        }

        tvNavFavorite.setOnClickListener {
            recyclerView.adapter = adapterListStock
            stockViewModel.favoritesStocks.observe(this, Observer {
                it?.let {
                    adapterListStock.refreshData(it)
                }
            })
            tvNavFavorite.textSize = 24f
            tvNavStock.textSize = 16f
            tvSearch.textSize = 16f
            tvNavFavorite.setTextColor(Color.parseColor("#000000"))
            tvNavStock.setTextColor(Color.parseColor("#DBE2EA"))
            tvSearch.setTextColor(Color.parseColor("#DBE2EA"))

        }

        tvSearch.setOnClickListener {
            recyclerView.adapter = adapterRecyclerViewStock
            tvSearch.textSize = 24f
            tvNavFavorite.textSize = 16f
            tvNavStock.textSize = 16f
            tvSearch.setTextColor(Color.parseColor("#000000"))
            tvNavFavorite.setTextColor(Color.parseColor("#DBE2EA"))
            tvNavStock.setTextColor(Color.parseColor("#DBE2EA"))
        }

        btnRefreshPrice.setOnClickListener{
            stockViewModel.refreshAllPrice(stockList)
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        progressBar.isVisible = true
        recyclerView.adapter = adapterRecyclerViewStock
        apiSearch.getSearchStockList(query.toString())


        tvSearch.textSize = 24f
        tvNavFavorite.textSize = 16f
        tvNavStock.textSize = 16f
        tvSearch.setTextColor(Color.parseColor("#000000"))
        tvNavFavorite.setTextColor(Color.parseColor("#DBE2EA"))
        tvNavStock.setTextColor(Color.parseColor("#DBE2EA"))
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}
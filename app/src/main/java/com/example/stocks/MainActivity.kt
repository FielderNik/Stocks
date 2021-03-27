package com.example.stocks


import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.res.ResourcesCompat
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
import com.example.stocks.model.Result
import com.example.stocks.viewmodel.StockViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    companion object
    {
        @SuppressLint("StaticFieldLeak")
        lateinit var progressBar: ProgressBar
    }
    lateinit var recyclerView: RecyclerView
    lateinit var btnRefreshPrice: ImageButton
    lateinit var tvNavStock: TextView
    lateinit var tvNavFavorite: TextView
    lateinit var tvSearch: TextView
    lateinit var searchView: SearchView

    lateinit var adapterListStock: AdapterListStock
    lateinit var adapterFavoriteListStock: AdapterListStock
    lateinit var adapterRecyclerViewStock: AdapterRecyclerViewSearchStock
    val liveDataSearchStock = MutableLiveData<List<Result>>()
    lateinit var stockViewModel: StockViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)

        btnRefreshPrice = findViewById(R.id.btnRefreshPrice)
        tvNavStock = findViewById(R.id.tvNavStocks)
        tvNavFavorite = findViewById(R.id.tvNavFavorites)
        progressBar = findViewById(R.id.progressBar)
        tvSearch = findViewById(R.id.tvSearch)

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(this)

        adapterListStock = AdapterListStock(this)
        adapterFavoriteListStock = AdapterListStock(this)
        adapterRecyclerViewStock = AdapterRecyclerViewSearchStock(this)

        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterListStock


        stockViewModel.readAllData.observe(this, Observer {
            it?.let {
                adapterListStock.refreshData(it)
            }
        })

        liveDataSearchStock.observe(this, Observer {
            progressBar.isVisible = false
            adapterRecyclerViewStock.refreshData(it.filter { it.type != "" })
            Log.d("milkQuery", "query: ${liveDataSearchStock.value}")
        })

        tvNavStock.setOnClickListener {
            recyclerView.adapter = adapterListStock
            stockViewModel.readAllData.observe(this, Observer {
                it?.let {
                    adapterListStock.refreshData(it)
                }
            })
            recyclerView.adapter = adapterListStock
            switchActiveTextView(tvNavStock, tvNavFavorite, tvSearch)
        }

        tvNavFavorite.setOnClickListener {

            stockViewModel.favoritesStocks.observe(this, Observer {
                it?.let {
                    adapterFavoriteListStock.refreshData(it)
                }
            })
            recyclerView.adapter = adapterFavoriteListStock
            switchActiveTextView(tvNavFavorite, tvSearch, tvNavStock)
        }

        tvSearch.setOnClickListener {
            recyclerView.adapter = adapterRecyclerViewStock
            switchActiveTextView(tvSearch, tvNavFavorite, tvNavStock)

        }

        btnRefreshPrice.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val listStock = stockViewModel.getStockListFromDatabase()
                stockViewModel.refreshAllPrice(listStock)
                Log.d("milkMain", "stockList when click refresh: ${listStock?.size}")
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        progressBar.isVisible = true
        recyclerView.adapter = adapterRecyclerViewStock
        stockViewModel.getSearchStockList(query!!).observe(this, Observer {
            it?.let {
                adapterRecyclerViewStock.refreshData(it)
                progressBar.isVisible = false
            }
        })
        Log.d("milkSearch", "response OK")

        switchActiveTextView(tvSearch, tvNavFavorite, tvNavStock)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }


    fun switchActiveTextView(activeTv: TextView, passTvFirst: TextView, passTvSecond: TextView){
        val mainFont = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        val font = ResourcesCompat.getFont(this, R.font.montserrat)

        activeTv.textSize = 24f
        activeTv.setTextColor(Color.parseColor("#000000"))
        activeTv.typeface = mainFont

        passTvFirst.textSize = 16f
        passTvFirst.setTextColor(Color.parseColor("#DBE2EA"))
        passTvFirst.typeface = font

        passTvSecond.textSize = 16f
        passTvSecond.setTextColor(Color.parseColor("#DBE2EA"))
        passTvFirst.typeface = font
    }

}
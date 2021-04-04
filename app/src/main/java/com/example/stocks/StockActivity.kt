package com.example.stocks

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.stocks.fragments.FragmentChartStock
import com.example.stocks.fragments.FragmentCompanyNews
import com.example.stocks.fragments.FragmentSummaryStock

import com.example.stocks.viewmodel.StockViewModel

class StockActivity : AppCompatActivity() {

    lateinit var tvTicker: TextView
    lateinit var tvCompanyName: TextView
    lateinit var tvDescription: TextView
    lateinit var ivFavorite: ImageView

    lateinit var btnBack: ImageButton
    lateinit var btnBuy: Button
    lateinit var tvCurrentPrice_Card: TextView
    lateinit var tvDiffPrice_Card: TextView

    lateinit var tvSummaryStock: TextView
    lateinit var tvChartStock: TextView
    lateinit var tvNewsStock: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)

        val stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        val tickerFromList = intent.extras?.get("ticker").toString()

        btnBack = findViewById(R.id.btnBack)
        btnBuy = findViewById(R.id.btnBuy)
        tvCurrentPrice_Card = findViewById(R.id.tvCurrentPrice_Card)
        tvDiffPrice_Card = findViewById(R.id.tvDiffPrice_Card)
        tvSummaryStock = findViewById(R.id.tvSummary)
        tvChartStock = findViewById(R.id.tvChart)
        tvNewsStock = findViewById(R.id.tvNews)

        val fragmentSummaryStock = FragmentSummaryStock(stockViewModel, tickerFromList)
        val fragmentChartStock = FragmentChartStock(stockViewModel, tickerFromList)
        val fragmentCompanyNews = FragmentCompanyNews(stockViewModel, tickerFromList)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragments, fragmentSummaryStock)
            commit()
            switchActiveTextView(tvSummaryStock, tvChartStock, tvNewsStock)
        }

        tvSummaryStock.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragments, fragmentSummaryStock)
                commit()
                switchActiveTextView(tvSummaryStock, tvChartStock, tvNewsStock)
            }
        }

        tvChartStock.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragments, fragmentChartStock)
                commit()
                switchActiveTextView(tvChartStock, tvSummaryStock, tvNewsStock)
            }
        }

        tvNewsStock.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragments, fragmentCompanyNews)
                commit()
                switchActiveTextView(tvNewsStock, tvSummaryStock, tvChartStock)
            }
        }

        btnBack.setOnClickListener{
            finish()
        }
        btnBuy.setOnClickListener{
            Toast.makeText(this, "You buy stock", Toast.LENGTH_SHORT).show()
        }

        tvTicker = findViewById(R.id.tvTicker_Card)
        tvCompanyName = findViewById(R.id.tvCompanyName_Card)
//        tvDescription = findViewById(R.id.tvDescription_Card)

        ivFavorite = findViewById(R.id.ivFavorite_Card)

        stockViewModel.getLiveDataStock(tickerFromList).observe(this, Observer {
            Log.d("stockActivity", "searchQuery: ${it?.ticker}")
            it?.let {
                tvTicker.text = it.ticker
                tvCompanyName.text = it.name
                val currentPrice = it.currentPrice

                tvCurrentPrice_Card.text = String.format("$%1.2f", it.currentPrice)
                val openPrice = it.openPriceOfTheDay
                val diffPrice = currentPrice - openPrice
                val percent: Double
                if (currentPrice == 0.0){
                    percent = 0.0
                } else {
                    percent = diffPrice/currentPrice*100
                }

                if (diffPrice >= 0){
                    tvDiffPrice_Card.setTextColor(Color.parseColor("#24B25D"))
                    tvDiffPrice_Card.text = String.format("+$%1.2f (%1.2f%%)", diffPrice, percent)
                } else if (diffPrice < 0){
                    tvDiffPrice_Card.setTextColor(Color.parseColor("#B22424"))
                    tvDiffPrice_Card.text = String.format("$%1.2f (%1.2f%%)", diffPrice, percent)
                }


                if (it.favorites == true){
                    ivFavorite.setImageResource(R.drawable.star_on)
                } else{
                    ivFavorite.setImageResource(R.drawable.star_off)
                }

                btnBuy.text = String.format("Buy for $%1.2f", currentPrice)


            }
        })

        ivFavorite.setOnClickListener{
            stockViewModel.switchFavorites(tickerFromList)
        }

    }


    fun switchActiveTextView(activeTv: TextView, passTvFirst: TextView, passTvSecond: TextView){
        val mainFont = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        val font = ResourcesCompat.getFont(this, R.font.montserrat)

        activeTv.textSize = 18f
        activeTv.setTextColor(Color.parseColor("#000000"))
        activeTv.typeface = mainFont

        passTvFirst.textSize = 14f
        passTvFirst.setTextColor(Color.parseColor("#DBE2EA"))
        passTvFirst.typeface = font

        passTvSecond.textSize = 14f
        passTvSecond.setTextColor(Color.parseColor("#DBE2EA"))
        passTvSecond.typeface = font
    }
}
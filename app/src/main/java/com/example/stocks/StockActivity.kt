package com.example.stocks

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target

import com.example.stocks.viewmodel.StockViewModel

class StockActivity : AppCompatActivity() {

    lateinit var tvTicker: TextView
    lateinit var tvCompanyName: TextView
    lateinit var tvDescription: TextView
    lateinit var ivFavorite: ImageView
    lateinit var trainImage: ImageView
    lateinit var btnBack: ImageButton
    lateinit var btnBuy: Button
    lateinit var tvCurrentPrice_Card: TextView
    lateinit var tvDiffPrice_Card: TextView

    //stock info
    lateinit var tvCountry: TextView
    lateinit var tvExchange: TextView
    lateinit var tvIndustry: TextView
    lateinit var tvIpo: TextView
    lateinit var tvCapitalization: TextView
    lateinit var tvOutstandingShare: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)

        tvCountry = findViewById(R.id.tvCountry)
        tvExchange = findViewById(R.id.tvExchange)
        tvIndustry = findViewById(R.id.tvIndustry)
        tvIpo = findViewById(R.id.tvIpo)
        tvCapitalization = findViewById(R.id.tvCapitalization)
        tvOutstandingShare = findViewById(R.id.tvOutstandingShare)
        btnBack = findViewById(R.id.btnBack)
        btnBuy = findViewById(R.id.btnBuy)
        tvCurrentPrice_Card = findViewById(R.id.tvCurrentPrice_Card)
        tvDiffPrice_Card = findViewById(R.id.tvDiffPrice_Card)

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
        trainImage = findViewById(R.id.ivTrainGlide)

        val stockViewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        val tickerFromList = intent.extras?.get("ticker").toString()

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

                tvCountry.text = it.country
                tvExchange.text = it.exchange
                tvIndustry.text = it.finnhubIndustry
                tvIpo.text = it.ipo
                tvCapitalization.text = it.marketCapitalization.toString()
                tvOutstandingShare.text = it.shareOutstanding.toString()

                if (it.favorites == true){
                    ivFavorite.setImageResource(R.drawable.star_on)
                } else{
                    ivFavorite.setImageResource(R.drawable.star_off)
                }

                btnBuy.text = String.format("Buy for $%1.2f", currentPrice)

                val logoUrl = it.logo

                Glide
                    .with(this)
                    .load(logoUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .override(Target.SIZE_ORIGINAL, 200)
                    .centerCrop()
                    .into(trainImage)
            }
        })

        ivFavorite.setOnClickListener{
            stockViewModel.switchFavorites(tickerFromList)
        }

    }
}
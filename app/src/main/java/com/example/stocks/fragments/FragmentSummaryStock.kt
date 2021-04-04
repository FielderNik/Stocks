package com.example.stocks.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.stocks.R
import com.example.stocks.viewmodel.StockViewModel

class FragmentSummaryStock(_viewModel: StockViewModel, _ticker: String) : Fragment() {
    val viewModel = _viewModel
    val ticker = _ticker

    lateinit var tvCountry: TextView
    lateinit var tvExchange: TextView
    lateinit var tvIndustry: TextView
    lateinit var tvIpo: TextView
    lateinit var tvCapitalization: TextView
    lateinit var tvOutstandingShare: TextView
    lateinit var stockLogo: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvCountry = view.findViewById(R.id.tvCountry)
        tvExchange = view.findViewById(R.id.tvExchange)
        tvIndustry = view.findViewById(R.id.tvIndustry)
        tvIpo = view.findViewById(R.id.tvIpo)
        tvCapitalization = view.findViewById(R.id.tvCapitalization)
        tvOutstandingShare = view.findViewById(R.id.tvOutstandingShare)
        stockLogo = view.findViewById(R.id.ivTrainGlide)


        viewModel.getLiveDataStock(ticker).observe(viewLifecycleOwner, Observer {
            Log.d("stockActivity", "searchQuery: ${it?.ticker}")
            it?.let {
                tvCountry.text = it.country
                tvExchange.text = it.exchange
                tvIndustry.text = it.finnhubIndustry
                tvIpo.text = it.ipo
                tvCapitalization.text = it.marketCapitalization.toString()
                tvOutstandingShare.text = it.shareOutstanding.toString()

                val logoUrl = it.logo

                Glide
                    .with(this)
                    .load(logoUrl)
                    .placeholder(R.drawable.ic_baseline_crop_original_24)
                    .override(Target.SIZE_ORIGINAL, 200)
                    .centerCrop()
                    .into(stockLogo)
            }
        })



        super.onViewCreated(view, savedInstanceState)
    }

}
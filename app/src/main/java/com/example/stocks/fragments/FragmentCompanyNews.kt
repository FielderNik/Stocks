package com.example.stocks.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.R
import com.example.stocks.adapters.AdapterCompanyNews
import com.example.stocks.viewmodel.StockViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class FragmentCompanyNews (_viewModel: StockViewModel, _ticker: String) : Fragment() {
    val viewModel = _viewModel
    val ticker = _ticker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewCompanyNews = view.findViewById<RecyclerView>(R.id.rvCompanyNews)
        val adapterCompanyNews = AdapterCompanyNews(requireContext())

        recyclerViewCompanyNews.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCompanyNews.adapter = adapterCompanyNews

        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val startTime = currentTime - 604800

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val dateNewsTo = Date(currentTime * 1000L)
        val dateNewsFrom = Date(startTime * 1000L)

        viewModel.getLiveDataCompanyNewsList(ticker, simpleDateFormat.format(dateNewsFrom), simpleDateFormat.format(dateNewsTo)).observe(viewLifecycleOwner, Observer {
            it?.let {
                adapterCompanyNews.refreshNewsData(it)
            }

        })


    }


}
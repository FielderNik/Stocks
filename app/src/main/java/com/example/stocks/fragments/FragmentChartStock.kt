package com.example.stocks.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.stocks.R
import com.example.stocks.model.ChartStock
import com.example.stocks.model.ChartView
import com.example.stocks.viewmodel.StockViewModel
import com.github.mikephil.charting.charts.LineChart
import java.time.LocalDateTime
import java.time.ZoneOffset

class FragmentChartStock(_viewModel: StockViewModel, _ticker: String) : Fragment() {
    val viewModel = _viewModel
    val ticker = _ticker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChartView = view.findViewById<LineChart>(R.id.lineChartInFragment)

        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val startTime = currentTime - 2629743

        val lineChart = ChartView(lineChartView)
        var valuesForChart = mutableListOf<Double>()
        viewModel.getLiveDataToChartStock(ticker, "D", startTime.toString(), currentTime.toString()).observe(viewLifecycleOwner, Observer {
            it?.let {
                valuesForChart = it.c.toMutableList()
            }
            lineChart.setChart(valuesForChart, ticker)
        })




    }


}
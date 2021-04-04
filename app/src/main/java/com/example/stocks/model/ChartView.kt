package com.example.stocks.model

import android.util.Log
import com.example.stocks.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.DecimalFormat

class ChartView(lineChart: LineChart) {
    val lineChart = lineChart

    fun setChart(valuesForChart: List<Double>, ticker: String){

        Log.d("milk", "begin fun setChart")
        val yValues = arrayListOf<Entry>()

        for ((index, value) in valuesForChart.withIndex()){
            yValues.add(Entry(index.toFloat(), value.toFloat()))
        }

        val quarters: ArrayList<String> = arrayListOf()
        for (i in valuesForChart.indices){
            quarters.add("Q${i}")
        }

        val set1 = LineDataSet(yValues, ticker)
        set1.setDrawCircles(false)
        set1.setColors(R.color.black)

//        val format  = DecimalFormat("")
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
//                    return quarters[value.toInt()]
                return ""
            }

            override fun getPointLabel(entry: Entry?): String {
//                    return format.format(entry?.y)
                return ""
            }
        }

        val xAxis = lineChart.xAxis
//        xAxis.granularity = 0.1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val left: YAxis = lineChart.getAxisLeft()
        left.setDrawLabels(false) // no axis labels

        xAxis.valueFormatter = formatter

        val dataSet = arrayListOf<ILineDataSet>()

        dataSet.add(set1)

        val description: Description = lineChart.description
        description.text = "Chart for the last month"

        val data = LineData(dataSet)
        data.setValueFormatter(formatter)
        lineChart.setDrawGridBackground(false)
        lineChart.setDrawBorders(false)

        lineChart.setData(data)

        lineChart.invalidate()
    }
}
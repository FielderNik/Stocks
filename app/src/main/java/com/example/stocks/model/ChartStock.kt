package com.example.stocks.model

data class ChartStock(
    val c: List<Double>,
    val h: List<Double>,
    val l: List<Double>,
    val o: List<Double>,
    val s: String,
    val t: List<Int>,
    val v: List<Int>
)
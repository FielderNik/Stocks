package com.example.stocks.model

data class CompanyNewsItem(
    val category: String,
    val datetime: Long,
    val headline: String,
    val id: Int,
    val image: String,
    val related: String,
    val source: String,
    val summary: String,
    val url: String
)
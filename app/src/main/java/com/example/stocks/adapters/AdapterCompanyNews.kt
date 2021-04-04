package com.example.stocks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.R
import com.example.stocks.model.CompanyNewsItem
import java.text.SimpleDateFormat
import java.util.*

class AdapterCompanyNews(context: Context) : RecyclerView.Adapter<AdapterCompanyNews.ViewHolder>() {

    val inflater = LayoutInflater.from(context)
    var listCompanyNews = emptyList<CompanyNewsItem>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var tvNewsHeader: TextView
        var tvNewsSummary: TextView
        var tvNewsDate: TextView

        init {
            tvNewsHeader = view.findViewById(R.id.tvNewsHeader)
            tvNewsSummary = view.findViewById(R.id.tvNewsSummary)
            tvNewsDate = view.findViewById(R.id.tvNewsDate)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCompanyNews.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.company_news_item, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterCompanyNews.ViewHolder, position: Int) {
        holder.tvNewsHeader.text = listCompanyNews[position].headline
        holder.tvNewsSummary.text = listCompanyNews[position].summary

        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.UK)
        val dateNews = Date(listCompanyNews[position].datetime * 1000L)
        holder.tvNewsDate.text = simpleDateFormat.format(dateNews)
    }

    override fun getItemCount(): Int = listCompanyNews.size


    fun refreshNewsData(_listCompanyNews: List<CompanyNewsItem>){
        this.listCompanyNews = _listCompanyNews
        notifyDataSetChanged()
    }
}
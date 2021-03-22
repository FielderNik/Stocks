package com.example.stocks.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.R
import com.example.stocks.StockActivity
import com.example.stocks.model.Result


class AdapterRecyclerViewSearchStock(context: Context): RecyclerView.Adapter<AdapterRecyclerViewSearchStock.ViewHolderSearch>() {
    var queryStocks: List<Result> = emptyList<Result>()

    class ViewHolderSearch(view: View): RecyclerView.ViewHolder(view){
        var ticker: TextView? = null
        var companyName: TextView? = null

        init {
            ticker = view.findViewById(R.id.tvTickerItem)
            companyName = view.findViewById(R.id.tvCompanyNameItem)

            view.setOnClickListener{
                val intent = Intent(view.context, StockActivity::class.java)
                intent.putExtra("ticker", ticker?.text)
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRecyclerViewSearchStock.ViewHolderSearch {
        return ViewHolderSearch(LayoutInflater.from(parent.context).inflate(R.layout.query_item, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterRecyclerViewSearchStock.ViewHolderSearch, position: Int) {
        holder.ticker?.text = queryStocks[position].symbol
        holder.companyName?.text = queryStocks[position].description
    }

    override fun getItemCount() = queryStocks.size

    fun refreshData(queryStocks: List<Result>){
        this.queryStocks = queryStocks
        notifyDataSetChanged()
    }

}
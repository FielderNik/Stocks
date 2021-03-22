package com.example.stocks.adapters


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.stocks.R
import com.example.stocks.StockActivity
import com.example.stocks.api.Api
import com.example.stocks.model.Quote
import com.example.stocks.model.Stock
import com.example.stocks.viewmodel.StockViewModel
import kotlin.coroutines.coroutineContext


class AdapterListStock(context: Context): RecyclerView.Adapter<AdapterListStock.ViewHolder>(){

    val inflater = LayoutInflater.from(context)
    var stocks: List<Stock> = emptyList<Stock>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var ivLogoStockItem: ImageView
        var tvTickerItem: TextView? = null
        var tvCompanyNameItem: TextView? = null
        var tvCurrentPriceItem: TextView? = null
        var tvDiffPriceItem: TextView? = null
        lateinit var ivFavoriteItem: ImageView
        var itemLayout: View? = null


        init {
            ivLogoStockItem = view.findViewById(R.id.ivLogoStockItem)
            tvTickerItem = view.findViewById(R.id.tvTickerItem)
            tvCompanyNameItem = view.findViewById(R.id.tvCompanyNameItem)
            tvCurrentPriceItem = view.findViewById(R.id.tvStockCurrentPriceItem)
            tvDiffPriceItem = view.findViewById(R.id.tvDiffPriceItem)
            ivFavoriteItem = view.findViewById(R.id.ivFavoriteItem)



            view.setOnClickListener{view ->
                val intent = Intent(view.context, StockActivity::class.java)
                intent.putExtra("ticker", tvTickerItem?.text)
                view.context.startActivity(intent)
//                Toast.makeText(view.context, "ticker: ${tickerTv?.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.activity_item_rv, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else {
//            holder.itemView.setBackgroundColor(Color.parseColor("#DBE2EA"))
            holder.itemView.setBackgroundResource(R.drawable.layout_bg)
        }

        if (stocks[position].favorites == true){
            holder.ivFavoriteItem.setImageResource(R.drawable.star_on)
        } else {
            holder.ivFavoriteItem.setImageResource(R.drawable.star_off)
        }

        holder.tvTickerItem?.text = stocks[position].ticker
        holder.tvCompanyNameItem?.text = stocks[position].name

        val currentPrice = stocks[position].currentPrice
        holder.tvCurrentPriceItem?.text = String.format("$%1.2f", currentPrice)

        val openPrice = stocks[position].openPriceOfTheDay
        val diffPrice = currentPrice - openPrice
        var diffPercent: Double = 0.0
        if (openPrice == 0.0){
            diffPercent = 0.0
        } else {
            diffPercent = diffPrice/openPrice*100
        }

        if (diffPrice >= 0){
            holder.tvDiffPriceItem?.setTextColor(Color.parseColor("#24B25D"))
            holder.tvDiffPriceItem?.text = String.format("+$%1.2f (%1.2f%%)", diffPrice, diffPercent)
        } else if (diffPrice < 0){
            holder.tvDiffPriceItem?.setTextColor(Color.parseColor("#B22424"))
            holder.tvDiffPriceItem?.text = String.format("-$%1.2f (%1.2f%%)", diffPrice, diffPercent)
        }




        val imageAddress = stocks[position].logo.toString()
        Glide
            .with(holder.itemView)
            .load(imageAddress)
            .placeholder(R.mipmap.ic_launcher)
            .override(Target.SIZE_ORIGINAL, 150)
            .centerCrop()
            .into(holder.ivLogoStockItem)
    }

    override fun getItemCount() = stocks.size

    fun refreshData(_stocks: List<Stock>){
        this.stocks = _stocks
        notifyDataSetChanged()
    }

/*    fun refreshDataQuotes(_quotes: List<Quote>){
        Thread.sleep(10000)
        this.quotes = _quotes
        notifyDataSetChanged()
    }*/

}
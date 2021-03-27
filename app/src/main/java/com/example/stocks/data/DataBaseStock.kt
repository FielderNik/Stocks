package com.example.stocks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stocks.model.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Stock::class], version = 1, exportSchema = false)
abstract class DataBaseStock : RoomDatabase(){

    abstract fun stockDao(): DaoStock


    companion object{
        @Volatile
        private var INSTANCE: DataBaseStock? = null


        fun getDataBase(context: Context): DataBaseStock{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, DataBaseStock::class.java, "table_stock")
                    .addCallback(object : Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                for (stock in stockList){
                                    INSTANCE?.stockDao()?.addStock(stock)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        val stockList: List<Stock> = listOf(
        Stock("US","USD","NASDAQ NMS - GLOBAL MARKET","Technology","1980-12-12","https://finnhub.io/api/logo?symbol=AAPL",
            2014404.0, "Apple Inc", "14089961010.0", 16788.096, "AAPL","https://www.apple.com/", true, 123.7914, 120.33),
        Stock("US","USD","NASDAQ NMS - GLOBAL MARKET","Retail","1997-05-15", "https://finnhub.io/api/logo?symbol=AMZN",
            1548442.0,"Amazon.com Inc","12062661000.0",503.564743,"AMZN","https://www.amazon.com/", false, 3123.34, 3067.85),
        Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Retail", "2015-04-16", "https://finnhub.io/api/logo?symbol=ETSY", 27152.28,
            "ETSY Inc", "17188557956.0", 126.049276, "ETSY", "https://www.etsy.com/", false, 219.82, 210.312),
        Stock("US", "USD","NEW YORK STOCK EXCHANGE, INC.", "Automobiles", "1956-03-07", "https://finnhub.io/api/logo?symbol=F",
            50137.63, "Ford Motor Co", "13133223000.0", 3978.695017, "F", "https://www.ford.com/", false, 12.845, 12.85),
        Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Media", "2012-05-18", "https://finnhub.io/api/logo?symbol=FB",
            826137.5, "Facebook Inc", "16506187714.0", 2847.669951, "FB", "https://www.facebook.com", false, 297.255, 290.45),
        Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Media", "2004-08-19", "https://finnhub.io/api/logo?symbol=GOOGL",
            1371768.0, "Alphabet Inc", "16502530000.0", 674.136665, "GOOGL", "https://abc.xyz/", true, 2041.07, 2027.63),
        Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Road & Rail", "2019-03-29", "https://finnhub.io/api/logo?symbol=LYFT",
            21735.74, "Lyft Inc", "18442502773.0", 328.930746, "LYFT", "https://www.lyft.com/", true, 64.89, 66.68),
        Stock("US", "USD", "NASDAQ NMS - GLOBAL MARKET", "Technology", "1986-03-13", "https://finnhub.io/api/logo?symbol=MSFT",
            marketCapitalization=1737349.0, name="Microsoft Corp", phone="14258828080.0", shareOutstanding=7542.215767, ticker="MSFT", weburl="https://www.microsoft.com/en-us", favorites=true, currentPrice=236.46, openPriceOfTheDay=230.27),
        Stock("US", currency="USD", exchange="NEW YORK STOCK EXCHANGE, INC.", finnhubIndustry="Technology", ipo="1986-03-12", logo="https://finnhub.io/api/logo?symbol=ORCL",
            marketCapitalization=191063.0, name="Oracle Corp", phone="16505067000.0", shareOutstanding=2883.535, ticker="ORCL", weburl="https://www.oracle.com/", favorites=true, currentPrice=66.33, 66.0)
        )

    }



}
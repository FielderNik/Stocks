package com.example.stocks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stocks.model.Stock

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
                val instance = Room.databaseBuilder(context.applicationContext, DataBaseStock::class.java, "table_stock").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
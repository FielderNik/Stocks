package com.example.stocks

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

open class MyApplication: Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var cont: Context

    }


    override fun onCreate() {
        cont = baseContext
        super.onCreate()
    }

}
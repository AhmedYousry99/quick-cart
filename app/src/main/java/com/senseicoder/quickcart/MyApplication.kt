package com.senseicoder.quickcart

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {


    val context = SupervisorJob() + Dispatchers.Main
    override fun onCreate() {
        super.onCreate()
        //to initialize currency
        SharedPref.init(this)
        CurrencySharedPref.context = this
    }


}
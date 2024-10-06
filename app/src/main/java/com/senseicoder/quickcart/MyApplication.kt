package com.senseicoder.quickcart

import android.app.Application
import com.senseicoder.quickcart.core.services.SharedPrefsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {

    val context = SupervisorJob() + Dispatchers.Main
    override fun onCreate() {
        super.onCreate()
        //to initialize currency
        SharedPrefsService.init(this@MyApplication.applicationContext)
        SharedPref.init(this)
        CurrencySharedPref.context = this
    }


}
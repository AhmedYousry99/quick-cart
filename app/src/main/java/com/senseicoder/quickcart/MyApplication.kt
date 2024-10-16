package com.senseicoder.quickcart

import android.app.Application
import com.senseicoder.quickcart.core.services.SharedPrefsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPrefsService.init(this@MyApplication.applicationContext)

    }


}
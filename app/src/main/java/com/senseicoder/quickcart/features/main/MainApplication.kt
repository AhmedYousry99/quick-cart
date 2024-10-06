package com.senseicoder.quickcart.features.main

import android.app.Application
import android.util.Log
import com.senseicoder.quickcart.core.services.SharedPrefsService

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: called")
        SharedPrefsService.init(applicationContext)
    }
    companion object{
        private const val TAG = "MainApplication"
    }
}
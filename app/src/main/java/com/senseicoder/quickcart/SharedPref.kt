package com.senseicoder.quickcart

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    lateinit var context: Application
    lateinit var sharedPreferences: SharedPreferences

    fun init(context: Application) {
        SharedPref.context = context
        sharedPreferences = context.getSharedPreferences("CUSTOMER_PREF", Context.MODE_PRIVATE)
    }
}
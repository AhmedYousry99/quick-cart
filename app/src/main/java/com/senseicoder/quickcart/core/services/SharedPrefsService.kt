package com.senseicoder.quickcart.core.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.senseicoder.quickcart.core.global.Constants

object SharedPrefsService: SharedPrefs{

    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context){

            sharedPrefs = context.getSharedPreferences(Constants.SharedPrefs.Settings.SETTINGS, Context.MODE_PRIVATE)
    }

    override fun setSharedPrefString(key: String, value: String) {
        val editor = sharedPrefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun getSharedPrefString(key: String, defaultValue: String): String {
        return sharedPrefs.getString(key, defaultValue)!!
    }

    override fun setSharedPrefBoolean(key: String, value: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    override fun getSharedPrefBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPrefs.getBoolean(key, defaultValue)
    }


    private const val TAG = "CustomerRepoImpl"


}
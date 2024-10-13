package com.senseicoder.quickcart.core.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.features.login.LoginFragment
import com.senseicoder.quickcart.features.login.LoginFragment.Companion

object SharedPrefsService : SharedPrefs {

    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {

        sharedPrefs = context.getSharedPreferences(
            Constants.SharedPrefs.Settings.SETTINGS,
            Context.MODE_PRIVATE
        )
    }
    override fun setSharedPrefString(key: String, value: String) {
        val editor = sharedPrefs.edit()
        editor.putString(key, value)
        editor.apply()
    }
    override fun setSharedPrefFloat(key: String, value: Float) {
        val editor = sharedPrefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    override fun getSharedPrefFloat(key: String, value: Float): Float {
        return sharedPrefs.getFloat(key, value)
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

    override fun logAllSharedPref(TAG: String, nameOfFunction: String) {
        Constants.also {
            Log.d(
                TAG,
                "${nameOfFunction}:USER_ID ${getSharedPrefString(it.USER_ID, it.USER_ID_DEFAULT)}"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:USER_TOKEN ${
                    getSharedPrefString(
                        it.USER_TOKEN,
                        it.USER_TOKEN_DEFAULT
                    )
                }"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:USER_EMAIL ${
                    getSharedPrefString(
                        it.USER_EMAIL,
                        it.USER_ID_DEFAULT
                    )
                }"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:CART_ID ${getSharedPrefString(it.CART_ID, it.CART_ID_DEFAULT)}"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:USER_DISPLAY_NAME ${
                    getSharedPrefString(
                        it.USER_DISPLAY_NAME,
                        it.USER_DISPLAY_NAME_DEFAULT
                    )
                }"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:CURRENCY ${
                    getSharedPrefString(
                        it.CURRENCY,
                        it.CURRENCY_DEFAULT
                    )
                }"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:FIREBASE_USER_ID ${
                    getSharedPrefString(
                        it.FIREBASE_USER_ID,
                        it.FIREBASE_USER_ID_DEFAULT
                    )
                }"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:FIREBASE_USER_ID ${
                    getSharedPrefString(
                        it.USER_DISPLAY_NAME,
                        it.USER_DISPLAY_NAME_DEFAULT
                    )
                }"
            )
            Log.d(
                TAG,
                "${nameOfFunction}:TOKEN_EXPIRATION_DATE ${
                    getSharedPrefString(
                        it.TOKEN_EXPIRATION_DATE,
                        it.TOKEN_EXPIRATION_DATE_DEAFULT
                    )
                }"
            )
        }

    }
    fun setDataForLogout(){
        setSharedPrefString(Constants.CURRENCY, Constants.CURRENCY_DEFAULT)
        setSharedPrefString(Constants.USER_ID, Constants.USER_ID_DEFAULT)
        setSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT)
        setSharedPrefString(Constants.USER_EMAIL, Constants.USER_ID_DEFAULT)
        setSharedPrefString(Constants.CART_ID, Constants.CART_ID_DEFAULT)
        setSharedPrefString(Constants.FIREBASE_USER_ID, Constants.FIREBASE_USER_ID_DEFAULT)
        setSharedPrefString(Constants.USER_DISPLAY_NAME, Constants.USER_DISPLAY_NAME_DEFAULT)
        setSharedPrefFloat(Constants.PERCENTAGE_OF_CURRENCY_CHANGE,Constants.PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT)

    }

    override fun getCurrencyData(): Triple<String?, String?, Float?> {
        val code = getSharedPrefString(Constants.CURRENCY, Constants.CURRENCY_DEFAULT)
        val symbol = Constants.Currency.currencyMap[code]
        val rete = getSharedPrefFloat(Constants.PERCENTAGE_OF_CURRENCY_CHANGE, Constants.PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT)
        return Triple(code,symbol,rete)
    }


    private const val TAG = "CustomerRepoImpl"


}
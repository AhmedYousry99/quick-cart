package com.senseicoder.weatherwatcher.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.Locale

object LanguageUtils {


    @Suppress("DEPRECATION")
    fun changeLanguage(code: String, activity:Activity) {
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        Log.d(TAG, "changeLanguage: ${Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = activity.getSystemService(Context.LOCALE_SERVICE) as LocaleManager
            val localeList = LocaleList(locale)
            localeManager.applicationLocales = localeList
        } else {
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
        }
    }

    private const val TAG = "LanguageUtils"
}
package com.senseicoder.quickcart.features.splash.viewmodel

import androidx.lifecycle.ViewModel
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.services.SharedPrefs

class SplashViewModel(private val sharedPrefs: SharedPrefs): ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return sharedPrefs.getSharedPrefString(Constants.USER_ID, Constants.USER_ID_DEFAULT) != Constants.USER_ID_DEFAULT
    }
}
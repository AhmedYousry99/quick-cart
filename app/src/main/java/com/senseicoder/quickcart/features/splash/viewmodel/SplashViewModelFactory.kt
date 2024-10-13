package com.senseicoder.quickcart.features.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.services.SharedPrefs

@Suppress("UNCHECKED_CAST")
class SplashViewModelFactory(private val sharedPrefs: SharedPrefs): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SplashViewModel::class.java)){
            SplashViewModel(sharedPrefs) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
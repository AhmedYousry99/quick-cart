package com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo

class MainActivityViewModelFactory(private val currencyRepo : CurrencyRepo,private val addressRepo: AddressRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            MainActivityViewModel(currencyRepo,addressRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }

}
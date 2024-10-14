package com.senseicoder.quickcart.features.main.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.customer.CustomerRepo

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val customerRepo: CustomerRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ProfileViewModel::class.java)){
            ProfileViewModel(customerRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
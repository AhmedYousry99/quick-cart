package com.senseicoder.quickcart.features.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.customer.CustomerRepo

@Suppress("UNCHECKED_CAST")
class SignupViewModelFactory(private val customerRepo: CustomerRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SignupViewModel::class.java)){
            SignupViewModel(customerRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
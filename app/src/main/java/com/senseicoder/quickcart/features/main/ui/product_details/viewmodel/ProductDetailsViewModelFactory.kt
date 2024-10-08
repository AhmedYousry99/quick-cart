package com.senseicoder.quickcart.features.main.ui.product_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.cart.CartRepo

class ProductDetailsViewModelFactory(private val cartRepo: CartRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)){
            ProductDetailsViewModel(cartRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
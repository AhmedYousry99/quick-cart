package com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.cart.CartRepo

@Suppress("UNCHECKED_CAST")
class ShoppingCartViewModelFactory (private val cartRepo: CartRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)){
            ShoppingCartViewModel(cartRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}

package com.senseicoder.quickcart.features.main.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import com.senseicoder.quickcart.features.main.ui.product_details.viewmodel.ProductDetailsViewModel

class FavoriteViewModelFactory(private val favoriteRepo: FavoriteRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(favoriteRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
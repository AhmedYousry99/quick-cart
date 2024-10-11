package com.senseicoder.quickcart.features.main.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.product.ProductsRepo

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val repository: ProductsRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
            SearchViewModel(repository) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
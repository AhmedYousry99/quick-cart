package com.senseicoder.quickcart.features.main.ui.address.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.address.AddressRepo

@Suppress("UNCHECKED_CAST")
class AddressViewModelFactory(private val addressRepo: AddressRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            AddressViewModel(addressRepo) as T
        } else {
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}
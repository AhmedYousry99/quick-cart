package com.senseicoder.quickcart.features.main.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import com.senseicoder.quickcart.core.repos.customer.CustomerRepo

class ProfileViewModel(val customerRepo: CustomerRepo,val currencyRepo: CurrencyRepo) : ViewModel() {




    fun signOut(){
        customerRepo.signOut()
    }
}
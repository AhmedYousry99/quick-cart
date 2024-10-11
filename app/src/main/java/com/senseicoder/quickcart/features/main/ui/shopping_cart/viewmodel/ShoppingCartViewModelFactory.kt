package com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.repos.cart.CartRepo
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepo
import com.senseicoder.quickcart.core.repos.order.draft_order.DraftOrderRepo

@Suppress("UNCHECKED_CAST")
class ShoppingCartViewModelFactory (private val cartRepo: CartRepo
,private val draftOrderRepo: DraftOrderRepo,
                                    private val copoinsRepo: CouponsRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)){
            ShoppingCartViewModel(cartRepo,draftOrderRepo,copoinsRepo) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}

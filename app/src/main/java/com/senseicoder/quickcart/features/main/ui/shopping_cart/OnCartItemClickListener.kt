package com.senseicoder.quickcart.features.main.ui.shopping_cart

import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.databinding.PaymentProccessDialogBinding

interface OnCartItemClickListener {

    fun onProductClick(item :ProductOfCart)

    fun onPlusClick(item :ProductOfCart)

    fun onMinusClick(item :ProductOfCart)

    fun onDeleteClick(item :ProductOfCart)
}
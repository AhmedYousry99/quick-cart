package com.senseicoder.quickcart.features.main.ui.address

import com.senseicoder.quickcart.core.model.AddressOfCustomer

interface OnAddressClickListener {
    fun onDeleteClick(addressOfCustomer: AddressOfCustomer)
    fun onEditClick(addressOfCustomer: AddressOfCustomer)

}
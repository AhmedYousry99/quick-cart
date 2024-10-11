package com.senseicoder.quickcart.core.model

import android.util.Log
import com.apollographql.apollo.api.Optional
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput

data class AddressOfCustomer(
    val id: String,
    var address1: String,
    val address2: String?,
    var city: String,
    var country: String,
    var phone: String,
    var firstName: String,
    var lastName: String
){
    companion object{
         const val TAG = "AddressOfCustomer"
    }
}

fun AddressOfCustomer.toAddress():Address{
    return Address(
        address1 = address1,
        city = city,
        province = country,
        zip = country,
        country = country
    )
}

fun AddressOfCustomer.toMailingAddressInput(): MailingAddressInput {
    Log.d(AddressOfCustomer.TAG, "toMailingAddressInput:${country}")
    return MailingAddressInput(
        address1 = Optional.present(address1),
        address2 = Optional.present(address2),
        city = Optional.present(city),
        country = Optional.present(country),
        phone = Optional.present(phone),
        firstName = Optional.present(firstName),
        lastName = Optional.present(lastName),
    )
}

fun CustomerAddressesQuery.DefaultAddress.toAddressOfCustomer(): AddressOfCustomer {
    return AddressOfCustomer(
        id,
        address1 ?: "",
        address2,
        city ?: "",
        country ?: "",
        phone ?: "",
        firstName ?: "",
        lastName ?: ""
    )
}

fun List<CustomerAddressesQuery.Edge>.fromEdges(): List<AddressOfCustomer> {
    val list = mutableListOf<AddressOfCustomer>()
    this.forEach {
        it.apply {
            list.add(
                AddressOfCustomer(
                    node.id,
                    it.node.address1 ?: "",
                    node.address2,
                    node.city ?: "",
                    node.country ?: "",
                    node.phone ?: "",
                    node.firstName ?: "",
                    node.lastName ?: ""
                )
            )
        }
    }
    return list
}

fun List<CustomerDefaultAddressUpdateMutation.Node>?.fromNodes(): List<AddressOfCustomer> {
    val list = mutableListOf<AddressOfCustomer>()
    this?.forEach {
        it.apply {
            list.add(
                AddressOfCustomer(
                    id ?: "",
                    address1 ?: "",
                    address2 ?: "",
                    city ?: "",
                    country ?: "",
                    phone ?: "",
                    firstName ?: "",
                    lastName ?: ""
                )
            )
        }
    }
    return list
}



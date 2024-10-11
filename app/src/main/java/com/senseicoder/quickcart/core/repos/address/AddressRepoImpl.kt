package com.senseicoder.quickcart.core.repos.address

import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.model.fromNodes
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddressRepoImpl(val remote: StorefrontHandler, val local: SharedPrefs) : AddressRepo {

    private var token =
        local.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT)

    override suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?> {
        updateToken()
        return flow {
            remote.getCustomerAddresses(token).collect{
                emit(it)
            }
        }
    }

    override suspend fun updateCustomerAddress(
        address: MailingAddressInput,
        id: String
    ): Flow<String?> {
        updateToken()
        return  flow {
            remote.updateCustomerAddress(address,token,id).collect{
                emit(it)
            }
        }
    }

    override suspend fun deleteAddress(id: String): Flow<String?> {
        updateToken()
        return flow {
            remote.deleteAddress(id,token).collect{
                emit(it)
            }
        }
    }

    override suspend fun createAddress(
        customerAddress: MailingAddressInput
    ): Flow<String?> {
        updateToken()
        return flow {
            remote.createAddress(customerAddress,token).collect{
                emit(it)
            }
        }
    }

    override suspend fun updateDefaultAddress(id: String): Flow<CustomerDefaultAddressUpdateMutation.CustomerDefaultAddressUpdate> {
        updateToken()
        return flow {
            remote.updateDefaultAddress(token,id)?.collect{
                emit(it)
            }
        }
    }

    override fun updateToken() {
        token = local.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT)
    }

}
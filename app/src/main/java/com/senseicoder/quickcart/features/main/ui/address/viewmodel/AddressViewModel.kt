package com.senseicoder.quickcart.features.main.ui.address.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.toCustomerOfDefault
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.model.toAddressOfCustomer
import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class AddressViewModel(private val addressRepo: AddressRepo) : ViewModel() {

    private val _allAddresses: MutableStateFlow<ApiState< CustomerAddressesQuery. Customer>> = MutableStateFlow(ApiState.Loading)
    val allAddresses = _allAddresses


    private val _updateAddress: MutableStateFlow<ApiState<String>> =
        MutableStateFlow(ApiState.Loading)
    val updateAddress = _updateAddress

    private val _deletedAddress: MutableStateFlow<ApiState<String>> =
        MutableStateFlow(ApiState.Loading)
    val deletedAddress = _deletedAddress

    private val _createdAddress: MutableStateFlow<ApiState<String>> =
        MutableStateFlow(ApiState.Loading)
    val createdAddress = _createdAddress

    private val _updateDefaultAddressProcess:MutableSharedFlow<Boolean> = MutableSharedFlow()
    val updateDefaultAddressProcess = _updateDefaultAddressProcess





    fun getCustomerAddresses() {
        _allAddresses.value = ApiState.Loading
        viewModelScope.launch {
            addressRepo.getCustomerAddresses().catch {
                _allAddresses.value = ApiState.Failure(it.message.toString())
            }.collect {
                    if (it  != null)
                         _allAddresses.value = ApiState.Success(it)
                else
                    _allAddresses.value = ApiState.Failure("No data found")
            }
        }
    }

    fun updateAddress(id: String, address: MailingAddressInput) {
        viewModelScope.launch {
            addressRepo.updateCustomerAddress(address, id)
                .catch {
                    _updateAddress.value = ApiState.Failure(it.message.toString())
                }.collect {
                    _updateAddress.value = ApiState.Success(it.toString())
                }
        }
    }

    fun updateDefaultAddress(id: String) {
        viewModelScope.launch {
            _allAddresses.value = ApiState.Loading
            addressRepo.updateDefaultAddress(id)?.catch {
                _allAddresses.value = ApiState.Failure(it.message.toString())
                _updateDefaultAddressProcess.emit(false)
            }?.collect {
                getCustomerAddresses()
                _updateDefaultAddressProcess.emit(true)
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            addressRepo.deleteAddress(id).catch {
                _deletedAddress.value = ApiState.Failure(it.message.toString())
            }.collect {
                _deletedAddress.value = ApiState.Success(it.toString())
            }
        }.invokeOnCompletion {
            getCustomerAddresses()
        }
    }

    fun createAddress(address: MailingAddressInput) {
        viewModelScope.launch {
            _createdAddress.value = ApiState.Loading
            addressRepo.createAddress(address).catch {
                _createdAddress.value = ApiState.Failure(it.message.toString())
            }.collect {
                _createdAddress.value = ApiState.Success(it.toString())
            }
        }.invokeOnCompletion {
            getCustomerAddresses()
        }
    }


}

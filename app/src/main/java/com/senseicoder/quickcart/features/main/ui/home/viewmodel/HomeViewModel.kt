package com.senseicoder.quickcart.features.main.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.DiscountCode
import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.coupons.CouponsRemoteImpl
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepo
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepoImpl
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(
    private val repoInterface: ProductsRepoInterface = ProductsRepo(),
    private val couponsRepo: CouponsRepo = CouponsRepoImpl(CouponsRemoteImpl() )
) :
    ViewModel() {

    var brands = MutableStateFlow<ApiState<List<DisplayBrand>>>(ApiState.Loading)


    init {
        getBrand()
        fetchCoupons()
    }

    fun getBrand() {
        viewModelScope.launch {
            try {
                repoInterface.getAllBrand().catch { e ->
                    brands.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
                }.collect { data ->
                    brands.value = ApiState.Success(data)
                }
            } catch (e: Exception) {
                brands.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }
        }
    }

    private val _coupons: MutableStateFlow<ApiState<PriceRulesResponse>> =
        MutableStateFlow(ApiState.Loading)
    val coupons: MutableStateFlow<ApiState<PriceRulesResponse>> = _coupons


    fun fetchCoupons() {
        viewModelScope.launch {

            couponsRepo.fetchCoupons().catch { e ->
                _coupons.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }.collect { data ->
                _coupons.value = ApiState.Success(data)
            }

        }
    }

    private val _couponsDetails: MutableStateFlow<ApiState<DiscountCodesResponse>> = MutableStateFlow(ApiState.Loading)
    val couponsDetails: MutableStateFlow<ApiState<DiscountCodesResponse>> = _couponsDetails
    fun getCouponsDetails(id: String) {
        viewModelScope.launch {
            couponsRepo.checkCouponDetails(id).catch { e ->
                _couponsDetails.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }.collect { data ->
                _couponsDetails.value = ApiState.Success(data)
            }
        }
    }

}
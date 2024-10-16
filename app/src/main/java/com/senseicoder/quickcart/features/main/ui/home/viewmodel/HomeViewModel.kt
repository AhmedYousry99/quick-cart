package com.senseicoder.quickcart.features.main.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.coupons.CouponsRemoteImpl
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepo
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepoImpl
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repoInterface: ProductsRepoInterface = ProductsRepo(),
    private val couponsRepo: CouponsRepo = CouponsRepoImpl(CouponsRemoteImpl())
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

    private val _coupons: MutableSharedFlow<ApiState<PriceRulesResponse>> = MutableSharedFlow(replay = 1)
    val coupons = _coupons.asSharedFlow()


    fun fetchCoupons() {
        viewModelScope.launch {
            try {
                val res: PriceRulesResponse = couponsRepo.fetchCoupons()
                _coupons.emit(ApiState.Success(res))
            } catch (e: Exception) {
                _coupons.emit(ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN))
            }
        }
    }
}
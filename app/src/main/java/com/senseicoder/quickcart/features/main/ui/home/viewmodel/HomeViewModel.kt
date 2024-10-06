package com.senseicoder.quickcart.features.main.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.repo.product.ProductsRepo
import com.senseicoder.quickcart.core.repo.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(
    private val repoInterface: ProductsRepoInterface = ProductsRepo(),

    ) :
    ViewModel() {

    var brands = MutableStateFlow<ApiState<List<DisplayBrand>>>(ApiState.Loading)


    init {

        getBrand()
    }

    fun getBrand() {
        viewModelScope.launch {
            try {
                repoInterface.getAllBrand().catch { e ->
                    brands.value = ApiState.Failure(e)
                }.collect { data ->
                    brands.value = ApiState.Success(data)
                }
            } catch (e: Exception) {
                brands.value = ApiState.Failure(e)
            }

        }

    }


}
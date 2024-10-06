package com.senseicoder.quickcart.features.main.ui.brand.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.repo.product.ProductsRepo
import com.senseicoder.quickcart.core.repo.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.Exception

class BrandViewModel(private val repoInterface: ProductsRepoInterface = ProductsRepo()) :
    ViewModel() {

    var products = MutableStateFlow<ApiState<List<DisplayProduct>>>(ApiState.Loading)
    var dataFiltered: List<DisplayProduct> = listOf()
    var allData: List<DisplayProduct> = listOf()
    var filter = false
    var maxPrice = 0

    fun getProductInBrand(brand: String) {
        viewModelScope.launch {
            try {
                repoInterface.getAllProductInBrand(brand).catch { e ->
                    products.value = ApiState.Failure(e)
                }.collect { data ->
                    products.value = ApiState.Success(data)
                }
            } catch (e: Exception) {
                products.value = ApiState.Failure(e)
            }
        }
    }

    fun seekMax() {
        allData.forEach {
            if (maxPrice.toFloat() < it.price.toFloat()) {
                maxPrice = it.price.toFloat().toInt() + 1
            }
        }
    }

    fun filterByPrice(price: String) {
        if (filter) {
            dataFiltered = allData.filter {
                (0.00 <= it.price.toFloat() && it.price.toFloat() <= price.toFloat())
            }
            if (price == "") {
                products.value = ApiState.Success(allData)
            } else {
                products.value = ApiState.Success(dataFiltered)
            }
        } else {
            products.value = ApiState.Success(allData)
        }
    }

    fun displayAllProducts() {
        products.value = ApiState.Success(allData)
    }

    fun filterByCategory(category: String) {
        if (filter) {
            dataFiltered = allData.filter {
                it.product_type == category
            }
            products.value = ApiState.Success(dataFiltered)

        } else {
            products.value = ApiState.Success(allData)
        }
    }


}
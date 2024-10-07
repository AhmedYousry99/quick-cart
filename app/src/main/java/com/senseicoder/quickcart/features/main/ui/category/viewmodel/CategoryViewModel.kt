package com.senseicoder.quickcart.features.main.ui.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel(private val repoInterface: ProductsRepoInterface = ProductsRepo()) :
    ViewModel() {

    var products = MutableStateFlow<ApiState<List<DisplayProduct>>>(ApiState.Loading)
    private lateinit var productMainCategory: List<DisplayProduct>
    private var productSubCategory: MutableList<DisplayProduct> = mutableListOf()
    var allData: List<DisplayProduct> = listOf()
    var filterMainCategory = false
    var filterSubCategory = false

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            try {
                repoInterface.getAllProduct().catch { e ->
                    products.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
                }.collect { data ->
                    products.value = ApiState.Success(data)
                }
            } catch (e: Exception) {
                products.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }
        }
    }

    fun filterByMainCategory(mainCategory: String) {
        if (filterMainCategory) {
            productMainCategory = allData.filter {
                it.product_type == mainCategory
            }
            products.value = ApiState.Success(productMainCategory)

        } else {
            products.value = ApiState.Success(allData)
        }
    }

    fun filterBySubCategory(subCategory: String) {

        if (filterMainCategory) {
            if (filterSubCategory) {
                productSubCategory.clear()
                productMainCategory.forEach { product ->
                    val strings = product.tag.split(",")
                    strings.forEach {
                        if (subCategory.trim().equals(it.trim(), true)) {
                            productSubCategory.add(product)
                        }
                    }
                }
                products.value = ApiState.Success(productSubCategory)
            } else {
                products.value = ApiState.Success(productMainCategory)
            }
        } else {
            products.value = ApiState.Success(allData)
        }
    }
}
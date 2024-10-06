package com.senseicoder.quickcart.features.main.ui.category.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.repo.product.ProductsRepo
import com.senseicoder.quickcart.core.repo.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.RemoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel(private val repoInterface: ProductsRepoInterface = ProductsRepo()) :
    ViewModel() {

    var products = MutableStateFlow<RemoteStatus<List<DisplayProduct>>>(RemoteStatus.Loading)
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
                    products.value = RemoteStatus.Failure(e)
                }.collect { data ->
                    products.value = RemoteStatus.Success(data)
                }
            } catch (e: Exception) {
                products.value = RemoteStatus.Failure(e)
            }
        }
    }

    fun filterByMainCategory(mainCategory: String) {
        if (filterMainCategory) {
            productMainCategory = allData.filter {
                it.product_type == mainCategory
            }
            products.value = RemoteStatus.Success(productMainCategory)

        } else {
            products.value = RemoteStatus.Success(allData)
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
                products.value = RemoteStatus.Success(productSubCategory)
            } else {
                products.value = RemoteStatus.Success(productMainCategory)
            }
        } else {
            products.value = RemoteStatus.Success(allData)
        }
    }
}
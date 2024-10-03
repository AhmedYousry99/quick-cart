package com.senseicoder.quickcart.features.main.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.repo.product.ProductsRepo
import com.senseicoder.quickcart.core.repo.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.RemoteStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(
    private val repoInterface: ProductsRepoInterface = ProductsRepo(),

    ) :
    ViewModel() {

    var brands = MutableStateFlow<RemoteStatus<List<DisplayBrand>>>(RemoteStatus.Loading)


    init {

        getBrand()
    }

    fun getBrand() {
        viewModelScope.launch {
            try {
                repoInterface.getAllBrand().catch { e ->
                    brands.value = RemoteStatus.Failure(e)
                }.collect { data ->
                    brands.value = RemoteStatus.Success(data)
                }
            } catch (e: Exception) {
                brands.value = RemoteStatus.Failure(e)
            }

        }

    }


}
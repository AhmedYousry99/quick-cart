package com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel : ViewModel() {
    private val _currentProductId: MutableStateFlow<String> = MutableStateFlow(INIT)
    val currentProductId = _currentProductId

    fun setCurrentProductId(id: String) {
        _currentProductId.value = "${Constants.API.PRODUCT_ID_PREFIX}$id"
    }

    companion object{
        const val INIT: String = "init"
    }

    private  val _location : MutableStateFlow<Pair<Double,Double>> = MutableStateFlow(Pair(0.0,0.0))
    val location = _location

    fun setLocation(lat:Double,long:Double){
        _location.value = Pair(lat,long)
    }
}
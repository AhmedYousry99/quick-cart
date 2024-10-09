package com.senseicoder.quickcart.features.main.ui.main_activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel : ViewModel() {
    // TODO IN CASE FIRST TIME CHECK LENGTH OF ID
    private val _currentProductId: MutableStateFlow<String> = MutableStateFlow("")
    val currentProductId = _currentProductId

    fun setCurrentProductId(id: String) {
        _currentProductId.value = id
    }

    private  val _location : MutableStateFlow<Pair<Double,Double>> = MutableStateFlow(Pair(0.0,0.0))
    val location = _location

    fun setLocation(lat:Double,long:Double){
        _location.value = Pair(lat,long)
    }
}
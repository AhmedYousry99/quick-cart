package com.senseicoder.quickcart.features.main.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepo: FavoriteRepo) : ViewModel() {

    private val _isFavorite = MutableStateFlow<ApiState<Boolean>>(ApiState.Init)
    val isFavorite = _isFavorite.asStateFlow()

    private val _favorites = MutableStateFlow<ApiState<List<FavoriteDTO>>>(ApiState.Loading)
    val favorites = _favorites.asStateFlow()

    fun addToFavorite(product: ProductDTO) {
        viewModelScope.launch (Dispatchers.IO){
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.addFavorite(firebaseId, product).catch {
                _isFavorite.emit(ApiState.Failure("failed to add favorite"))
            }.collect{
                _isFavorite.value = ApiState.Success(true)
            }
        }
    }

    fun checkIfFavorite(firebaseUserId: String, productId: String) {
        viewModelScope.launch {
            favoriteRepo.isFavorite(firebaseUserId, productId).catch {
                _isFavorite.emit(ApiState.Success(false))
            }.collect{
                _isFavorite.value = ApiState.Success(it)
            }
        }
    }

    fun removeFromFavorite(product: ProductDTO) {
        viewModelScope.launch (Dispatchers.IO){
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.removeFavorite(firebaseId, product).catch {
                _isFavorite.emit(ApiState.Failure("failed to delete favorite"))
            }.collect{
                _isFavorite.value = ApiState.Success(false)
            }
        }
    }

    fun removeFromFavorite(favorite: FavoriteDTO) {
        viewModelScope.launch (Dispatchers.IO){
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.removeFavorite(firebaseId, favorite).catch {
                _isFavorite.emit(ApiState.Failure("failed to delete favorite"))
            }.collect{
                _favorites.value = ApiState.Success((_favorites.value as ApiState.Success).data.filter { it.id != favorite.id })
                _isFavorite.value = ApiState.Success(false)
            }
        }
    }

    fun getFavorites(){
        viewModelScope.launch (Dispatchers.IO){
            _favorites.emit(ApiState.Init)
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.getFavorites(firebaseId).catch {
                _favorites.emit(ApiState.Failure("failed to get favorites"))
            }.collect{
                _favorites.value = ApiState.Success(it)
            }
        }
    }
}
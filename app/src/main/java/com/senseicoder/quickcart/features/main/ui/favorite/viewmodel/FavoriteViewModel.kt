package com.senseicoder.quickcart.features.main.ui.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepo: FavoriteRepo,
                        private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val _isFavorite = MutableSharedFlow<ApiState<Boolean>>()
    val isFavorite = _isFavorite.asSharedFlow()

    private val _favorites = MutableStateFlow<ApiState<List<FavoriteDTO>>>(ApiState.Init)
    val favorites = _favorites.asStateFlow()

    fun addToFavorite(product: ProductDTO) {
        viewModelScope.launch (dispatcher){
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.addFavorite(firebaseId, favoriteRepo.revertPricesAccordingToCurrency(product)).catch {
                _isFavorite.emit(ApiState.Failure("failed to add favorite"))
            }.collect{
                _isFavorite.emit(ApiState.Success(true))
            }
        }
    }

    fun checkIfFavorite(firebaseUserId: String, productId: String) {
        viewModelScope.launch (dispatcher){
            favoriteRepo.isFavorite(firebaseUserId, productId).catch {
                _isFavorite.emit(ApiState.Success(false))
            }.collect{
                _isFavorite.emit(ApiState.Success(it))
            }
        }
    }

    fun removeFromFavorite(product: ProductDTO) {
        viewModelScope.launch (dispatcher){
            _isFavorite.emit(ApiState.Loading)
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.removeFavorite(firebaseId, product).catch {
                _isFavorite.emit(ApiState.Failure("failed to delete favorite"))
            }.collect{
                _isFavorite.emit(ApiState.Success(false))
            }
        }
    }

    fun removeFromFavorite(favorite: FavoriteDTO) {
        viewModelScope.launch (dispatcher){
            _isFavorite.emit(ApiState.Loading)
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.removeFavorite(firebaseId, favorite).catch {
                _isFavorite.emit(ApiState.Failure("failed to delete favorite"))
            }.collect{
                val favorites = _favorites.stateIn(CoroutineScope(Dispatchers.Default)).value
                Log.d(TAG, "removeFromFavorite: $favorites")
                if(favorites is ApiState.Success)
                    _favorites.emit(ApiState.Success((_favorites.stateIn(CoroutineScope(Dispatchers.Default)).value as ApiState.Success).data.filter { it.id != favorite.id }))
                _isFavorite.emit(ApiState.Success(false))
            }
        }
    }

    fun getFavorites(){
        viewModelScope.launch (dispatcher){
            _favorites.emit(ApiState.Init)
            val firebaseId = favoriteRepo.getUserFirebaseID()
            favoriteRepo.getFavorites(firebaseId).map { it.map { favorite-> favoriteRepo.convertPricesAccordingToCurrency(favorite) } }.catch {
                _favorites.emit(ApiState.Failure("failed to get favorites"))
            }.collect{
                _favorites.emit(ApiState.Success(it))
            }
        }
    }

    companion object{
        private const val TAG = "FavoriteViewModel"
    }
}
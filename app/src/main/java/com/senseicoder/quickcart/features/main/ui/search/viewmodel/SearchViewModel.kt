package com.senseicoder.quickcart.features.main.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepoInterface
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(private val repository: ProductsRepoInterface = ProductsRepo(),
                      private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val _searchResults = MutableStateFlow<ApiState<List<ProductDTO>>>(ApiState.Init)
    val searchResults = _searchResults.asStateFlow()

    private val _suggestionResults = MutableStateFlow<ApiState<List<String>>>(ApiState.Init)
    val suggestionResults = _suggestionResults.asStateFlow()

    private val _listResult = MutableStateFlow<ApiState<List<ProductDTO>>>(ApiState.Init)
    val listResult = _listResult.asStateFlow()


    fun searchProducts(query: String) {
        viewModelScope.launch(dispatcher) {
            _suggestionResults.value = ApiState.Loading
            _searchResults.value = ApiState.Loading
            repository.getProductsByQuery(query).catch { msg ->
                _searchResults.emit(
                    ApiState.Failure(
                        msg.message ?: "something went wrong"
                    )
                )
            }.collect {
                updateSuggestions(it)
                _searchResults.value = ApiState.Success(it)
            }
        }
    }

    private suspend fun updateSuggestions(products: List<ProductDTO>){
        withContext(dispatcher){
            _suggestionResults.value = ApiState.Success(products.map { it.title })
        }
    }

    fun filterProducts(price: String) {

    }



    /* fun searchProducts(query: String) {
         viewModelScope.launch(Dispatchers.IO){
             try {
                 val response = repository.searchProducts(query)
                 _searchResults.value = response
             } catch (e: Exception) {
             }
         }
     }*/

}
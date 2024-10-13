package com.senseicoder.quickcart.repos

import com.senseicoder.quickcart.core.db.remote.RemoteDataSource
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource : RemoteDataSource {
    private val favorites = mutableMapOf<String, MutableList<FavoriteDTO>>() // To hold favorites by user

//    override  fun addFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
//        favorites.computeIfAbsent(firebaseId) { mutableListOf() }.add(favorite)
//        return flowOf(favorite) // Emit the favorite that was added
//    }

    override fun addFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        // Check if the favorite already exists for the user
        val userFavorites = favorites.computeIfAbsent(firebaseId) { mutableListOf() }
        if (userFavorites.any { it.id == favorite.id }) {
            // You can choose to emit the existing favorite or throw an exception
            return flowOf(favorite) // Return the existing favorite if already present
        }

        userFavorites.add(favorite) // Add favorite if it doesn't exist
        return flowOf(favorite) // Emit the favorite that was added
    }

    override  fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        favorites[firebaseId]?.remove(favorite)
        return flowOf(favorite) // Emit the favorite that was removed
    }

    override  fun isFavorite(firebaseId: String, productId: String): Flow<Boolean> {
        val isFav = favorites[firebaseId]?.any { it.id == productId } ?: false
        return flowOf(isFav)
    }

    override fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>> {
        return flowOf(favorites[firebaseId] ?: emptyList())
    }

    // Other methods can be left unimplemented or as stubs
    override  fun getUserByIdOrAddUser(customer: CustomerDTO): Flow<CustomerDTO> {
        TODO("Not implemented")
    }

    override  fun addUser(customer: CustomerDTO): Flow<CustomerDTO> {
        TODO("Not implemented")
    }

    override  fun getUserByEmail(customer: CustomerDTO): Flow<CustomerDTO> {
        TODO("Not implemented")
    }
}

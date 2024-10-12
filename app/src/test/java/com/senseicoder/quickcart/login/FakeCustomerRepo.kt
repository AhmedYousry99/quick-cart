package com.senseicoder.quickcart.login

import com.senseicoder.quickcart.core.repos.customer.CustomerRepo


import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class FakeCustomerRepo : CustomerRepo {
    var shouldReturnError = false

    override suspend fun signupUsingEmailAndPassword(firstName: String, lastName: String, email: String, password: String): Flow<CustomerDTO> {
        // Implement as needed for testing
        return flow { /* Not needed for this test */ }
    }

    override suspend fun loginUsingGuest(): Flow<CustomerDTO> {
        return flow {
            if (shouldReturnError) {
                throw Exception("Guest login failed")
            }
            delay(100) // Simulating network delay
            emit(CustomerDTO(
                id = "fake_guest_user_id",
                token = "fake_guest_user_token",
                email = "guest@example.com",
                displayName = "Guest User",
                cartId = "fake_guest_cart_id",
                firebaseId = "fake_firebase_id"
            ))
        }
    }

    override fun signOut() {}

    override fun setUserId(value: String) {}

    override fun getUserId(): String {
        return "fake_user_id"
    }

    override fun setUserToken(token: String) {}

    override fun getUserToken(): String {
        return "fake_user_token"
    }

    override fun setEmail(email: String) {}

    override fun setDisplayName(displayName: String) {}

    override fun setCartId(cartId: String) {}

    override fun getCartId(): String {
        return "fake_cart_id"
    }

    override fun setFirebaseId(firebaseId: String) {}

    override suspend fun addFavorite(email: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        // Implement as needed for testing
        return flow { /* Not needed for this test */ }
    }

    override suspend fun removeFavorite(email: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        // Implement as needed for testing
        return flow { /* Not needed for this test */ }
    }

    override suspend fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO> {
        return flow {
            if (shouldReturnError) {
                throw Exception("Login failed")
            }
            delay(100) // Simulate network delay
            emit(CustomerDTO(
                id = "fake_user_id",
                token = "fake_user_token",
                email = email,
                displayName = "Fake User",
                cartId = "fake_cart_id",
                firebaseId = "fake_firebase_id"
            ))
        }
    }
}

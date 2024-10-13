package com.senseicoder.quickcart.core.db.remote

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.senseicoder.quickcart.core.global.withoutGIDPrefix
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.customer.CustomerKeys
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.tasks.await
import java.io.IOException

object FirebaseFirestoreDataSource : RemoteDataSource {

    private const val TAG = "FirebaseFirestoreDataSo"

    init {
        FirebaseFirestore.setLoggingEnabled(true)
    }

    override suspend fun getUserByIdOrAddUser(customer: CustomerDTO) = flow {

        val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)

        try {
            val document = customersCollection.document(customer.firebaseId).get().await()
            if (document.exists()) {
                Log.d(TAG, "getUserByIdOrAddUser: user exists")
                emit(CustomerDTO.fromDocument(document))
            } else {
                // User doesn't exist, add the new user
                val res = customersCollection.add(customer).await()
                emit(customer.copy(firebaseId = res.id))
            }
        } catch (e: IllegalArgumentException) {
            Log.d(TAG, "getUserByIdOrAddUser: user doesn't exist, adding user: $customer")
            val res = customersCollection.add(customer).await()
            Log.d(TAG, "getUserByIdOrAddUser: added successfully")
            emit(customer.copy(firebaseId = res.id))
        }
    }

    override suspend fun addUser(customer: CustomerDTO) = flow<CustomerDTO> {
        Log.d(TAG, "addUser:  adding user: $customer")
        val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)
        val res = customersCollection.add(customer).await()
        emit(customer.copy(firebaseId = res.id))
    }.retryWhen { cause, attempt ->
        cause is IOException && attempt < 3
    }

    override suspend fun getUserByEmail(customer: CustomerDTO) = flow<CustomerDTO> {
        Log.d(TAG, "getUserByEmail: $customer")
        val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)
        val querySnapshot = customersCollection
            .whereEqualTo("email", customer.email)
            .get()
            .await()
        if (!querySnapshot.isEmpty) {
            Log.d(TAG, "getUserByEmail: documents: ${querySnapshot.documents}")
            // Assuming the first document contains the matching user
            val document = querySnapshot.documents.first()
            val firebaseCustomer = CustomerDTO.fromDocument(document)
            emit(firebaseCustomer.copy(token = customer.token))
        } else {
            Log.d(TAG, "getUserByEmail: documents: ${querySnapshot.documents}")
            throw (Exception("Invalid credentials"))
        }
    }


    override suspend fun addFavorite(firebaseId: String, favorite: FavoriteDTO) =
        flow<FavoriteDTO> {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val customersCollection =
                firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)

            val userDocument = customersCollection.document(firebaseId)
            userDocument.collection("favorites").document(favorite.id).set(favorite).await()
            emit(favorite)
        }

    override suspend fun isFavorite(firebaseId: String, productId: String) = flow<Boolean> {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val document =
            firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION).document(firebaseId)
                .collection("favorites")
                .document(productId.withoutGIDPrefix())
                .get().await()
        emit(document.exists())
    }

    override suspend fun removeFavorite(firebaseId: String, favorite: FavoriteDTO) =
        flow<FavoriteDTO> {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val customersCollection =
                firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)

            val userDocument = customersCollection.document(firebaseId)
            userDocument.collection("favorites").document(favorite.id).delete().await()
            emit(favorite)
        }

    override fun getFavorites(firebaseId: String) = flow {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)
        try {
            emit(
                customersCollection.document(firebaseId).collection("favorites").get().await()
                    .toObjects(FavoriteDTO::class.java)
            )
        } catch (e: Exception) {
            emit(emptyList())
        }
    }


}
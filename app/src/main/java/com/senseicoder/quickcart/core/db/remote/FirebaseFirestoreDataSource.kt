package com.senseicoder.quickcart.core.db.remote

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.customer.CustomerKeys
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirebaseFirestoreDataSource: RemoteDataSource{

    private const val TAG = "FirebaseFirestoreDataSo"

    init {
        FirebaseFirestore.setLoggingEnabled(true)
    }

    override suspend fun getUserByIdOrAddUser(customer: CustomerDTO) = flow {

        val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)

        try{
            val document =  customersCollection.document(customer.firebaseId).get().await()
            if (document.exists()) {
                Log.d(TAG, "getUserByIdOrAddUser: user exists")
                emit(CustomerDTO.fromDocument(document))
            } else {
                // User doesn't exist, add the new user
                val res = customersCollection.add(customer).await()
                emit(customer.copy(firebaseId = res.id))
            }
        }catch(e: IllegalArgumentException){
            Log.d(TAG, "getUserByIdOrAddUser: user doesn't exist, adding user: $customer")
            val res = customersCollection.add(customer).await()
            Log.d(TAG, "getUserByIdOrAddUser: added successfully")
            emit(customer.copy(firebaseId = res.id))
        }
    }

    override suspend fun getUserByEmail(customer: CustomerDTO)= flow<CustomerDTO> {
        Log.d(TAG, "getUserByEmail: $customer")
        val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)
        try {
            val querySnapshot = customersCollection
                .whereEqualTo("email", customer.email)
                .get()
                .await()
            if (!querySnapshot.isEmpty) {
                // Assuming the first document contains the matching user
                val document = querySnapshot.documents.first()
                val firebaseCustomer = CustomerDTO.fromDocument(document)
                emit(firebaseCustomer.copy(token = customer.token))
            } else {
                throw(Exception("Error getting user data"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserByEmail: ", e)
            throw(Exception("Error getting user data"))
        }
    }


    override suspend fun addFavorite(firebaseId: String, favorite: FavoriteDTO) = flow<FavoriteDTO> {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)

        val userDocument = customersCollection.document(firebaseId)
        userDocument.update("favorites", FieldValue.arrayUnion(favorite)).await()
        emit(favorite)
    }

    override suspend fun removeFavorite(firebaseId: String, favorite: FavoriteDTO) =flow<FavoriteDTO> {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)

        val userDocument = customersCollection.document(firebaseId)
        userDocument.update("favorites", FieldValue.arrayRemove(favorite)).await()
        emit(favorite)
    }


}
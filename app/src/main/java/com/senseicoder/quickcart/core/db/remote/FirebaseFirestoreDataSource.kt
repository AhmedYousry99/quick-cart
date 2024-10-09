package com.senseicoder.quickcart.core.db.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.customer.CustomerKeys
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
            Log.d(TAG, "getUserByIdOrAddUser: user doesn't exist, adding user")
            val res = customersCollection.add(customer).await()
            Log.d(TAG, "getUserByIdOrAddUser: added successfully")
            emit(customer.copy(firebaseId = res.id))
        }
    }

    override suspend fun getUserByEmail(email: String)= flow<CustomerDTO> {
        val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customersCollection = firestoreInstance.collection(CustomerKeys.CUSTOMERS_COLLECTION)
        try {
            val querySnapshot = customersCollection
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!querySnapshot.isEmpty) {
                // Assuming the first document contains the matching user
                val document = querySnapshot.documents.first()
                val customer = CustomerDTO.fromDocument(document)
                emit(customer)
            } else {
                throw(Exception("Error getting user data"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserByEmail: ", e)
            throw(Exception("Error getting user data"))
        }
    }


}
package com.senseicoder.quickcart.core.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.models.CustomerDTO
import com.senseicoder.quickcart.core.network.interfaces.FirebaseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirebaseHandlerImpl :FirebaseHandler{

    private val firebaseAuthInstance = FirebaseAuth.getInstance()
    override suspend fun signupUsingNormalEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) = flow<CustomerDTO> {
        val task = firebaseAuthInstance.createUserWithEmailAndPassword(email, password).await()
        val firstNameEmpty = firstName.isBlank()
        val lastNameEmpty = lastName.isBlank()

        val displayName = if(firstNameEmpty && lastNameEmpty) email else{
            if(!firstNameEmpty) firstName else lastName
        }
        emit(CustomerDTO(
            task.user!!.displayName ?: displayName,
            email,
            password
        ))
       /* val profileUpdates = userProfileChangeRequest {
            displayName = displayName
        }
        task.user!!.updateProfile(profileUpdates)*/
    }

    override suspend fun loginUsingNormalEmail(email: String, password: String) = flow<CustomerDTO> {
        val task = firebaseAuthInstance.signInWithEmailAndPassword(email, password).await()
        Log.d(TAG, "loginUsingNormalEmail: success")
        task.user.let {
            emit(CustomerDTO(
                it!!.displayName ?: Constants.Errors.UNKNOWN,
                it.email!!,
                password,
            ))
        }
    }

    override suspend fun updateDisplayName(customerDTO: CustomerDTO)= flow<CustomerDTO> {
        val user = firebaseAuthInstance.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = customerDTO.displayName
        }
        user!!.updateProfile(profileUpdates).await()
        emit(customerDTO)
    }

    override suspend fun loginUsingGuest() = flow<CustomerDTO> {
        val task = firebaseAuthInstance.signInAnonymously().await()
        emit(CustomerDTO(
            "",
            "",
            "",
            isGuest = true
        ))
    }


    override fun signOut() {
        firebaseAuthInstance.signOut()
    }

    private const val TAG = "FirebaseAuthHelperImpl"
}
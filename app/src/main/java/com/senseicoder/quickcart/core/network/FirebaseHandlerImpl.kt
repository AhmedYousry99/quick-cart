package com.senseicoder.quickcart.core.network

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.userProfileChangeRequest
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.network.interfaces.FirebaseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.tasks.await

object FirebaseHandlerImpl : FirebaseHandler {

    private val firebaseAuthInstance = FirebaseAuth.getInstance()
    override fun signupUsingNormalEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) = flow<CustomerDTO> {
        val task = firebaseAuthInstance.createUserWithEmailAndPassword(email, password).await()
        val firstNameEmpty = firstName.isBlank()
        val lastNameEmpty = lastName.isBlank()

        val displayName = if (firstNameEmpty && lastNameEmpty) email else {
            if (!firstNameEmpty && !lastNameEmpty)
                "$firstName $lastName"
            else if (!firstNameEmpty)
                firstName
            else
                lastName
        }
        Log.d(TAG, "signupUsingNormalEmail: $displayName")
        emit(
            CustomerDTO(
                displayName = task.user!!.displayName ?: displayName,
                email = email,
                password = password
            )
        )
        /* val profileUpdates = userProfileChangeRequest {
             displayName = displayName
         }
         task.user!!.updateProfile(profileUpdates)*/
    }.catch {
        if (it is FirebaseAuthUserCollisionException) {
            throw Exception(Constants.Errors.CustomerCreate.EMAIL_TAKEN)
        }
        throw it
    }

    override fun loginUsingNormalEmail(email: String, password: String) = flow<CustomerDTO> {
        val task = firebaseAuthInstance.signInWithEmailAndPassword(email, password).await()
        Log.d(TAG, "loginUsingNormalEmail: success")
        task.user.let {
            emit(
                CustomerDTO(
                    it!!.displayName ?: Constants.Errors.UNKNOWN,
                    it.email!!,
                    password,
                )
            )
        }
    }.retryWhen{ cause, attempt ->
        (cause !is FirebaseAuthInvalidCredentialsException)&& attempt < 3
    }.catch {
        if (it is FirebaseAuthInvalidCredentialsException) {
            throw Exception(Constants.Errors.Login.INVALID_CREDENTIALS)
        }else if(it is FirebaseNetworkException){
            throw Exception(Constants.Errors.NO_INTERNET)
        }
        throw it
    }

    override fun updateDisplayName(customerDTO: CustomerDTO) = flow<CustomerDTO> {
        val user = firebaseAuthInstance.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = customerDTO.displayName
        }
        user!!.updateProfile(profileUpdates).await()
        emit(customerDTO)
    }

    override fun handleEmailVerification(customer: CustomerDTO): Flow<CustomerDTO> = flow {
        val user = firebaseAuthInstance.currentUser
        if (user?.isEmailVerified == true) {
            emit(customer.copy(isVerified = true))
        } else {
            (user ?: firebaseAuthInstance.currentUser!!).sendEmailVerification().await()
            throw Exception(Constants.Errors.Firebase.EMAIL_NOT_VERIFIED)
        }
    }

    override fun sendEmailVerification(customer: CustomerDTO) = flow<CustomerDTO> {
        val user = firebaseAuthInstance.currentUser
        user!!.sendEmailVerification()
        emit(customer)
    }

    /*  override suspend fun loginUsingGuest() = flow<CustomerDTO> {
          val task = firebaseAuthInstance.signInAnonymously()
          emit(
              CustomerDTO(
              "",
              "",
              "",
              isGuest = true
          )
          )
      }*/


    override fun signOut() {
        firebaseAuthInstance.signOut()
    }

    private const val TAG = "FirebaseAuthHelperImpl"
}
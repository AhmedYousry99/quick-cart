package com.senseicoder.quickcart.core.model.customer

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO

data class CustomerDTO(
    val displayName: String = "unknown",
    val email: String = "unknown",
    val password: String = INVALID_PASSWORD,
    val id: String = INVALID_ID,
    @DocumentId
    val firebaseId: String = INVALID_ID,
    val isVerified: Boolean = false,
    val isGuest: Boolean = false,
    val token: String = "",
    val expireAt: Any? = null,
    val cartId: String = Constants.CART_ID_DEFAULT,
){
    val favorites: List<FavoriteDTO> = emptyList()
    companion object{
        fun fromDocument(document: DocumentSnapshot): CustomerDTO {
            val tempDisplayName = document.getString(CustomerKeys.DISPLAY_NAME)
            val tempCartId = document.getString(CustomerKeys.CART_ID)!!
            val tempEmail = document.getString(CustomerKeys.EMAIL)!!
            val tempApiId = document.getString(CustomerKeys.API_USER_ID)!!
            val tempFirebaseId = document.id
            return CustomerDTO(
                displayName = tempDisplayName ?: tempEmail,
                tempEmail,
                firebaseId = tempFirebaseId,
                id = tempApiId,
                cartId = tempCartId
            )
        }
        const val INVALID_ID = "-1"
        const val INVALID_PASSWORD = "-1"
    }

//    companion object{
//        fun fromDocument(document: DocumentSnapshot): UserDTO {
//            val tempDisplayName: String = document.getString(UserKeys.DISPLAY_NAME)
//            val tempEmail: String = document.getString(UserKeys.EMAIL)
//            val tempPassword: String = document.getString(UserKeys.PASSWORD)
//            val tempId: String = document.getId()
//            return UserDTO(
//                tempDisplayName,
//                tempEmail,
//                tempPassword,
//                tempId
//            )
//        }
//    }
}

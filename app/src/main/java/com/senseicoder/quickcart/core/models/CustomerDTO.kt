package com.senseicoder.quickcart.core.models

data class CustomerDTO(
    val displayName: String,
    val email: String,
    val password: String = INVALID_PASSWORD,
    val id: String = INVALID_ID,
    val isVerified: Boolean = false,
    val isGuest: Boolean = false
){

    companion object{
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

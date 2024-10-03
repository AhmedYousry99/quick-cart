package com.senseicoder.quickcart.core.global

object Constants {

    sealed class SharedPrefs{
       object Settings{
           const val SETTINGS: String = "settings"

       }
    }

    object API{
        const val ADMIN = "https://android-alex-team5.myshopify.com/admin/api/2024-10/graphql.json"
    }
}
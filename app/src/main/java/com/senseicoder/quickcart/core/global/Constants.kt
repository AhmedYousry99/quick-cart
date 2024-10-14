package com.senseicoder.quickcart.core.global


object Constants {


    object Errors {
        const val UNKNOWN = "unknown error occurred"
        const val CUSTOMER_CREATE_FAILED = "customer creation failed"
        object CustomerCreate{
            const val EMAIL_TAKEN = "Email has already been taken"
        }
        object Firebase{
            const val EMAIL_NOT_VERIFIED = "Email is not verified"
        }
    }

    sealed class SharedPrefs {
        object Settings {
            const val SETTINGS: String = "settings"

        }
    }
    object Currency {
        val currencyMap:Map<String, String> = mapOf(
            "EUR" to "€","USD" to "$","CAD" to "CA$","EGP" to "LE")
    }
    object API {
        const val ADMIN = "https://android-alex-team5.myshopify.com/admin/api/2024-10/"
        const val STORE_FRONT = "https://android-alex-team5.myshopify.com/api/2024-10/graphql.json"
        const val CUSTOMER_ID_PREFIX = "gid://shopify/Customer/"
        const val ORDER_ID_PREFIX = "gid://shopify/Order/"
        const val PRODUCT_ID_PREFIX = "gid://shopify/Product/"

        object MetaFields {
            const val CART = "Cart_data"
        }
    }


    const val PAYMENT_TYPE ="Payment Type"
    const val CASH ="cash"
    const val CARD ="card"

    const val TOKEN_EXPIRATION_DATE: String = "tokenExpirationDate"
    const val TOKEN_EXPIRATION_DATE_DEAFULT: String = "unknown"
    const val LABEL = "label"
    const val LABEL_DEFAULT = "unknown"
    const val MAPS_FRAGMENT = "mapsFragment"
    const val CART_FRAGMENT_TO_EDIT = "cartFragmentToEdit"
    const val CART_FRAGMENT_TO_ADD = "cartFragmentToAdd"
    const val CART_FRAGMENT_TO_CHECKOUT = "cartFragmentToCheckout"
    const val FROM_ADD = "fromAdd"


    const val CART_ID: String = "cartId"
    const val CART_ID_DEFAULT: String = "unknown"

    const val USER_EMAIL: String = "userEmail"
    const val USER_EMAIL_DEFAULT: String = "unknown"

    const val USER_DISPLAY_NAME: String = "userDisplayName"
    const val USER_DISPLAY_NAME_DEFAULT: String = "unknown"

    const val USER_TOKEN: String = "userToken"
    const val USER_TOKEN_DEFAULT: String = "unknown"

    const val USER_ID = "userId"
    const val USER_ID_DEFAULT = "unknown"

    const val FIREBASE_USER_ID = "firebaseUserId"
    const val FIREBASE_USER_ID_DEFAULT = "unknown"

    const val CURRENCY = "currency"
    const val CURRENCY_DEFAULT = "EGP"

    const val PERCENTAGE_OF_CURRENCY_CHANGE = "percentageOfCurrencyChange"
    const val PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT = 1.0F

}

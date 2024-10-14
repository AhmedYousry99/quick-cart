package com.senseicoder.quickcart.repos

import com.senseicoder.quickcart.core.services.SharedPrefs
import com.storefront.type.MailingAddressInput


class FakeSharedPrefs : SharedPrefs {
    private val preferences = mutableMapOf<String, String>()

    override fun getSharedPrefString(key: String, defaultValue: String): String {
        return preferences[key] ?: defaultValue
    }

    override fun setSharedPrefBoolean(key: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSharedPrefBoolean(key: String, defaultValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun logAllSharedPref(tag: String, nameOfFunction: String) {
        TODO("Not yet implemented")
    }

    override fun getCurrencyData(): Triple<String?, String?, Float?> {
        TODO("Not yet implemented")
    }

    override fun setSharedPrefString(key: String, value: String) {
        preferences[key] = value
    }

    override fun setSharedPrefFloat(key: String, value: Float) {
        TODO("Not yet implemented")
    }

    override fun getSharedPrefFloat(key: String, value: Float): Float {
        TODO("Not yet implemented")
    }


}

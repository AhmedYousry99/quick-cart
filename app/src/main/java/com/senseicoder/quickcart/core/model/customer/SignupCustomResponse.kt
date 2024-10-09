package com.senseicoder.quickcart.core.model.customer


import com.google.gson.annotations.SerializedName

/**
{
  "customer": {
    "id": 1073339462,
    "email": "steve.lastnameson@example.com",
    "created_at": "2024-09-25T17:18:24-04:00",
    "updated_at": "2024-09-25T17:18:24-04:00",
    "first_name": "Steve",
    "last_name": "Lastnameson",
    "orders_count": 0,
    "state": "enabled",
    "total_spent": "0.00",
    "last_order_id": null,
    "note": null,
    "verified_email": true,
    "multipass_identifier": null,
    "tax_exempt": false,
    "tags": "",
    "last_order_name": null,
    "currency": "USD",
    "phone": "+15142546011",
    "addresses": [
      {
        "id": 1053317292,
        "customer_id": 1073339462,
        "first_name": "Mother",
        "last_name": "Lastnameson",
        "company": null,
        "address1": "123 Oak St",
        "address2": null,
        "city": "Ottawa",
        "province": "Ontario",
        "country": "Canada",
        "zip": "123 ABC",
        "phone": "555-1212",
        "name": "Mother Lastnameson",
        "province_code": "ON",
        "country_code": "CA",
        "country_name": "Canada",
        "default": true
      }
    ],
    "tax_exemptions": [],
    "email_marketing_consent": {
      "state": "not_subscribed",
      "opt_in_level": "single_opt_in",
      "consent_updated_at": null
    },
    "sms_marketing_consent": {
      "state": "not_subscribed",
      "opt_in_level": "single_opt_in",
      "consent_updated_at": null,
      "consent_collected_from": "OTHER"
    },
    "admin_graphql_api_id": "gid://shopify/Customer/1073339462",
    "default_address": {
      "id": 1053317292,
      "customer_id": 1073339462,
      "first_name": "Mother",
      "last_name": "Lastnameson",
      "company": null,
      "address1": "123 Oak St",
      "address2": null,
      "city": "Ottawa",
      "province": "Ontario",
      "country": "Canada",
      "zip": "123 ABC",
      "phone": "555-1212",
      "name": "Mother Lastnameson",
      "province_code": "ON",
      "country_code": "CA",
      "country_name": "Canada",
      "default": true
    }
  }
}
*/
data class SignupCustomResponse(
    @SerializedName("customer")
    val customer: Customer?
) {
    data class Customer(
        @SerializedName("addresses")
        val addresses: List<Addresse?>?,
        @SerializedName("admin_graphql_api_id")
        val adminGraphqlApiId: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("currency")
        val currency: String?,
        @SerializedName("default_address")
        val defaultAddress: DefaultAddress?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("email_marketing_consent")
        val emailMarketingConsent: EmailMarketingConsent?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("last_order_id")
        val lastOrderId: Any?,
        @SerializedName("last_order_name")
        val lastOrderName: Any?,
        @SerializedName("multipass_identifier")
        val multipassIdentifier: Any?,
        @SerializedName("note")
        val note: Any?,
        @SerializedName("orders_count")
        val ordersCount: Int?,
        @SerializedName("phone")
        val phone: String?,
        @SerializedName("sms_marketing_consent")
        val smsMarketingConsent: SmsMarketingConsent?,
        @SerializedName("state")
        val state: String?,
        @SerializedName("tags")
        val tags: String?,
        @SerializedName("tax_exempt")
        val taxExempt: Boolean?,
        @SerializedName("tax_exemptions")
        val taxExemptions: List<Any?>?,
        @SerializedName("total_spent")
        val totalSpent: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("verified_email")
        val verifiedEmail: Boolean?
    ) {
        data class Addresse(
            @SerializedName("address1")
            val address1: String?,
            @SerializedName("address2")
            val address2: Any?,
            @SerializedName("city")
            val city: String?,
            @SerializedName("company")
            val company: Any?,
            @SerializedName("country")
            val country: String?,
            @SerializedName("country_code")
            val countryCode: String?,
            @SerializedName("country_name")
            val countryName: String?,
            @SerializedName("customer_id")
            val customerId: Int?,
            @SerializedName("default")
            val default: Boolean?,
            @SerializedName("first_name")
            val firstName: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("last_name")
            val lastName: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("phone")
            val phone: String?,
            @SerializedName("province")
            val province: String?,
            @SerializedName("province_code")
            val provinceCode: String?,
            @SerializedName("zip")
            val zip: String?
        )

        data class DefaultAddress(
            @SerializedName("address1")
            val address1: String?,
            @SerializedName("address2")
            val address2: Any?,
            @SerializedName("city")
            val city: String?,
            @SerializedName("company")
            val company: Any?,
            @SerializedName("country")
            val country: String?,
            @SerializedName("country_code")
            val countryCode: String?,
            @SerializedName("country_name")
            val countryName: String?,
            @SerializedName("customer_id")
            val customerId: Int?,
            @SerializedName("default")
            val default: Boolean?,
            @SerializedName("first_name")
            val firstName: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("last_name")
            val lastName: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("phone")
            val phone: String?,
            @SerializedName("province")
            val province: String?,
            @SerializedName("province_code")
            val provinceCode: String?,
            @SerializedName("zip")
            val zip: String?
        )

        data class EmailMarketingConsent(
            @SerializedName("consent_updated_at")
            val consentUpdatedAt: Any?,
            @SerializedName("opt_in_level")
            val optInLevel: String?,
            @SerializedName("state")
            val state: String?
        )

        data class SmsMarketingConsent(
            @SerializedName("consent_collected_from")
            val consentCollectedFrom: String?,
            @SerializedName("consent_updated_at")
            val consentUpdatedAt: Any?,
            @SerializedName("opt_in_level")
            val optInLevel: String?,
            @SerializedName("state")
            val state: String?
        )
    }
}
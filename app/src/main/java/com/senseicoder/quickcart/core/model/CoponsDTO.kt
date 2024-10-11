package com.senseicoder.quickcart.core.model

class CoponsDTO

data class DiscountCodesResponse(
    val discount_codes: List<DiscountCode>
)

data class DiscountCode(
    val id: Long,
    val price_rule_id: Long,
    val code: String,
    val usage_count: Int,
    val created_at: String,
    val updated_at: String
)

data class PriceRulesResponse(
    val price_rules: List<PriceRule>
)

data class PriceRule(
    val id: Long,
    val value_type: String,
    val value: String,
    val customer_selection: String,
    val target_type: String,
    val target_selection: String,
    val allocation_method: String,
    val allocation_limit: Any?,
    val once_per_customer: Boolean,
    val usage_limit: Any?,
    val starts_at: String,
    val ends_at: Any?,
    val created_at: String,
    val updated_at: String,
    val entitled_product_ids: List<Any>,
    val entitled_variant_ids: List<Any>,
    val entitled_collection_ids: List<Any>,
    val entitled_country_ids: List<Any>,
    val prerequisite_product_ids: List<Any>,
    val prerequisite_variant_ids: List<Any>,
    val prerequisite_collection_ids: List<Any>,
    val customer_segment_prerequisite_ids: List<Any>,
    val prerequisite_customer_ids: List<Any>,
    val prerequisite_subtotal_range: Any?,
    val prerequisite_quantity_range: Any?,
    val prerequisite_shipping_price_range: Any?,
    val prerequisite_to_entitlement_quantity_ratio: PrerequisiteToEntitlementQuantityRatio,
    val prerequisite_to_entitlement_purchase: PrerequisiteToEntitlementPurchase,
    val title: String,
    val admin_graphql_api_id: String
)

data class PrerequisiteToEntitlementQuantityRatio(
    val prerequisite_quantity: Any?,
    val entitled_quantity: Any?
)

data class PrerequisiteToEntitlementPurchase(
    val prerequisite_amount: Any?
)
data class CouponsForDisplay(
    val priceRule: PriceRule,
    val imageResId: Int
)

package com.example.data.model

enum class CouponCategory(val displayName: String) {
    ALL("All Deals"),
    PLAY_CREDIT("Play Cards"),
    GAMES("Games & Coins"),
    SUBSCRIPTIONS("Apps & Passes"),
    FLASH_DEALS("Flash Sales")
}

data class CouponItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: CouponCategory,
    val faceValue: Double,
    val discountedPrice: Double,
    val discountPercent: Int,
    val description: String,
    val terms: String,
    val stockLeft: Int,
    val rating: Float = 4.9f,
    val ratingCount: Int = 1250,
    val badge: String? = null,
    val expiryHoursLeft: Int = 48,
    val accentColor: Long = 0xFF0B57D0,
    val iconType: String = "PLAY_STORE",
    val pointsReward: Int = 20
)

data class RedeemResult(
    val success: Boolean,
    val code: String,
    val message: String
)

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchased_coupons")
data class PurchasedCouponEntity(
    @PrimaryKey
    val id: String,
    val couponId: String,
    val title: String,
    val category: String,
    val redeemCode: String,
    val purchaseDate: Long,
    val pricePaid: Double,
    val faceValue: Double,
    val isRedeemed: Boolean = false,
    val redeemedDate: Long? = null,
    val notes: String = ""
)

@Entity(tableName = "wallet_transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val timestamp: Long,
    val type: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val walletBalance: Double = 35.0,
    val playPoints: Int = 320,
    val streakDays: Int = 3,
    val lastCheckInDate: Long = 0L,
    val lastSpinDate: Long = 0L
)

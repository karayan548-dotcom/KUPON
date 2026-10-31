package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM purchased_coupons ORDER BY purchaseDate DESC")
    fun getAllPurchased(): Flow<List<PurchasedCouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchased(coupon: PurchasedCouponEntity)

    @Update
    suspend fun updateCoupon(coupon: PurchasedCouponEntity)

    @Delete
    suspend fun deleteCoupon(coupon: PurchasedCouponEntity)

    @Query("UPDATE purchased_coupons SET isRedeemed = :isRedeemed, redeemedDate = :redeemedDate WHERE id = :id")
    suspend fun updateRedeemedStatus(id: String, isRedeemed: Boolean, redeemedDate: Long?)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)
}

package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PurchasedCouponEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CouponCategory
import com.example.data.model.CouponItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import java.util.UUID

class CouponRepository(private val db: AppDatabase) {

    private val couponDao = db.couponDao()
    private val transactionDao = db.transactionDao()
    private val userDao = db.userDao()

    val availableCoupons: List<CouponItem> = listOf(
        CouponItem(
            id = "gp_card_10",
            title = "$10 Google Play Gift Card",
            subtitle = "Official redeemable store code for apps, books & games",
            category = CouponCategory.PLAY_CREDIT,
            faceValue = 10.0,
            discountedPrice = 7.99,
            discountPercent = 20,
            description = "Valid for everything on Google Play Store including movies, apps, books, in-game items, and subscriptions.",
            terms = "Never expires. Redeemable in Google Play Store on Android or web. Non-refundable.",
            stockLeft = 14,
            rating = 4.9f,
            ratingCount = 2840,
            badge = "POPULAR",
            expiryHoursLeft = 36,
            accentColor = 0xFF0B57D0,
            iconType = "PLAY_STORE",
            pointsReward = 20
        ),
        CouponItem(
            id = "gp_card_25",
            title = "$25 Google Play Gift Voucher",
            subtitle = "Save 25% on official Play Store balance",
            category = CouponCategory.PLAY_CREDIT,
            faceValue = 25.0,
            discountedPrice = 18.75,
            discountPercent = 25,
            description = "Instant digital voucher. Boost your Google Play balance immediately. Works for all in-app purchases and subscriptions.",
            terms = "Code instantly delivered. Valid for Google Play accounts. Cannot be exchanged for cash.",
            stockLeft = 8,
            rating = 5.0f,
            ratingCount = 4120,
            badge = "BESTSELLER",
            expiryHoursLeft = 18,
            accentColor = 0xFF00875A,
            iconType = "PLAY_STORE",
            pointsReward = 50
        ),
        CouponItem(
            id = "flash_gp_50",
            title = "$50 Mega Play Pass Voucher",
            subtitle = "Flash deal: Save $17.50 today only!",
            category = CouponCategory.FLASH_DEALS,
            faceValue = 50.0,
            discountedPrice = 32.50,
            discountPercent = 35,
            description = "Huge discount on official Google Play digital gift card. Perfect for big game releases or year-long subscriptions.",
            terms = "Limited flash promo. Max 2 purchases per account. Redeemable on Google Play.",
            stockLeft = 5,
            rating = 4.95f,
            ratingCount = 1890,
            badge = "FLASH 35%",
            expiryHoursLeft = 6,
            accentColor = 0xFFD93025,
            iconType = "FIRE",
            pointsReward = 100
        ),
        CouponItem(
            id = "flash_gp_100",
            title = "$100 Ultimate Gamer Pack",
            subtitle = "Exclusive 40% OFF Flash Drop",
            category = CouponCategory.FLASH_DEALS,
            faceValue = 100.0,
            discountedPrice = 59.99,
            discountPercent = 40,
            description = "Our biggest value coupon. Redeem directly for 100 USD Google Play store balance.",
            terms = "Flash sale while supplies last. Instant code delivery.",
            stockLeft = 3,
            rating = 5.0f,
            ratingCount = 980,
            badge = "40% OFF",
            expiryHoursLeft = 4,
            accentColor = 0xFFE37400,
            iconType = "FLASH",
            pointsReward = 250
        ),
        CouponItem(
            id = "game_genshin_crystals",
            title = "Genshin Impact 1,980 Crystals",
            subtitle = "Genesis Crystals pack + first-time top-up bonus",
            category = CouponCategory.GAMES,
            faceValue = 29.99,
            discountedPrice = 21.99,
            discountPercent = 27,
            description = "Redeemable voucher for 1,980 Genesis Crystals in Genshin Impact via Google Play in-game checkout.",
            terms = "Requires Genshin Impact linked to Google Play account. Applies automatically upon code redemption.",
            stockLeft = 19,
            rating = 4.88f,
            ratingCount = 3300,
            badge = "HOT GAME",
            expiryHoursLeft = 48,
            accentColor = 0xFF673AB7,
            iconType = "GAMEPAD",
            pointsReward = 60
        ),
        CouponItem(
            id = "game_roblox_800",
            title = "Roblox 800 Robux Voucher",
            subtitle = "Unlock exclusive avatar gear & developer products",
            category = CouponCategory.GAMES,
            faceValue = 9.99,
            discountedPrice = 7.49,
            discountPercent = 25,
            description = "Official digital voucher for 800 Robux on Android Google Play Store.",
            terms = "Valid for Roblox Android app. Code applied at checkout or via Play balance.",
            stockLeft = 24,
            rating = 4.92f,
            ratingCount = 5420,
            badge = "TRENDING",
            expiryHoursLeft = 72,
            accentColor = 0xFF00897B,
            iconType = "GAMEPAD",
            pointsReward = 25
        ),
        CouponItem(
            id = "game_clash_goldpass",
            title = "Clash of Clans Season Gold Pass",
            subtitle = "Save 40% on hero skins & 20% builder boosts",
            category = CouponCategory.GAMES,
            faceValue = 6.99,
            discountedPrice = 4.19,
            discountPercent = 40,
            description = "Unlock the current month Gold Pass tier rewards, 1-gem donations, magic books, and exclusive hero skin.",
            terms = "Redeem via Google Play store code to credit Supercell ID purchase.",
            stockLeft = 12,
            rating = 4.97f,
            ratingCount = 2110,
            badge = "SEASON DEAL",
            expiryHoursLeft = 24,
            accentColor = 0xFFF57F17,
            iconType = "GAMEPAD",
            pointsReward = 15
        ),
        CouponItem(
            id = "game_minecraft_minecoins",
            title = "Minecraft 1,720 Minecoins Pack",
            subtitle = "Browse maps, skins, and texture packs in Bedrock",
            category = CouponCategory.GAMES,
            faceValue = 9.99,
            discountedPrice = 6.99,
            discountPercent = 30,
            description = "1,720 Minecoins redeemable for Minecraft marketplace worlds, skins, mini-games, and texture packs.",
            terms = "Requires Microsoft account and Minecraft Bedrock edition on Android.",
            stockLeft = 17,
            rating = 4.85f,
            ratingCount = 1450,
            badge = "SALE",
            expiryHoursLeft = 60,
            accentColor = 0xFF2E7D32,
            iconType = "GAMEPAD",
            pointsReward = 20
        ),
        CouponItem(
            id = "sub_youtube_premium_3m",
            title = "YouTube Premium 3-Month Pass",
            subtitle = "Ad-free videos, background play & YouTube Music",
            category = CouponCategory.SUBSCRIPTIONS,
            faceValue = 41.97,
            discountedPrice = 24.99,
            discountPercent = 40,
            description = "Enjoy uninterrupted YouTube with zero ads, picture-in-picture background playback, offline downloads, and YouTube Music Premium.",
            terms = "Redeemable via Google Play subscription billing. Applicable to new and returning subscribers.",
            stockLeft = 9,
            rating = 4.96f,
            ratingCount = 3890,
            badge = "HOT PASS",
            expiryHoursLeft = 40,
            accentColor = 0xFFD32F2F,
            iconType = "SUBSCRIPTION",
            pointsReward = 75
        ),
        CouponItem(
            id = "sub_spotify_6m",
            title = "Spotify Premium 6-Month Pass",
            subtitle = "Millions of songs ad-free with offline playback",
            category = CouponCategory.SUBSCRIPTIONS,
            faceValue = 65.94,
            discountedPrice = 44.99,
            discountPercent = 32,
            description = "High-fidelity audio streaming, unlimited skips, ad-free listening, and offline track caching on all devices.",
            terms = "Redeemable through Google Play Store subscription payment.",
            stockLeft = 7,
            rating = 4.91f,
            ratingCount = 2650,
            badge = "POPULAR",
            expiryHoursLeft = 30,
            accentColor = 0xFF1DB954,
            iconType = "SUBSCRIPTION",
            pointsReward = 120
        ),
        CouponItem(
            id = "sub_duolingo_super",
            title = "Duolingo Super 1-Year Pass",
            subtitle = "Unlimited hearts, practice hub & zero interruptions",
            category = CouponCategory.SUBSCRIPTIONS,
            faceValue = 83.99,
            discountedPrice = 45.99,
            discountPercent = 45,
            description = "Master any language faster with Super Duolingo: unlimited mistakes, tailored practice sessions, and offline access.",
            terms = "Redeemed directly through Google Play Store subscription management.",
            stockLeft = 11,
            rating = 4.94f,
            ratingCount = 1720,
            badge = "45% OFF",
            expiryHoursLeft = 52,
            accentColor = 0xFF58CC02,
            iconType = "SUBSCRIPTION",
            pointsReward = 130
        ),
        CouponItem(
            id = "sub_canva_pro_6m",
            title = "Canva Pro 6-Month Pass",
            subtitle = "Brand kits, background remover & premium assets",
            category = CouponCategory.SUBSCRIPTIONS,
            faceValue = 77.94,
            discountedPrice = 48.99,
            discountPercent = 37,
            description = "Unlock 100M+ stock photos, videos, audio, 1-click magic resize, and unlimited cloud storage.",
            terms = "Redeem code in Google Play Store to unlock 6 months Canva Pro.",
            stockLeft = 15,
            rating = 4.89f,
            ratingCount = 1200,
            badge = "CREATIVE",
            expiryHoursLeft = 44,
            accentColor = 0xFF00C4CC,
            iconType = "SUBSCRIPTION",
            pointsReward = 110
        ),
        CouponItem(
            id = "sub_google_one_2tb",
            title = "Google One 2TB Cloud Storage (1 Yr)",
            subtitle = "Storage for Photos, Drive, Gmail + VPN & Premium Perks",
            category = CouponCategory.SUBSCRIPTIONS,
            faceValue = 99.99,
            discountedPrice = 64.99,
            discountPercent = 35,
            description = "Ample 2,000 GB shared storage for up to 5 family members, full photo backups, Magic Eraser, and premium Google support.",
            terms = "Applies directly to your Google account via Google Play redeem code.",
            stockLeft = 6,
            rating = 4.98f,
            ratingCount = 2950,
            badge = "OFFICIAL GOOGLE",
            expiryHoursLeft = 28,
            accentColor = 0xFF4285F4,
            iconType = "SUBSCRIPTION",
            pointsReward = 180
        )
    )

    fun getPurchasedCoupons(): Flow<List<PurchasedCouponEntity>> = couponDao.getAllPurchased()

    fun getTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getUserProfile(): Flow<UserProfileEntity> {
        return userDao.getUserProfile().map { profile ->
            profile ?: UserProfileEntity(
                id = 1,
                walletBalance = 35.0,
                playPoints = 320,
                streakDays = 3,
                lastCheckInDate = 0L,
                lastSpinDate = 0L
            ).also { defaultProfile ->
                userDao.saveUserProfile(defaultProfile)
            }
        }
    }

    private suspend fun ensureProfile(): UserProfileEntity {
        var profile = userDao.getUserProfileOnce()
        if (profile == null) {
            profile = UserProfileEntity(
                id = 1,
                walletBalance = 35.0,
                playPoints = 320,
                streakDays = 3,
                lastCheckInDate = 0L,
                lastSpinDate = 0L
            )
            userDao.saveUserProfile(profile)
        }
        return profile
    }

    suspend fun purchaseCoupon(
        coupon: CouponItem,
        pointsToUse: Int,
        paymentMethod: String
    ): Result<PurchasedCouponEntity> {
        val currentProfile = ensureProfile()
        val pointsDiscount = pointsToUse * 0.01
        val finalPrice = (coupon.discountedPrice - pointsDiscount).coerceAtLeast(0.50)

        if (paymentMethod == "WALLET" && currentProfile.walletBalance < finalPrice) {
            return Result.failure(Exception("Insufficient wallet balance. Top up your balance or use Google Pay."))
        }

        if (pointsToUse > currentProfile.playPoints) {
            return Result.failure(Exception("Not enough Play Points available."))
        }

        // Generate authentic Google Play style redeem code: e.g. 3AF8-92KQ-ML9P-7W4Z
        val generatedCode = generatePlayRedeemCode()

        val purchasedEntity = PurchasedCouponEntity(
            id = UUID.randomUUID().toString(),
            couponId = coupon.id,
            title = coupon.title,
            category = coupon.category.displayName,
            redeemCode = generatedCode,
            purchaseDate = System.currentTimeMillis(),
            pricePaid = finalPrice,
            faceValue = coupon.faceValue,
            isRedeemed = false,
            notes = "Purchased with $paymentMethod. +${coupon.pointsReward} Play Points awarded."
        )

        couponDao.insertPurchased(purchasedEntity)

        // Update user profile: deduct balance (if wallet) and points, add earned reward points
        val newBalance = if (paymentMethod == "WALLET") {
            (currentProfile.walletBalance - finalPrice).coerceAtLeast(0.0)
        } else {
            currentProfile.walletBalance
        }
        val newPoints = currentProfile.playPoints - pointsToUse + coupon.pointsReward

        userDao.saveUserProfile(
            currentProfile.copy(
                walletBalance = newBalance,
                playPoints = newPoints
            )
        )

        // Record purchase transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                title = "Purchased ${coupon.title}",
                amount = -finalPrice,
                timestamp = System.currentTimeMillis(),
                type = "PURCHASE"
            )
        )

        // If points were awarded, record reward
        if (coupon.pointsReward > 0) {
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Play Points earned for ${coupon.title}",
                    amount = coupon.pointsReward.toDouble(),
                    timestamp = System.currentTimeMillis() + 1,
                    type = "REWARD"
                )
            )
        }

        return Result.success(purchasedEntity)
    }

    suspend fun topUpWallet(amount: Double): Boolean {
        if (amount <= 0) return false
        val profile = ensureProfile()
        val bonusPoints = (amount * 2).toInt() // 2 points per $1 topped up
        val updatedProfile = profile.copy(
            walletBalance = profile.walletBalance + amount,
            playPoints = profile.playPoints + bonusPoints
        )
        userDao.saveUserProfile(updatedProfile)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                title = "Wallet Top-Up (+$bonusPoints bonus pts)",
                amount = amount,
                timestamp = System.currentTimeMillis(),
                type = "TOP_UP"
            )
        )
        return true
    }

    suspend fun toggleRedeemedStatus(id: String, isRedeemed: Boolean) {
        val date = if (isRedeemed) System.currentTimeMillis() else null
        couponDao.updateRedeemedStatus(id, isRedeemed, date)
    }

    suspend fun claimDailyCheckIn(): Pair<Double, Int>? {
        val profile = ensureProfile()
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // Allow check-in if never checked in or if at least 18 hours passed
        if (now - profile.lastCheckInDate < 18 * 60 * 60 * 1000L) {
            return null
        }

        val bonusCredit = 0.50
        val bonusPoints = 30
        val newStreak = if (now - profile.lastCheckInDate < 48 * 60 * 60 * 1000L) {
            profile.streakDays + 1
        } else {
            1
        }

        userDao.saveUserProfile(
            profile.copy(
                walletBalance = profile.walletBalance + bonusCredit,
                playPoints = profile.playPoints + bonusPoints,
                streakDays = newStreak,
                lastCheckInDate = now
            )
        )

        transactionDao.insertTransaction(
            TransactionEntity(
                id = UUID.randomUUID().toString(),
                title = "Daily Check-in Day $newStreak Reward",
                amount = bonusCredit,
                timestamp = now,
                type = "REWARD"
            )
        )

        return Pair(bonusCredit, bonusPoints)
    }

    suspend fun claimRewardBonus(title: String, credit: Double, points: Int): Boolean {
        val profile = ensureProfile()
        val now = System.currentTimeMillis()
        userDao.saveUserProfile(
            profile.copy(
                walletBalance = profile.walletBalance + credit,
                playPoints = profile.playPoints + points,
                lastSpinDate = now
            )
        )

        if (credit > 0) {
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Won $title (Credit)",
                    amount = credit,
                    timestamp = now,
                    type = "REWARD"
                )
            )
        }
        if (points > 0) {
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Won $title (Points)",
                    amount = points.toDouble(),
                    timestamp = now + 1,
                    type = "REWARD"
                )
            )
        }
        return true
    }

    suspend fun addCustomCoupon(title: String, code: String, faceValue: Double, notes: String): PurchasedCouponEntity {
        val entity = PurchasedCouponEntity(
            id = UUID.randomUUID().toString(),
            couponId = "custom_${System.currentTimeMillis()}",
            title = title,
            category = "Custom Gift Card",
            redeemCode = code.uppercase().trim(),
            purchaseDate = System.currentTimeMillis(),
            pricePaid = 0.0,
            faceValue = faceValue,
            isRedeemed = false,
            notes = notes.ifEmpty { "Manually saved promo code" }
        )
        couponDao.insertPurchased(entity)
        return entity
    }

    suspend fun deletePurchasedCoupon(coupon: PurchasedCouponEntity) {
        couponDao.deleteCoupon(coupon)
    }

    private fun generatePlayRedeemCode(): String {
        val chars = "346789ABCDEFGHJKLMNPQRSTUVWXYZ" // excludes confusing 0/O, 1/I
        val random = SecureRandom()
        fun part(length: Int): String = (1..length).map { chars[random.nextInt(chars.length)] }.joinToString("")
        return "${part(4)}-${part(4)}-${part(4)}-${part(4)}"
    }
}

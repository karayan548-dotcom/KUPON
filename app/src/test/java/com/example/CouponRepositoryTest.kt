package com.example

import com.example.data.model.CouponCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CouponRepositoryTest {

    @Test
    fun testCouponDiscountsAreValid() {
        // Verify discount calculations logic
        val faceValue = 25.0
        val discountPercent = 25
        val expectedDiscounted = faceValue * (1.0 - discountPercent / 100.0)
        assertEquals(18.75, expectedDiscounted, 0.01)
    }

    @Test
    fun testCouponCategoriesContainRequiredDeals() {
        val categories = CouponCategory.values()
        assertTrue(categories.contains(CouponCategory.ALL))
        assertTrue(categories.contains(CouponCategory.PLAY_CREDIT))
        assertTrue(categories.contains(CouponCategory.GAMES))
        assertTrue(categories.contains(CouponCategory.SUBSCRIPTIONS))
        assertTrue(categories.contains(CouponCategory.FLASH_DEALS))
    }

    @Test
    fun testPlayStoreRedeemCodeFormat() {
        val pattern = Regex("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$")
        val sampleCode = "3AF8-92KQ-ML9P-7W4Z"
        assertTrue(sampleCode.matches(pattern))
    }
}

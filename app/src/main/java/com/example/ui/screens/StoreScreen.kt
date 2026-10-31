package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CouponCategory
import com.example.data.model.CouponItem
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.CouponCard
import com.example.ui.components.HeroBannerCard
import com.example.ui.viewmodel.SortOption

@Composable
fun StoreScreen(
    coupons: List<CouponItem>,
    selectedCategory: CouponCategory,
    searchQuery: String,
    sortOption: SortOption,
    onCategorySelected: (CouponCategory) -> Unit,
    onSortOptionChanged: (SortOption) -> Unit,
    onBuyCoupon: (CouponItem) -> Unit,
    onOpenSpinWheel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSortMenuOpen by remember { mutableStateOf(false) }

    // Filter by Category and Search
    val filteredCoupons = remember(coupons, selectedCategory, searchQuery, sortOption) {
        coupons.filter { coupon ->
            val matchesCategory = selectedCategory == CouponCategory.ALL || coupon.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    coupon.title.contains(searchQuery, ignoreCase = true) ||
                    coupon.subtitle.contains(searchQuery, ignoreCase = true) ||
                    coupon.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }.let { list ->
            when (sortOption) {
                SortOption.RECOMMENDED -> list
                SortOption.DISCOUNT_DESC -> list.sortedByDescending { it.discountPercent }
                SortOption.PRICE_ASC -> list.sortedBy { it.discountedPrice }
                SortOption.RATING_DESC -> list.sortedByDescending { it.rating }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("store_screen_list"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Banner Promo (visible when no search is active)
        if (searchQuery.isBlank()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    HeroBannerCard(
                        onSpinClick = onOpenSpinWheel,
                        onFlashDealsClick = { onCategorySelected(CouponCategory.FLASH_DEALS) }
                    )
                }
            }
        }

        // Category Filter Chips
        item {
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )
        }

        // Filter / Sort Info Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredCoupons.size} deals available",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box {
                    OutlinedButton(
                        onClick = { isSortMenuOpen = true },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("sort_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortOption.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    DropdownMenu(
                        expanded = isSortMenuOpen,
                        onDismissRequest = { isSortMenuOpen = false }
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option.title) },
                                onClick = {
                                    onSortOptionChanged(option)
                                    isSortMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Empty Search State
        if (filteredCoupons.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SentimentDissatisfied,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No matching coupons found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try searching for gift cards, Roblox, or Google Play",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Coupon Items
            items(filteredCoupons, key = { it.id }) { coupon ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CouponCard(
                        coupon = coupon,
                        onBuyClick = { onBuyCoupon(coupon) }
                    )
                }
            }
        }
    }
}

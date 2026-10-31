package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.CouponCategory
import com.example.ui.theme.PlayBlue
import com.example.ui.theme.PlayRed

@Composable
fun CategoryFilterChips(
    selectedCategory: CouponCategory,
    onCategorySelected: (CouponCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CouponCategory.values().forEach { category ->
            val isSelected = category == selectedCategory
            val icon = getCategoryIcon(category)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = category.displayName,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else if (category == CouponCategory.FLASH_DEALS) {
                            PlayRed
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (category == CouponCategory.FLASH_DEALS) PlayRed else PlayBlue,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("category_chip_${category.name}")
            )
        }
    }
}

private fun getCategoryIcon(category: CouponCategory): ImageVector {
    return when (category) {
        CouponCategory.ALL -> Icons.Default.Apps
        CouponCategory.PLAY_CREDIT -> Icons.Default.CardGiftcard
        CouponCategory.GAMES -> Icons.Default.SportsEsports
        CouponCategory.SUBSCRIPTIONS -> Icons.Default.Star
        CouponCategory.FLASH_DEALS -> Icons.Default.FlashOn
    }
}

package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddCustomCodeDialog
import com.example.ui.components.CheckoutBottomSheet
import com.example.ui.components.LuckySpinDialog
import com.example.ui.components.PurchaseSuccessDialog
import com.example.ui.components.RedeemCodeSheet
import com.example.ui.components.TopPlayBar
import com.example.ui.components.TopUpBottomSheet
import com.example.ui.screens.DailyRewardsScreen
import com.example.ui.screens.MyVaultScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PlayBlue
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.PlayCouponViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PlayCouponApp()
            }
        }
    }
}

@Composable
fun PlayCouponApp(
    viewModel: PlayCouponViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val availableCoupons = viewModel.availableCoupons
    val purchasedCoupons by viewModel.purchasedCoupons.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    val checkoutCoupon by viewModel.checkoutCoupon.collectAsStateWithLifecycle()
    val purchasedSuccessResult by viewModel.purchasedSuccessResult.collectAsStateWithLifecycle()
    val selectedDetailCoupon by viewModel.selectedDetailCoupon.collectAsStateWithLifecycle()
    val isTopUpSheetVisible by viewModel.isTopUpSheetVisible.collectAsStateWithLifecycle()
    val isAddCustomDialogVisible by viewModel.isAddCustomDialogVisible.collectAsStateWithLifecycle()
    val isSpinWheelOpen by viewModel.isSpinWheelOpen.collectAsStateWithLifecycle()
    val isProcessingCheckout by viewModel.isProcessingCheckout.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val activeCouponsCount = remember(purchasedCoupons) {
        purchasedCoupons.count { !it.isRedeemed }
    }

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle back button: return to STORE tab if on sub-screen
    BackHandler(enabled = activeTab != NavigationTab.STORE) {
        viewModel.setActiveTab(NavigationTab.STORE)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopPlayBar(
                walletBalance = userProfile.walletBalance,
                playPoints = userProfile.playPoints,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onWalletClick = { viewModel.setActiveTab(NavigationTab.WALLET) },
                onPointsClick = { viewModel.setActiveTab(NavigationTab.DAILY_REWARDS) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    val (selectedIcon, unselectedIcon) = when (tab) {
                        NavigationTab.STORE -> Pair(Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer)
                        NavigationTab.MY_VAULT -> Pair(Icons.Filled.ConfirmationNumber, Icons.Outlined.ConfirmationNumber)
                        NavigationTab.DAILY_REWARDS -> Pair(Icons.Filled.Casino, Icons.Outlined.Casino)
                        NavigationTab.WALLET -> Pair(Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setActiveTab(tab) },
                        icon = {
                            if (tab == NavigationTab.MY_VAULT && activeCouponsCount > 0) {
                                BadgedBox(badge = { Badge { Text("$activeCouponsCount") } }) {
                                    Icon(
                                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PlayBlue.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    NavigationTab.STORE -> {
                        StoreScreen(
                            coupons = availableCoupons,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            sortOption = sortOption,
                            onCategorySelected = { viewModel.setCategory(it) },
                            onSortOptionChanged = { viewModel.setSortOption(it) },
                            onBuyCoupon = { viewModel.openCheckout(it) },
                            onOpenSpinWheel = { viewModel.openSpinWheel() }
                        )
                    }
                    NavigationTab.MY_VAULT -> {
                        MyVaultScreen(
                            purchasedCoupons = purchasedCoupons,
                            onCouponClick = { viewModel.openDetail(it) },
                            onAddCustomClick = { viewModel.openAddCustomDialog() },
                            onExploreDealsClick = { viewModel.setActiveTab(NavigationTab.STORE) }
                        )
                    }
                    NavigationTab.DAILY_REWARDS -> {
                        DailyRewardsScreen(
                            userProfile = userProfile,
                            onClaimCheckIn = { viewModel.claimDailyCheckIn() },
                            onOpenSpinWheel = { viewModel.openSpinWheel() },
                            onClaimScratchReward = { title, credit, points ->
                                viewModel.claimSpinOrScratchReward(title, credit, points)
                            }
                        )
                    }
                    NavigationTab.WALLET -> {
                        WalletScreen(
                            userProfile = userProfile,
                            transactions = transactions,
                            onOpenTopUp = { viewModel.openTopUpSheet() },
                            onQuickTopUp = { viewModel.topUpBalance(it) }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets & Dialogs

    // Checkout Modal
    checkoutCoupon?.let { coupon ->
        CheckoutBottomSheet(
            coupon = coupon,
            walletBalance = userProfile.walletBalance,
            availablePlayPoints = userProfile.playPoints,
            isProcessing = isProcessingCheckout,
            onDismiss = { viewModel.closeCheckout() },
            onConfirmPurchase = { pointsToUse, paymentMethod ->
                viewModel.purchaseCoupon(coupon, pointsToUse, paymentMethod) {
                    // Purchase handled
                }
            },
            onTopUpClick = {
                viewModel.openTopUpSheet()
            }
        )
    }

    // Purchase Success Dialog
    purchasedSuccessResult?.let { result ->
        PurchaseSuccessDialog(
            purchasedCoupon = result,
            onDismiss = { viewModel.dismissSuccessResult() },
            onViewVault = {
                viewModel.dismissSuccessResult()
                viewModel.setActiveTab(NavigationTab.MY_VAULT)
            }
        )
    }

    // Voucher Detail / Redeem Sheet
    selectedDetailCoupon?.let { coupon ->
        RedeemCodeSheet(
            coupon = coupon,
            onDismiss = { viewModel.closeDetail() },
            onToggleRedeemed = { currentState ->
                viewModel.toggleRedeemedStatus(coupon.id, currentState)
                viewModel.closeDetail()
            },
            onDelete = {
                viewModel.deleteCoupon(coupon)
            }
        )
    }

    // Wallet Top-Up Sheet
    if (isTopUpSheetVisible) {
        TopUpBottomSheet(
            currentBalance = userProfile.walletBalance,
            onDismiss = { viewModel.closeTopUpSheet() },
            onTopUpConfirm = { amount ->
                viewModel.topUpBalance(amount)
            }
        )
    }

    // Lucky Spin Wheel Modal
    if (isSpinWheelOpen) {
        LuckySpinDialog(
            onDismiss = { viewModel.closeSpinWheel() },
            onClaimReward = { title, credit, points ->
                viewModel.claimSpinOrScratchReward(title, credit, points)
            }
        )
    }

    // Add Custom Code Dialog
    if (isAddCustomDialogVisible) {
        AddCustomCodeDialog(
            onDismiss = { viewModel.closeAddCustomDialog() },
            onSave = { title, code, faceValue, notes ->
                viewModel.addCustomCoupon(title, code, faceValue, notes)
            }
        )
    }
}

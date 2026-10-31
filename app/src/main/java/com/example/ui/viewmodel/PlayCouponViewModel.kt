package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PurchasedCouponEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CouponCategory
import com.example.data.model.CouponItem
import com.example.data.repository.CouponRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val title: String) {
    STORE("Deals"),
    MY_VAULT("My Codes"),
    DAILY_REWARDS("Rewards"),
    WALLET("Wallet")
}

enum class SortOption(val title: String) {
    RECOMMENDED("Top Picks"),
    DISCOUNT_DESC("Highest Discount"),
    PRICE_ASC("Lowest Price"),
    RATING_DESC("Best Rated")
}

class PlayCouponViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CouponRepository(AppDatabase.getDatabase(application))

    val availableCoupons: List<CouponItem> = repository.availableCoupons

    val purchasedCoupons: StateFlow<List<PurchasedCouponEntity>> = repository.getPurchasedCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.getTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity> = repository.getUserProfile()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfileEntity()
        )

    private val _selectedCategory = MutableStateFlow(CouponCategory.ALL)
    val selectedCategory: StateFlow<CouponCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RECOMMENDED)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _activeTab = MutableStateFlow(NavigationTab.STORE)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _checkoutCoupon = MutableStateFlow<CouponItem?>(null)
    val checkoutCoupon: StateFlow<CouponItem?> = _checkoutCoupon.asStateFlow()

    private val _purchasedSuccessResult = MutableStateFlow<PurchasedCouponEntity?>(null)
    val purchasedSuccessResult: StateFlow<PurchasedCouponEntity?> = _purchasedSuccessResult.asStateFlow()

    private val _selectedDetailCoupon = MutableStateFlow<PurchasedCouponEntity?>(null)
    val selectedDetailCoupon: StateFlow<PurchasedCouponEntity?> = _selectedDetailCoupon.asStateFlow()

    private val _isTopUpSheetVisible = MutableStateFlow(false)
    val isTopUpSheetVisible: StateFlow<Boolean> = _isTopUpSheetVisible.asStateFlow()

    private val _isAddCustomDialogVisible = MutableStateFlow(false)
    val isAddCustomDialogVisible: StateFlow<Boolean> = _isAddCustomDialogVisible.asStateFlow()

    private val _isSpinWheelOpen = MutableStateFlow(false)
    val isSpinWheelOpen: StateFlow<Boolean> = _isSpinWheelOpen.asStateFlow()

    private val _isScratchCardOpen = MutableStateFlow(false)
    val isScratchCardOpen: StateFlow<Boolean> = _isScratchCardOpen.asStateFlow()

    private val _isProcessingCheckout = MutableStateFlow(false)
    val isProcessingCheckout: StateFlow<Boolean> = _isProcessingCheckout.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun setCategory(category: CouponCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun setActiveTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun openCheckout(coupon: CouponItem) {
        _checkoutCoupon.value = coupon
    }

    fun closeCheckout() {
        _checkoutCoupon.value = null
    }

    fun openDetail(coupon: PurchasedCouponEntity) {
        _selectedDetailCoupon.value = coupon
    }

    fun closeDetail() {
        _selectedDetailCoupon.value = null
    }

    fun openTopUpSheet() {
        _isTopUpSheetVisible.value = true
    }

    fun closeTopUpSheet() {
        _isTopUpSheetVisible.value = false
    }

    fun openAddCustomDialog() {
        _isAddCustomDialogVisible.value = true
    }

    fun closeAddCustomDialog() {
        _isAddCustomDialogVisible.value = false
    }

    fun openSpinWheel() {
        _isSpinWheelOpen.value = true
    }

    fun closeSpinWheel() {
        _isSpinWheelOpen.value = false
    }

    fun openScratchCard() {
        _isScratchCardOpen.value = true
    }

    fun closeScratchCard() {
        _isScratchCardOpen.value = false
    }

    fun dismissSuccessResult() {
        _purchasedSuccessResult.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun purchaseCoupon(
        coupon: CouponItem,
        pointsToUse: Int,
        paymentMethod: String,
        onSuccess: (PurchasedCouponEntity) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessingCheckout.value = true
            // Simulate brief secure payment processing
            kotlinx.coroutines.delay(800)
            val result = repository.purchaseCoupon(coupon, pointsToUse, paymentMethod)
            _isProcessingCheckout.value = false
            result.onSuccess { purchased ->
                _checkoutCoupon.value = null
                _purchasedSuccessResult.value = purchased
                _toastMessage.value = "Code unlocked! Code saved to your Vault."
                onSuccess(purchased)
            }.onFailure { error ->
                _toastMessage.value = error.message ?: "Purchase failed. Please try again."
            }
        }
    }

    fun topUpBalance(amount: Double) {
        viewModelScope.launch {
            val success = repository.topUpWallet(amount)
            if (success) {
                _isTopUpSheetVisible.value = false
                _toastMessage.value = "Successfully added $$amount to your Play Wallet!"
            }
        }
    }

    fun toggleRedeemedStatus(id: String, currentState: Boolean) {
        viewModelScope.launch {
            repository.toggleRedeemedStatus(id, !currentState)
            _toastMessage.value = if (!currentState) "Marked as Redeemed" else "Marked as Active"
        }
    }

    fun deleteCoupon(coupon: PurchasedCouponEntity) {
        viewModelScope.launch {
            repository.deletePurchasedCoupon(coupon)
            _selectedDetailCoupon.value = null
            _toastMessage.value = "Coupon deleted"
        }
    }

    fun claimDailyCheckIn() {
        viewModelScope.launch {
            val reward = repository.claimDailyCheckIn()
            if (reward != null) {
                _toastMessage.value = "Checked in! Earned +$${reward.first} credit & +${reward.second} Play Points!"
            } else {
                _toastMessage.value = "You have already checked in today! Come back tomorrow."
            }
        }
    }

    fun claimSpinOrScratchReward(title: String, credit: Double, points: Int) {
        viewModelScope.launch {
            repository.claimRewardBonus(title, credit, points)
            _toastMessage.value = "Bonus claimed: $title!"
        }
    }

    fun addCustomCoupon(title: String, code: String, faceValue: Double, notes: String) {
        viewModelScope.launch {
            val added = repository.addCustomCoupon(title, code, faceValue, notes)
            _isAddCustomDialogVisible.value = false
            _toastMessage.value = "Voucher saved to Vault!"
            _selectedDetailCoupon.value = added
        }
    }
}

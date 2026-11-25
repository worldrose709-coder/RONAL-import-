package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.AmbientMusicEngine
import com.example.core.AmbientTrack
import com.example.core.WhatsAppService
import com.example.data.ai.AiThemeRepository
import com.example.data.ai.AiThemeUiState
import com.example.data.local.AppNotificationEntity
import com.example.data.local.CalendarBookingEntity
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderReceiptEntity
import com.example.data.local.PersonalNoteEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.RonalDatabase
import com.example.data.local.SavedProductEntity
import com.example.data.local.UserPreferencesState
import com.example.data.model.DigitalGift
import com.example.data.model.LuxuryProduct
import com.example.data.model.ProductCategory
import com.example.data.model.RonalCatalog
import com.example.data.model.ThemeSwatch
import com.example.data.model.VipLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

data class AchievementBanner(
    val title: String,
    val subtitle: String,
    val pointsAwarded: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RonalDatabase.getInstance(application)
    private val prefsRepo = PreferencesRepository(application)
    private val aiThemeRepo = AiThemeRepository()
    private val musicEngine = AmbientMusicEngine()

    val preferences: StateFlow<UserPreferencesState> = prefsRepo.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesState())

    val cartItems: StateFlow<List<CartItemEntity>> = db.cartDao().observeCart()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedProducts: StateFlow<Map<String, SavedProductEntity>> = db.savedProductDao().observeAll()
        .combine(MutableStateFlow(Unit)) { list, _ -> list.associateBy { it.productId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val personalNotes: StateFlow<List<PersonalNoteEntity>> = db.noteDao().observeNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderReceipts: StateFlow<List<OrderReceiptEntity>> = db.orderDao().observeOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotificationEntity>> = db.notificationDao().observeNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarBookings: StateFlow<List<CalendarBookingEntity>> = db.calendarDao().observeBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _ukClockText = MutableStateFlow("")
    val ukClockText: StateFlow<String> = _ukClockText.asStateFlow()

    private val _ukDateText = MutableStateFlow("")
    val ukDateText: StateFlow<String> = _ukDateText.asStateFlow()

    private val _visitorCount = MutableStateFlow(148)
    val visitorCount: StateFlow<Int> = _visitorCount.asStateFlow()

    private val _aiUiState = MutableStateFlow<AiThemeUiState>(AiThemeUiState.Idle)
    val aiUiState: StateFlow<AiThemeUiState> = _aiUiState.asStateFlow()

    private val _achievementBanner = MutableStateFlow<AchievementBanner?>(null)
    val achievementBanner: StateFlow<AchievementBanner?> = _achievementBanner.asStateFlow()

    private val _statusToast = MutableStateFlow<String?>(null)
    val statusToast: StateFlow<String?> = _statusToast.asStateFlow()

    private val _selectedBookingDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date())
    )
    val selectedBookingDate: StateFlow<String> = _selectedBookingDate.asStateFlow()

    private val _isRecaptchaVerified = MutableStateFlow(false)
    val isRecaptchaVerified: StateFlow<Boolean> = _isRecaptchaVerified.asStateFlow()

    private val _recaptchaToken = MutableStateFlow("")
    val recaptchaToken: StateFlow<String> = _recaptchaToken.asStateFlow()

    init {
        seedInitialNotificationsIfEmpty()
        startUkClockAndVisitors()
    }

    private fun seedInitialNotificationsIfEmpty() {
        viewModelScope.launch {
            delay(500)
            if (notifications.value.isEmpty()) {
                db.notificationDao().insert(
                    AppNotificationEntity(
                        title = "WhatsApp Concierge Connected",
                        message = "Automated bills and receipt updates linked to 0719362947.",
                        category = "WhatsApp"
                    )
                )
                db.notificationDao().insert(
                    AppNotificationEntity(
                        title = "Mayfair 2026 Collection Live",
                        message = "TRJ VVIP 550M, UK Ultra Luxury & 1KG Ceylon Tea open for reservation.",
                        category = "VIP"
                    )
                )
            }
        }
    }

    private fun startUkClockAndVisitors() {
        val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.UK).apply {
            timeZone = TimeZone.getTimeZone("Europe/London")
        }
        val dateFmt = SimpleDateFormat("EEE, dd MMM yyyy", Locale.UK).apply {
            timeZone = TimeZone.getTimeZone("Europe/London")
        }
        viewModelScope.launch {
            var tick = 0
            while (true) {
                val now = Date()
                _ukClockText.value = "${timeFmt.format(now)} GMT"
                _ukDateText.value = dateFmt.format(now)
                if (tick % 4 == 0) {
                    val delta = Random.nextInt(-2, 4)
                    _visitorCount.value = (_visitorCount.value + delta).coerceIn(124, 298)
                }
                tick++
                delay(1000L)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryKey: String) {
        _selectedCategory.value = categoryKey
    }

    fun selectBookingDate(dateIso: String) {
        _selectedBookingDate.value = dateIso
    }

    fun addToCart(product: LuxuryProduct) {
        viewModelScope.launch {
            val existing = db.cartDao().getById(product.id)
            val newQty = (existing?.quantity ?: 0) + 1
            db.cartDao().upsert(
                CartItemEntity(
                    productId = product.id,
                    productName = product.name,
                    varietyLabel = product.varietyLabel,
                    categoryName = product.category.title,
                    unitPriceLkr = product.priceLkr,
                    quantity = newQty
                )
            )
            _statusToast.value = "Added ${product.name} (${product.varietyLabel}) to Bag"
        }
    }

    fun updateCartQuantity(item: CartItemEntity, delta: Int) {
        viewModelScope.launch {
            val next = item.quantity + delta
            if (next <= 0) {
                db.cartDao().deleteById(item.productId)
            } else {
                db.cartDao().upsert(item.copy(quantity = next))
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            db.cartDao().deleteById(productId)
        }
    }

    fun toggleFavorite(product: LuxuryProduct) {
        viewModelScope.launch {
            val existing = db.savedProductDao().getById(product.id)
            val nextFav = !(existing?.isFavorite ?: false)
            db.savedProductDao().upsert(
                SavedProductEntity(
                    productId = product.id,
                    isFavorite = nextFav,
                    isBookmarked = existing?.isBookmarked ?: false
                )
            )
        }
    }

    fun toggleBookmark(product: LuxuryProduct) {
        viewModelScope.launch {
            val existing = db.savedProductDao().getById(product.id)
            val nextBook = !(existing?.isBookmarked ?: false)
            db.savedProductDao().upsert(
                SavedProductEntity(
                    productId = product.id,
                    isFavorite = existing?.isFavorite ?: false,
                    isBookmarked = nextBook
                )
            )
        }
    }

    fun addNote(title: String, body: String, tag: String) {
        if (title.isBlank() && body.isBlank()) return
        viewModelScope.launch {
            db.noteDao().insert(
                PersonalNoteEntity(
                    title = title.ifBlank { "Concierge Note" },
                    body = body,
                    tag = tag.ifBlank { "UK VIP" }
                )
            )
            _statusToast.value = "Note saved offline"
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch { db.noteDao().deleteById(id) }
    }

    fun addCalendarBooking(dateIso: String, title: String, location: String) {
        viewModelScope.launch {
            db.calendarDao().insert(
                CalendarBookingEntity(
                    dateIso = dateIso,
                    title = title.ifBlank { "Mayfair Private Viewing" },
                    ukLocation = location.ifBlank { "Mayfair, London UK" }
                )
            )
            _selectedBookingDate.value = dateIso
            _statusToast.value = "UK date reserved: $dateIso"
        }
    }

    fun deleteCalendarBooking(id: Int) {
        viewModelScope.launch { db.calendarDao().deleteById(id) }
    }

    fun applyThemeSwatch(swatch: ThemeSwatch) {
        viewModelScope.launch {
            prefsRepo.setThemeColors(
                accentArgb = colorToArgbLong(swatch.accentColor),
                glowArgb = colorToArgbLong(swatch.glowColor)
            )
            _statusToast.value = "Applied ${swatch.name}"
        }
    }

    fun setProfileColor(color: Color) {
        viewModelScope.launch {
            prefsRepo.setProfileColor(colorToArgbLong(color))
        }
    }

    fun setFont(fontId: String) {
        viewModelScope.launch { prefsRepo.setFont(fontId) }
    }

    fun setWallpaper(wallpaperId: String) {
        viewModelScope.launch { prefsRepo.setWallpaper(wallpaperId) }
    }

    fun setCustomBackgroundUri(uri: String) {
        viewModelScope.launch {
            prefsRepo.setCustomBgUri(uri)
            _statusToast.value = "Custom background applied"
        }
    }

    fun saveProfileCustomization(memberName: String, welcomeMessage: String, frameId: String) {
        viewModelScope.launch {
            prefsRepo.setProfileCustomization(
                memberName = memberName.ifBlank { "Ronal VIP Guest" },
                welcomeMessage = welcomeMessage.ifBlank { "Welcome back to the Mayfair Private Circle" },
                frameId = frameId
            )
            _statusToast.value = "VIP profile saved"
        }
    }

    fun toggleMusicPlayback() {
        val current = preferences.value
        val nextPlaying = !current.isMusicPlaying
        val track = AmbientTrack.fromId(current.profileTrackId)
        if (nextPlaying) {
            musicEngine.start(track, current.soundVolume, current.isSoundMuted)
        } else {
            musicEngine.stop()
        }
        viewModelScope.launch {
            prefsRepo.setAudioState(current.profileTrackId, current.soundVolume, current.isSoundMuted, nextPlaying)
        }
    }

    fun selectMusicTrack(track: AmbientTrack) {
        val current = preferences.value
        if (current.isMusicPlaying) {
            musicEngine.updateSettings(track, current.soundVolume, current.isSoundMuted)
        } else {
            musicEngine.start(track, current.soundVolume, current.isSoundMuted)
        }
        viewModelScope.launch {
            prefsRepo.setAudioState(track.id, current.soundVolume, current.isSoundMuted, true)
        }
    }

    fun updateAudioVolume(volume: Float, muted: Boolean) {
        val current = preferences.value
        val track = AmbientTrack.fromId(current.profileTrackId)
        musicEngine.updateSettings(track, volume, muted)
        viewModelScope.launch {
            prefsRepo.setAudioState(current.profileTrackId, volume, muted, current.isMusicPlaying)
        }
    }

    fun setOfflineMode(offline: Boolean) {
        viewModelScope.launch {
            prefsRepo.setOfflineMode(offline)
            _statusToast.value = if (offline) {
                "Offline Mode active — all local features & receipts ready"
            } else {
                "Connected Mode active — AI Designer & WhatsApp API enabled"
            }
        }
    }

    fun updateWidgetVisibility(clock: Boolean, weather: Boolean, visitor: Boolean, quote: Boolean) {
        viewModelScope.launch {
            prefsRepo.setWidgetVisibility(clock, weather, visitor, quote)
        }
    }

    fun updateWhatsAppConfig(number: String, autoApi: Boolean, phoneId: String, token: String) {
        viewModelScope.launch {
            prefsRepo.setWhatsAppConfig(number, autoApi, phoneId, token)
            _statusToast.value = "WhatsApp config updated (${number.ifBlank { WhatsAppService.DEFAULT_WHATSAPP_LOCAL }})"
        }
    }

    fun claimDailyLoginReward() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date())
        val current = preferences.value
        if (current.lastRewardDate == today) {
            _statusToast.value = "Today's VIP reward already claimed"
            return
        }
        val bonus = 250 + (current.loginStreak * 25)
        viewModelScope.launch {
            prefsRepo.claimDailyReward(today, bonus)
            prefsRepo.addVipPointsAndBadge(0, "Daily Streak x${current.loginStreak + 1}")
            db.notificationDao().insert(
                AppNotificationEntity(
                    title = "Daily VIP Reward Claimed",
                    message = "+$bonus VIP points added to your Ronal Sovereign balance.",
                    category = "Rewards"
                )
            )
            triggerAchievement(
                title = "Daily VIP Login Streak",
                subtitle = "Day ${current.loginStreak + 1} Mayfair Sovereign Bonus",
                points = bonus
            )
        }
    }

    fun claimDigitalGift(gift: DigitalGift) {
        val current = preferences.value
        val alreadyClaimed = current.claimedGiftsCsv.split(",").contains(gift.id)
        if (alreadyClaimed) {
            _statusToast.value = "${gift.title} already in your VIP Vault"
            return
        }
        viewModelScope.launch {
            prefsRepo.markGiftClaimed(gift.id)
            prefsRepo.addVipPointsAndBadge(150, "Gift Collector")
            db.notificationDao().insert(
                AppNotificationEntity(
                    title = "Digital Gift Unlocked",
                    message = "${gift.title} (${gift.codePrefix}-2026) added to your account.",
                    category = "Gift"
                )
            )
            triggerAchievement(
                title = "Gift Unlocked: ${gift.title}",
                subtitle = gift.valueDisplay,
                points = 150
            )
        }
    }

    fun verifyRecaptchaHuman(passed: Boolean) {
        _isRecaptchaVerified.value = passed
        _recaptchaToken.value = if (passed) {
            "RCPA-${System.currentTimeMillis().toString().takeLast(6)}"
        } else {
            ""
        }
    }

    fun resetRecaptcha() {
        _isRecaptchaVerified.value = false
        _recaptchaToken.value = ""
    }

    fun generateAiTheme(moodPrompt: String) {
        if (moodPrompt.isBlank()) {
            _aiUiState.value = AiThemeUiState.Error("Enter a luxury theme mood first.")
            return
        }
        viewModelScope.launch {
            _aiUiState.value = AiThemeUiState.Loading
            try {
                val result = aiThemeRepo.designTheme(moodPrompt, preferences.value.isOfflineMode)
                val accentArgb = parseHexToArgbLong(result.accentHex, 0xFFC59B27L)
                val glowArgb = parseHexToArgbLong(result.glowHex, 0xFF06B6D4L)
                prefsRepo.setThemeColors(accentArgb, glowArgb)
                prefsRepo.setFont(result.fontId)
                prefsRepo.setWallpaper(result.wallpaperId)
                prefsRepo.setAiTagline(result.tagline)
                _aiUiState.value = AiThemeUiState.Success(result)
                triggerAchievement(
                    title = "AI Atelier Theme Applied",
                    subtitle = result.themeName,
                    points = 100
                )
                prefsRepo.addVipPointsAndBadge(100, "AI Style Patron")
            } catch (e: Exception) {
                _aiUiState.value = AiThemeUiState.Error(e.localizedMessage ?: "AI Theme Designer failed.")
            }
        }
    }

    fun completeOrderAndSendWhatsApp(
        customerName: String,
        whatsappNumberInput: String,
        launchWhatsAppIntent: (String, String) -> Boolean
    ) {
        val items = cartItems.value
        if (items.isEmpty()) {
            _statusToast.value = "Add items to your luxury bag first"
            return
        }
        if (!_isRecaptchaVerified.value) {
            _statusToast.value = "Complete the 'I'm not a robot' security check first"
            return
        }
        val prefs = preferences.value
        val targetPhone = whatsappNumberInput.ifBlank { prefs.whatsappNumber }
        val total = items.sumOf { it.unitPriceLkr * it.quantity }
        val orderCode = "TRJ-${SimpleDateFormat("HHmmss", Locale.UK).format(Date())}"
        val vipTier = VipLevel.forPoints(prefs.vipPoints).title
        val token = _recaptchaToken.value.ifBlank { "RCPA-VERIFIED" }

        val message = WhatsAppService.buildBillMessage(
            orderCode = orderCode,
            customerName = customerName.ifBlank { prefs.memberName },
            whatsappNumber = targetPhone,
            bookingDate = _selectedBookingDate.value,
            items = items,
            totalAmount = total,
            recaptchaToken = token,
            vipTier = vipTier
        )

        viewModelScope.launch {
            var deliveryStatus = if (prefs.isOfflineMode) {
                "Offline Ready (Tap to Open WhatsApp)"
            } else {
                "Prepared for WhatsApp ($targetPhone)"
            }

            if (!prefs.isOfflineMode && prefs.whatsappAutoApiEnabled &&
                prefs.whatsappPhoneNumberId.isNotBlank() && prefs.whatsappAccessToken.isNotBlank()
            ) {
                val apiRes = WhatsAppService.sendViaWhatsAppCloudApi(
                    phoneNumberId = prefs.whatsappPhoneNumberId,
                    accessToken = prefs.whatsappAccessToken,
                    recipientRaw = targetPhone,
                    messageText = message
                )
                deliveryStatus = apiRes.fold(
                    onSuccess = { "Auto-Sent via WhatsApp API ($targetPhone)" },
                    onFailure = { "API fallback -> App Intent ($targetPhone)" }
                )
            }

            val launched = launchWhatsAppIntent(targetPhone, message)
            if (launched && deliveryStatus.startsWith("Prepared")) {
                deliveryStatus = "Dispatched to WhatsApp ($targetPhone)"
            }

            db.orderDao().insert(
                OrderReceiptEntity(
                    orderCode = orderCode,
                    customerName = customerName.ifBlank { prefs.memberName },
                    whatsappNumber = targetPhone,
                    bookingDate = _selectedBookingDate.value,
                    totalLkr = total,
                    summaryText = message,
                    whatsappStatus = deliveryStatus,
                    recaptchaToken = token
                )
            )
            db.notificationDao().insert(
                AppNotificationEntity(
                    title = "WhatsApp Bill $orderCode",
                    message = "$deliveryStatus · Total ${WhatsAppService.formatPriceLkr(total)}",
                    category = "Bill"
                )
            )
            db.cartDao().clearCart()
            resetRecaptcha()
            prefsRepo.addVipPointsAndBadge(500, "Ronal Patron")
            triggerAchievement(
                title = "Order Confirmed · $orderCode",
                subtitle = "Bill routed to WhatsApp $targetPhone",
                points = 500
            )
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch { db.notificationDao().markAllRead() }
    }

    fun dismissAchievement() {
        _achievementBanner.value = null
    }

    fun dismissToast() {
        _statusToast.value = null
    }

    fun showMessage(msg: String) {
        _statusToast.value = msg
    }

    private fun triggerAchievement(title: String, subtitle: String, points: Int) {
        _achievementBanner.value = AchievementBanner(title, subtitle, points)
    }

    private fun colorToArgbLong(color: Color): Long {
        val a = (color.alpha * 255).toInt() and 0xFF
        val r = (color.red * 255).toInt() and 0xFF
        val g = (color.green * 255).toInt() and 0xFF
        val b = (color.blue * 255).toInt() and 0xFF
        return ((a.toLong() shl 24) or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong())
    }

    private fun parseHexToArgbLong(hex: String, fallback: Long): Long {
        return try {
            val clean = hex.removePrefix("#")
            0xFF000000L or clean.toLong(16)
        } catch (_: Exception) {
            fallback
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicEngine.stop()
    }
}

fun buildFilteredProducts(
    query: String,
    categoryKey: String,
    savedMap: Map<String, SavedProductEntity>
): List<LuxuryProduct> {
    val base = when (categoryKey) {
        "CARS" -> RonalCatalog.products.filter { it.category == ProductCategory.CARS }
        "HOTELS" -> RonalCatalog.products.filter { it.category == ProductCategory.HOTELS }
        "TEA" -> RonalCatalog.products.filter { it.category == ProductCategory.TEA }
        "PERFUME" -> RonalCatalog.products.filter { it.category == ProductCategory.PERFUME }
        "FAVORITES" -> RonalCatalog.products.filter { savedMap[it.id]?.isFavorite == true }
        "BOOKMARKS" -> RonalCatalog.products.filter { savedMap[it.id]?.isBookmarked == true }
        else -> RonalCatalog.products
    }
    val q = query.trim().lowercase()
    if (q.isEmpty()) return base
    return base.filter {
        it.name.lowercase().contains(q) ||
            it.varietyLabel.lowercase().contains(q) ||
            it.category.title.lowercase().contains(q) ||
            it.priceDisplay.lowercase().contains(q) ||
            it.ukLocation.lowercase().contains(q)
    }
}

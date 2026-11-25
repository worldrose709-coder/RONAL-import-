package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.configureAppCheck
import com.example.data.model.ProductCategory
import com.example.data.model.WallpaperStyle
import com.example.ui.components.AchievementUnlockOverlay
import com.example.ui.components.AnimatedWhiteLuxuryBackground
import com.example.ui.components.AppDestination
import com.example.ui.components.FloatingQuickMenu
import com.example.ui.components.NotificationsModalSheet
import com.example.ui.components.RonalBottomNavigationBar
import com.example.ui.components.RonalCustomLoadingScreen
import com.example.ui.components.RonalTopBar
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StudioPersonalizeScreen
import com.example.ui.screens.VipLoungeScreen
import com.example.ui.theme.AppFontOption
import com.example.ui.theme.RonalImportsTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before UI or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            RonalImportsApp()
        }
    }
}

@Composable
fun RonalImportsApp(viewModel: MainViewModel = viewModel()) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val savedMap by viewModel.savedProducts.collectAsStateWithLifecycle()
    val notes by viewModel.personalNotes.collectAsStateWithLifecycle()
    val receipts by viewModel.orderReceipts.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val bookings by viewModel.calendarBookings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val ukClock by viewModel.ukClockText.collectAsStateWithLifecycle()
    val ukDate by viewModel.ukDateText.collectAsStateWithLifecycle()
    val visitorCount by viewModel.visitorCount.collectAsStateWithLifecycle()
    val aiUiState by viewModel.aiUiState.collectAsStateWithLifecycle()
    val achievement by viewModel.achievementBanner.collectAsStateWithLifecycle()
    val statusToast by viewModel.statusToast.collectAsStateWithLifecycle()
    val selectedBookingDate by viewModel.selectedBookingDate.collectAsStateWithLifecycle()
    val isRecaptchaVerified by viewModel.isRecaptchaVerified.collectAsStateWithLifecycle()
    val recaptchaToken by viewModel.recaptchaToken.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showCustomLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(850L)
        showCustomLoading = false
    }

    LaunchedEffect(statusToast) {
        val msg = statusToast
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    val fontOption = AppFontOption.fromId(prefs.fontId)
    val wallpaperStyle = WallpaperStyle.entries.firstOrNull { it.id == prefs.wallpaperId }
        ?: WallpaperStyle.PEARL_SILK

    RonalImportsTheme(
        accentColor = Color(prefs.accentColorArgb),
        glowColor = Color(prefs.glowColorArgb),
        profileColor = Color(prefs.profileColorArgb),
        fontOption = fontOption
    ) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        fun navigateTo(dest: AppDestination) {
            navController.navigate(dest.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        AnimatedWhiteLuxuryBackground(
            wallpaperStyle = wallpaperStyle,
            customBgUri = prefs.customBgUri
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    RonalTopBar(
                        isOfflineMode = prefs.isOfflineMode,
                        isMusicPlaying = prefs.isMusicPlaying,
                        unreadNotifications = notifications.count { !it.isRead },
                        onToggleOffline = { viewModel.setOfflineMode(!prefs.isOfflineMode) },
                        onToggleMusic = { viewModel.toggleMusicPlayback() },
                        onOpenNotifications = { showNotificationsSheet = true }
                    )
                },
                bottomBar = {
                    RonalBottomNavigationBar(
                        currentRoute = currentRoute,
                        cartCount = cartItems.sumOf { it.quantity },
                        onNavigate = { navigateTo(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppDestination.HOME.route,
                        enterTransition = {
                            fadeIn(tween(260)) + slideInHorizontally(tween(260)) { it / 12 }
                        },
                        exitTransition = {
                            fadeOut(tween(220)) + slideOutHorizontally(tween(220)) { -it / 12 }
                        },
                        popEnterTransition = {
                            fadeIn(tween(240)) + slideInHorizontally(tween(240)) { -it / 12 }
                        },
                        popExitTransition = {
                            fadeOut(tween(220)) + slideOutHorizontally(tween(220)) { it / 12 }
                        }
                    ) {
                        composable(AppDestination.HOME.route) {
                            HomeScreen(
                                prefs = prefs,
                                ukClock = ukClock,
                                ukDate = ukDate,
                                visitorCount = visitorCount,
                                onClaimDailyReward = { viewModel.claimDailyLoginReward() },
                                onOpenCategory = { cat ->
                                    val key = when (cat) {
                                        ProductCategory.CARS -> "CARS"
                                        ProductCategory.HOTELS -> "HOTELS"
                                        ProductCategory.TEA -> "TEA"
                                        ProductCategory.PERFUME -> "PERFUME"
                                    }
                                    viewModel.selectCategory(key)
                                    navigateTo(AppDestination.CATALOG)
                                },
                                onAddToCart = { viewModel.addToCart(it) },
                                onOpenVipLounge = { navigateTo(AppDestination.VIP) }
                            )
                        }

                        composable(AppDestination.CATALOG.route) {
                            CatalogScreen(
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory,
                                savedMap = savedMap,
                                onSearchChange = { viewModel.updateSearchQuery(it) },
                                onCategoryChange = { viewModel.selectCategory(it) },
                                onAddToCart = { viewModel.addToCart(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onToggleBookmark = { viewModel.toggleBookmark(it) }
                            )
                        }

                        composable(AppDestination.VIP.route) {
                            VipLoungeScreen(
                                prefs = prefs,
                                notes = notes,
                                bookings = bookings,
                                selectedBookingDate = selectedBookingDate,
                                onClaimDailyReward = { viewModel.claimDailyLoginReward() },
                                onClaimDigitalGift = { viewModel.claimDigitalGift(it) },
                                onSaveProfile = { name, welcome, frame ->
                                    viewModel.saveProfileCustomization(name, welcome, frame)
                                },
                                onAddNote = { title, body, tag -> viewModel.addNote(title, body, tag) },
                                onDeleteNote = { viewModel.deleteNote(it) },
                                onSelectBookingDate = { viewModel.selectBookingDate(it) },
                                onAddCalendarBooking = { date, title, loc ->
                                    viewModel.addCalendarBooking(date, title, loc)
                                },
                                onDeleteCalendarBooking = { viewModel.deleteCalendarBooking(it) },
                                onStatusMessage = { viewModel.showMessage(it) }
                            )
                        }

                        composable(AppDestination.STUDIO.route) {
                            StudioPersonalizeScreen(
                                prefs = prefs,
                                aiUiState = aiUiState,
                                onGenerateAiTheme = { viewModel.generateAiTheme(it) },
                                onSelectSwatch = { viewModel.applyThemeSwatch(it) },
                                onSelectProfileColor = { viewModel.setProfileColor(it) },
                                onSelectFont = { viewModel.setFont(it) },
                                onSelectWallpaper = { viewModel.setWallpaper(it) },
                                onCustomBgPicked = { viewModel.setCustomBackgroundUri(it) },
                                onToggleMusic = { viewModel.toggleMusicPlayback() },
                                onSelectTrack = { viewModel.selectMusicTrack(it) },
                                onUpdateAudio = { vol, muted -> viewModel.updateAudioVolume(vol, muted) },
                                onToggleOfflineMode = { viewModel.setOfflineMode(it) },
                                onUpdateWidgets = { c, w, v, q -> viewModel.updateWidgetVisibility(c, w, v, q) }
                            )
                        }

                        composable(AppDestination.CHECKOUT.route) {
                            CheckoutScreen(
                                prefs = prefs,
                                cartItems = cartItems,
                                orderReceipts = receipts,
                                selectedBookingDate = selectedBookingDate,
                                isRecaptchaVerified = isRecaptchaVerified,
                                recaptchaToken = recaptchaToken,
                                onUpdateCartQty = { item, delta -> viewModel.updateCartQuantity(item, delta) },
                                onRemoveFromCart = { viewModel.removeFromCart(it) },
                                onSaveWhatsAppConfig = { num, api, pid, tok ->
                                    viewModel.updateWhatsAppConfig(num, api, pid, tok)
                                },
                                onVerifyRecaptcha = { viewModel.verifyRecaptchaHuman(it) },
                                onConfirmOrder = { name, phone, launcher ->
                                    viewModel.completeOrderAndSendWhatsApp(name, phone, launcher)
                                },
                                onBrowseCatalog = { navigateTo(AppDestination.CATALOG) },
                                onStatusMessage = { viewModel.showMessage(it) }
                            )
                        }
                    }

                    FloatingQuickMenu(
                        isMusicPlaying = prefs.isMusicPlaying,
                        onToggleMusic = { viewModel.toggleMusicPlayback() },
                        onOpenAiStudio = { navigateTo(AppDestination.STUDIO) },
                        onOpenCheckout = { navigateTo(AppDestination.CHECKOUT) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 18.dp, bottom = 22.dp)
                    )
                }
            }

            if (showNotificationsSheet) {
                NotificationsModalSheet(
                    notifications = notifications,
                    onMarkAllRead = { viewModel.markNotificationsRead() },
                    onDismiss = { showNotificationsSheet = false }
                )
            }

            AchievementUnlockOverlay(
                banner = achievement,
                onDismiss = { viewModel.dismissAchievement() }
            )

            RonalCustomLoadingScreen(visible = showCustomLoading)
        }
    }
}

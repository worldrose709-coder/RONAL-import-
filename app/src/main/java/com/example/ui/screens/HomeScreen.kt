package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.UserPreferencesState
import com.example.data.model.LuxuryProduct
import com.example.data.model.ProductCategory
import com.example.data.model.ProfileFrameStyle
import com.example.data.model.RonalCatalog
import com.example.data.model.VipLevel
import com.example.ui.components.AnimatedGradientText
import com.example.ui.components.AnimatedProfileAvatar
import com.example.ui.components.Glass3DCard
import com.example.ui.components.Interactive3DPrismCanvas
import com.example.ui.components.PremiumShimmerButton
import com.example.ui.components.ProductVarietyArtwork
import com.example.ui.components.UkConciergeMapCard
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeScreen(
    prefs: UserPreferencesState,
    ukClock: String,
    ukDate: String,
    visitorCount: Int,
    onClaimDailyReward: () -> Unit,
    onOpenCategory: (ProductCategory) -> Unit,
    onAddToCart: (LuxuryProduct) -> Unit,
    onOpenVipLounge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showBackToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }
    var selectedUkPin by remember { mutableStateOf(RonalCatalog.ukLocations.first()) }

    val vipLevel = VipLevel.forPoints(prefs.vipPoints)
    val nextLevel = VipLevel.nextLevel(vipLevel)
    val progress = if (nextLevel != null) {
        val span = (nextLevel.minPoints - vipLevel.minPoints).coerceAtLeast(1)
        ((prefs.vipPoints - vipLevel.minPoints).toFloat() / span).coerceIn(0f, 1f)
    } else 1f

    val dayIndex = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val quotePair = RonalCatalog.dailyQuotes[dayIndex % RonalCatalog.dailyQuotes.size]

    val frameStyle = ProfileFrameStyle.entries.firstOrNull { it.id == prefs.profileFrameId }
        ?: ProfileFrameStyle.GOLDEN_HALO

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // VIP Welcome Card
            item {
                Glass3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenVipLounge
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedProfileAvatar(
                                initials = prefs.memberName,
                                frameStyle = frameStyle,
                                profileColor = Color(prefs.profileColorArgb),
                                size = 64.dp
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = ChampagneGold.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = "LV.${vipLevel.level} · ${vipLevel.title.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ChampagneGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "${prefs.vipPoints} pts",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extra.glow
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                AnimatedGradientText(
                                    text = prefs.memberName,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(
                                    text = prefs.customWelcomeMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = extra.accent,
                            trackColor = extra.accent.copy(alpha = 0.15f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (nextLevel != null) {
                                    "${nextLevel.minPoints - prefs.vipPoints} pts to ${nextLevel.title}"
                                } else {
                                    "Sovereign Crown Tier Achieved"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                onClick = onClaimDailyReward,
                                shape = RoundedCornerShape(50),
                                color = extra.accent,
                                modifier = Modifier.testTag("claim_daily_reward_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = "Daily Reward",
                                        tint = PureWhite,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Daily Reward (${prefs.loginStreak}d)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PureWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Live UK Clock, Weather & Visitor Counter Widgets (Customizable)
            if (prefs.showClockWidget || prefs.showWeatherWidget || prefs.showVisitorWidget) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (prefs.showClockWidget) {
                            Glass3DCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 20.dp,
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = "UK Clock",
                                            tint = extra.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "LONDON UK",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extra.accent
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = ukClock.ifBlank { "12:00:00 GMT" },
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = InkObsidian,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = ukDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        if (prefs.showWeatherWidget) {
                            Glass3DCard(
                                modifier = Modifier.weight(1f),
                                cornerRadius = 20.dp,
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.WbSunny,
                                            contentDescription = "UK Weather",
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "MAYFAIR",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ChampagneGold
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "14°C · Crisp",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = InkObsidian
                                    )
                                    Text(
                                        text = "Hyde Park Clear",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        if (prefs.showVisitorWidget) {
                            Glass3DCard(
                                modifier = Modifier.weight(0.85f),
                                cornerRadius = 20.dp,
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.People,
                                            contentDescription = "Live Visitors",
                                            tint = extra.glow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "LIVE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = extra.glow
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "$visitorCount",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = InkObsidian
                                    )
                                    Text(
                                        text = "UK Guests",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Daily Quote of the Day
            if (prefs.showQuoteWidget) {
                item {
                    Glass3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Quote of the Day",
                                tint = extra.accent,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DAILY QUOTE · ${prefs.aiTagline.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extra.accent
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "\"${quotePair.first}\"",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = InkObsidian
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "— ${quotePair.second}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Category Portal Grid
            item {
                Text(
                    text = "TRJ CURATED HOUSES",
                    style = MaterialTheme.typography.labelSmall,
                    color = extra.accent
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryTile(
                        title = "TRJ Cars",
                        subtitle = "6 Models · 9M–550M",
                        icon = Icons.Default.DirectionsCar,
                        onClick = { onOpenCategory(ProductCategory.CARS) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryTile(
                        title = "UK Hotels",
                        subtitle = "4 Estates / Day",
                        icon = Icons.Default.Hotel,
                        onClick = { onOpenCategory(ProductCategory.HOTELS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryTile(
                        title = "Ceylon Tea",
                        subtitle = "100g to 1KG",
                        icon = Icons.Default.LocalCafe,
                        onClick = { onOpenCategory(ProductCategory.TEA) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryTile(
                        title = "Perfume",
                        subtitle = "Cinnamon 100–500ml",
                        icon = Icons.Default.Spa,
                        onClick = { onOpenCategory(ProductCategory.PERFUME) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Interactive 3D Object Showcase
            item {
                Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Interactive3DPrismCanvas(
                            modifier = Modifier.size(128.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "INTERACTIVE 3D ATELIER",
                                style = MaterialTheme.typography.labelSmall,
                                color = extra.glow
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Ronal Sovereign Prism",
                                style = MaterialTheme.typography.titleLarge,
                                color = InkObsidian
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Drag the 3D crystal prism with your finger to inspect its real-time faceted reflections.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Featured Showcase Carousel
            item {
                Text(
                    text = "FLAGSHIP SIGNATURES",
                    style = MaterialTheme.typography.labelSmall,
                    color = extra.accent
                )
                Spacer(Modifier.height(8.dp))
                val featured = listOf(
                    RonalCatalog.products[1],  // TRJ VVIP 550M
                    RonalCatalog.products[6],  // UK Ultra Luxury
                    RonalCatalog.products[13], // Tea 1KG
                    RonalCatalog.products[16]  // Perfume 500ml
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(featured, key = { it.id }) { item ->
                        Glass3DCard(
                            modifier = Modifier.width(240.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Column {
                                ProductVarietyArtwork(
                                    product = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(136.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = InkObsidian,
                                    maxLines = 1
                                )
                                Text(
                                    text = item.varietyLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.priceDisplay} ${item.unitSuffix}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = extra.accent
                                    )
                                    Surface(
                                        onClick = { onAddToCart(item) },
                                        shape = RoundedCornerShape(50),
                                        color = InkObsidian
                                    ) {
                                        Text(
                                            text = "+ Bag",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PureWhite,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // UK Offline Atlas Map
            item {
                UkConciergeMapCard(
                    selectedPin = selectedUkPin,
                    onSelectPin = { selectedUkPin = it }
                )
            }
        }

        // Back to Top Button
        AnimatedVisibility(
            visible = showBackToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 100.dp)
        ) {
            FloatingActionButton(
                onClick = { scope.launch { listState.animateScrollToItem(0) } },
                shape = CircleShape,
                containerColor = InkObsidian,
                contentColor = PureWhite,
                modifier = Modifier.size(48.dp).testTag("home_back_to_top")
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Back to Top")
            }
        }
    }
}

@Composable
private fun CategoryTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    Glass3DCard(
        modifier = modifier,
        cornerRadius = 20.dp,
        onClick = onClick,
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = extra.accent.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, extra.accent.copy(alpha = 0.35f)),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = extra.accent)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkObsidian,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

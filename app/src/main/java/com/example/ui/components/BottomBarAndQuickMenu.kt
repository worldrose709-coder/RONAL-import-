package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AppNotificationEntity
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite

enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    CATALOG("catalog", "Catalog", Icons.Default.Storefront),
    VIP("vip", "VIP", Icons.Default.Diamond),
    STUDIO("studio", "Studio", Icons.Default.Palette),
    CHECKOUT("checkout", "Checkout", Icons.Default.ShoppingBag)
}

@Composable
fun RonalTopBar(
    isOfflineMode: Boolean,
    isMusicPlaying: Boolean,
    unreadNotifications: Int,
    onToggleOffline: () -> Unit,
    onToggleMusic: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    Surface(
        color = PureWhite.copy(alpha = 0.92f),
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RONAL IMPORTS",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkObsidian
                )
                Text(
                    text = "TRJ · MAYFAIR LONDON, UK",
                    style = MaterialTheme.typography.labelSmall,
                    color = extra.accent
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    onClick = onToggleOffline,
                    shape = RoundedCornerShape(50),
                    color = if (isOfflineMode) Color(0xFFFEF3C7) else Color(0xFFF3F4F6),
                    border = BorderStroke(1.dp, if (isOfflineMode) ChampagneGold else Color(0xFFE5E7EB)),
                    modifier = Modifier.testTag("offline_mode_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.Wifi,
                            contentDescription = "Offline Mode Toggle",
                            tint = if (isOfflineMode) ChampagneGold else extra.accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isOfflineMode) "Offline" else "Online",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkObsidian
                        )
                    }
                }

                IconButton(
                    onClick = onToggleMusic,
                    modifier = Modifier.testTag("top_music_toggle")
                ) {
                    Icon(
                        imageVector = if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isMusicPlaying) "Pause Music" else "Play Music",
                        tint = if (isMusicPlaying) extra.accent else InkObsidian
                    )
                }

                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.testTag("notification_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge(containerColor = extra.accent, contentColor = PureWhite) {
                                    Text("$unreadNotifications")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = InkObsidian
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RonalBottomNavigationBar(
    currentRoute: String?,
    cartCount: Int,
    onNavigate: (AppDestination) -> Unit
) {
    val extra = LocalRonalColors.current
    NavigationBar(
        containerColor = PureWhite.copy(alpha = 0.96f),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        AppDestination.entries.forEach { dest ->
            val selected = currentRoute == dest.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(dest) },
                icon = {
                    if (dest == AppDestination.CHECKOUT && cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = extra.accent, contentColor = PureWhite) {
                                    Text("$cartCount")
                                }
                            }
                        ) {
                            Icon(dest.icon, contentDescription = dest.label)
                        }
                    } else {
                        Icon(dest.icon, contentDescription = dest.label)
                    }
                },
                label = { Text(dest.label, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = extra.accent,
                    selectedTextColor = InkObsidian,
                    indicatorColor = extra.accent.copy(alpha = 0.14f),
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280)
                ),
                modifier = Modifier.testTag("nav_${dest.route}")
            )
        }
    }
}

@Composable
fun FloatingQuickMenu(
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    onOpenAiStudio: () -> Unit,
    onOpenCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickMenuChip(
                    label = if (isMusicPlaying) "Pause Music" else "Play Music",
                    icon = Icons.Default.MusicNote,
                    onClick = {
                        onToggleMusic()
                        expanded = false
                    }
                )
                QuickMenuChip(
                    label = "AI Theme Designer",
                    icon = Icons.Default.AutoAwesome,
                    onClick = {
                        onOpenAiStudio()
                        expanded = false
                    }
                )
                QuickMenuChip(
                    label = "WhatsApp Checkout",
                    icon = Icons.Default.ShoppingBag,
                    onClick = {
                        onOpenCheckout()
                        expanded = false
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            shape = CircleShape,
            containerColor = extra.accent,
            contentColor = PureWhite,
            modifier = Modifier.size(52.dp).testTag("floating_quick_menu")
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Quick Menu"
            )
        }
    }
}

@Composable
private fun QuickMenuChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            color = PureWhite,
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = InkObsidian,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = InkObsidian,
            contentColor = PureWhite
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsModalSheet(
    notifications: List<AppNotificationEntity>,
    onMarkAllRead: () -> Unit,
    onDismiss: () -> Unit
) {
    val extra = LocalRonalColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PureWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Concierge & Bill Alerts",
                    style = MaterialTheme.typography.headlineMedium,
                    color = InkObsidian
                )
                TextButton(onClick = onMarkAllRead) {
                    Text("Mark All Read")
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(320.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (item.isRead) Color(0xFFF9FAFB) else extra.accent.copy(alpha = 0.09f),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${item.category.uppercase()} · ${item.title}",
                                style = MaterialTheme.typography.labelLarge,
                                color = InkObsidian
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = item.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

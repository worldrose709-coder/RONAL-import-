package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.AmbientTrack
import com.example.data.ai.AiThemeUiState
import com.example.data.local.UserPreferencesState
import com.example.data.model.RonalCatalog
import com.example.data.model.ThemeSwatch
import com.example.data.model.WallpaperStyle
import com.example.ui.components.Glass3DCard
import com.example.ui.components.PremiumShimmerButton
import com.example.ui.theme.AppFontOption
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioPersonalizeScreen(
    prefs: UserPreferencesState,
    aiUiState: AiThemeUiState,
    onGenerateAiTheme: (String) -> Unit,
    onSelectSwatch: (ThemeSwatch) -> Unit,
    onSelectProfileColor: (Color) -> Unit,
    onSelectFont: (String) -> Unit,
    onSelectWallpaper: (String) -> Unit,
    onCustomBgPicked: (String) -> Unit,
    onToggleMusic: () -> Unit,
    onSelectTrack: (AmbientTrack) -> Unit,
    onUpdateAudio: (Float, Boolean) -> Unit,
    onToggleOfflineMode: (Boolean) -> Unit,
    onUpdateWidgets: (Boolean, Boolean, Boolean, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    val context = LocalContext.current
    var moodPrompt by remember { mutableStateOf("Mayfair white marble with champagne gold and sapphire neon") }

    val colorFamilies = remember { RonalCatalog.themeSwatches.map { it.family }.distinct() }
    var activeFamily by remember { mutableStateOf(colorFamilies.first()) }
    val familySwatches = remember(activeFamily) {
        RonalCatalog.themeSwatches.filter { it.family == activeFamily }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            onCustomBgPicked(uri.toString())
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Theme Designer (Firebase AI Logic)
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Designer", tint = extra.accent)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "AI THEME DESIGNER",
                                style = MaterialTheme.typography.labelSmall,
                                color = extra.accent
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (prefs.isOfflineMode) Color(0xFFFEF3C7) else extra.glow.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = if (prefs.isOfflineMode) "Offline Engine" else "Gemini AI Live",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkObsidian,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Describe your white luxury atmosphere and let AI compose custom accent colors, neon glow, font & animated wallpaper.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = moodPrompt,
                        onValueChange = { moodPrompt = it },
                        label = { Text("Theme Mood Prompt") },
                        modifier = Modifier.fillMaxWidth().testTag("ai_theme_prompt_input")
                    )
                    Spacer(Modifier.height(10.dp))
                    PremiumShimmerButton(
                        text = if (aiUiState is AiThemeUiState.Loading) "Designing Theme..." else "Design White Luxury Theme",
                        enabled = aiUiState !is AiThemeUiState.Loading,
                        onClick = { onGenerateAiTheme(moodPrompt) },
                        testTag = "ai_design_button",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            if (aiUiState is AiThemeUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = PureWhite,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    )

                    when (aiUiState) {
                        is AiThemeUiState.Success -> {
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = extra.accent.copy(alpha = 0.10f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Applied: ${aiUiState.result.themeName} (${aiUiState.result.accentHex}) · \"${aiUiState.result.tagline}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkObsidian,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        is AiThemeUiState.Error -> {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = aiUiState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }

        // 100+ Theme Colors (108 Swatches)
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "100+ LUXURY THEME COLORS (${RonalCatalog.themeSwatches.size} SWATCHES)",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(colorFamilies) { family ->
                            val selected = family == activeFamily
                            Surface(
                                onClick = { activeFamily = family },
                                shape = RoundedCornerShape(50),
                                color = if (selected) InkObsidian else Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = family,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) PureWhite else InkObsidian,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        familySwatches.forEach { swatch ->
                            val isSelected = Color(prefs.accentColorArgb) == swatch.accentColor
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(swatch.accentColor)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.5.dp,
                                        color = if (isSelected) InkObsidian else swatch.glowColor,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectSwatch(swatch) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Profile Card Accent Color",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Color(0xFF1D4ED8),
                            Color(0xFFC59B27),
                            Color(0xFF047857),
                            Color(0xFF9333EA),
                            Color(0xFFDB2777),
                            Color(0xFF0F172A)
                        ).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(2.dp, PureWhite, CircleShape)
                                    .clickable { onSelectProfileColor(color) }
                            )
                        }
                    }
                }
            }
        }

        // Custom Font Selection (5 Bundled Google Fonts)
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CUSTOM FONT SELECTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    AppFontOption.entries.forEach { fontOpt ->
                        val active = prefs.fontId == fontOpt.id
                        Surface(
                            onClick = { onSelectFont(fontOpt.id) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (active) extra.accent.copy(alpha = 0.12f) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, if (active) extra.accent else Color(0xFFE5E7EB)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = fontOpt.displayName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = fontOpt.displayFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = InkObsidian
                                    )
                                    Text(
                                        text = fontOpt.tagline,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (active) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extra.accent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Wallpapers & Custom Background Upload
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ANIMATED WALLPAPERS & BACKGROUND UPLOAD",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    WallpaperStyle.entries.forEach { wp ->
                        val selected = prefs.wallpaperId == wp.id
                        Surface(
                            onClick = {
                                if (wp == WallpaperStyle.CUSTOM_PHOTO && prefs.customBgUri.isBlank()) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    onSelectWallpaper(wp.id)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected) InkObsidian else Color(0xFFF9FAFB),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = wp.label,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (selected) PureWhite else InkObsidian
                                    )
                                    Text(
                                        text = wp.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) PureWhite.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Upload Custom White-Blend Background")
                    }
                }
            }
        }

        // Website Music Player & Background Sound Control
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = "Music Player", tint = extra.accent)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "ATELIER MUSIC & SOUND CONTROL",
                                style = MaterialTheme.typography.labelSmall,
                                color = extra.accent
                            )
                        }
                        IconButton(onClick = onToggleMusic) {
                            Icon(
                                imageVector = if (prefs.isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (prefs.isMusicPlaying) "Pause Music" else "Play Music",
                                tint = InkObsidian
                            )
                        }
                    }

                    AmbientTrack.entries.forEach { track ->
                        val active = prefs.profileTrackId == track.id
                        Surface(
                            onClick = { onSelectTrack(track) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) extra.accent.copy(alpha = 0.12f) else Color(0xFFF9FAFB),
                            border = if (active) BorderStroke(1.dp, extra.accent) else null,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = InkObsidian
                                    )
                                    Text(
                                        text = track.mood,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = if (active && prefs.isMusicPlaying) "PLAYING" else "SELECT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = extra.accent
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onUpdateAudio(prefs.soundVolume, !prefs.isSoundMuted) }) {
                            Icon(
                                imageVector = if (prefs.isSoundMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Mute Toggle",
                                tint = InkObsidian
                            )
                        }
                        Slider(
                            value = prefs.soundVolume,
                            onValueChange = { onUpdateAudio(it, false) },
                            colors = SliderDefaults.colors(
                                thumbColor = extra.accent,
                                activeTrackColor = extra.accent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Personal Dashboard Customization & Offline Mode
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DASHBOARD & OFFLINE MODE",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    ToggleRow("Offline Mode (Zero Server Dependency)", prefs.isOfflineMode) {
                        onToggleOfflineMode(it)
                    }
                    ToggleRow("Show UK Digital Clock Widget", prefs.showClockWidget) {
                        onUpdateWidgets(it, prefs.showWeatherWidget, prefs.showVisitorWidget, prefs.showQuoteWidget)
                    }
                    ToggleRow("Show UK Mayfair Weather Widget", prefs.showWeatherWidget) {
                        onUpdateWidgets(prefs.showClockWidget, it, prefs.showVisitorWidget, prefs.showQuoteWidget)
                    }
                    ToggleRow("Show Live Visitor Counter", prefs.showVisitorWidget) {
                        onUpdateWidgets(prefs.showClockWidget, prefs.showWeatherWidget, it, prefs.showQuoteWidget)
                    }
                    ToggleRow("Show Daily Quote of the Day", prefs.showQuoteWidget) {
                        onUpdateWidgets(prefs.showClockWidget, prefs.showWeatherWidget, prefs.showVisitorWidget, it)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkObsidian,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

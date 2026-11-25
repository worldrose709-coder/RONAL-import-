package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.WhatsAppService
import com.example.data.local.CalendarBookingEntity
import com.example.data.local.PersonalNoteEntity
import com.example.data.local.UserPreferencesState
import com.example.data.model.DigitalGift
import com.example.data.model.ProfileFrameStyle
import com.example.data.model.RonalCatalog
import com.example.data.model.VipLevel
import com.example.ui.components.AnimatedProfileAvatar
import com.example.ui.components.Glass3DCard
import com.example.ui.components.PremiumShimmerButton
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VipLoungeScreen(
    prefs: UserPreferencesState,
    notes: List<PersonalNoteEntity>,
    bookings: List<CalendarBookingEntity>,
    selectedBookingDate: String,
    onClaimDailyReward: () -> Unit,
    onClaimDigitalGift: (DigitalGift) -> Unit,
    onSaveProfile: (String, String, String) -> Unit,
    onAddNote: (String, String, String) -> Unit,
    onDeleteNote: (Int) -> Unit,
    onSelectBookingDate: (String) -> Unit,
    onAddCalendarBooking: (String, String, String) -> Unit,
    onDeleteCalendarBooking: (Int) -> Unit,
    onStatusMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val vipLevel = VipLevel.forPoints(prefs.vipPoints)
    val badges = remember(prefs.unlockedBadgesCsv) {
        prefs.unlockedBadgesCsv.split(",").filter { it.isNotBlank() }
    }
    val claimedGifts = remember(prefs.claimedGiftsCsv) {
        prefs.claimedGiftsCsv.split(",").filter { it.isNotBlank() }.toSet()
    }

    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    var nameInput by remember(prefs.memberName) { mutableStateOf(prefs.memberName) }
    var welcomeInput by remember(prefs.customWelcomeMessage) { mutableStateOf(prefs.customWelcomeMessage) }
    var selectedFrame by remember(prefs.profileFrameId) { mutableStateOf(prefs.profileFrameId) }

    var noteTitle by remember { mutableStateOf("") }
    var noteBody by remember { mutableStateOf("") }

    var bookingTitle by remember { mutableStateOf("Mayfair TRJ Private Viewing") }

    // Upcoming 10 UK dates for Interactive Calendar
    val upcomingDates = remember {
        val cal = Calendar.getInstance()
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val labelFmt = SimpleDateFormat("EEE\ndd MMM", Locale.UK)
        (0 until 10).map { offset ->
            val clone = cal.clone() as Calendar
            clone.add(Calendar.DAY_OF_YEAR, offset)
            isoFmt.format(clone.time) to labelFmt.format(clone.time)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Tiltable VIP Membership Card
        item {
            val cardShape = RoundedCornerShape(26.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationX = tiltX
                        rotationY = tiltY
                        cameraDistance = 14f * density
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                tiltX = 0f
                                tiltY = 0f
                            },
                            onDragCancel = {
                                tiltX = 0f
                                tiltY = 0f
                            }
                        ) { change, drag ->
                            change.consume()
                            tiltY = (tiltY + drag.x * 0.12f).coerceIn(-16f, 16f)
                            tiltX = (tiltX - drag.y * 0.12f).coerceIn(-12f, 12f)
                        }
                    }
                    .shadow(
                        elevation = 16.dp,
                        shape = cardShape,
                        ambientColor = extra.glow.copy(alpha = 0.25f),
                        spotColor = ChampagneGold.copy(alpha = 0.3f)
                    )
                    .clip(cardShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PureWhite,
                                Color(0xFFFFFBEB),
                                extra.glow.copy(alpha = 0.12f),
                                PureWhite
                            )
                        )
                    )
                    .border(
                        width = 1.6.dp,
                        brush = Brush.linearGradient(listOf(ChampagneGold, extra.glow, ChampagneGold)),
                        shape = cardShape
                    )
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RONAL IMPORTS · UK",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold
                            )
                            Text(
                                text = "Sovereign Membership Card",
                                style = MaterialTheme.typography.titleLarge,
                                color = InkObsidian
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified VIP",
                                tint = ChampagneGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "VERIFIED",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val frame = ProfileFrameStyle.entries.firstOrNull { it.id == selectedFrame }
                            ?: ProfileFrameStyle.GOLDEN_HALO
                        AnimatedProfileAvatar(
                            initials = prefs.memberName,
                            frameStyle = frame,
                            profileColor = Color(prefs.profileColorArgb),
                            size = 58.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = prefs.memberName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = InkObsidian
                            )
                            Text(
                                text = "${vipLevel.title} · ${prefs.vipPoints} Points",
                                style = MaterialTheme.typography.labelLarge,
                                color = extra.accent
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = vipLevel.perkSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tip: Drag card gently to tilt in 3D · Member ID TRJ-UK-2026-071",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.glow
                    )
                }
            }
        }

        // Member Levels & Daily Rewards
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VIP LEVELS & REWARDS",
                            style = MaterialTheme.typography.labelSmall,
                            color = extra.accent
                        )
                        Text(
                            text = "Streak: ${prefs.loginStreak} Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    VipLevel.entries.forEach { lvl ->
                        val unlocked = prefs.vipPoints >= lvl.minPoints
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.WorkspacePremium,
                                    contentDescription = lvl.title,
                                    tint = if (unlocked) ChampagneGold else Color(0xFF9CA3AF),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = lvl.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = InkObsidian
                                )
                            }
                            Text(
                                text = "${lvl.minPoints}+ pts",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (unlocked) extra.accent else Color(0xFF9CA3AF)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (prefs.vipPoints / 3500f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                        color = ChampagneGold,
                        trackColor = ChampagneGold.copy(alpha = 0.16f)
                    )
                    Spacer(Modifier.height(12.dp))
                    PremiumShimmerButton(
                        text = "Claim Daily VIP Login Bonus",
                        onClick = onClaimDailyReward,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Digital Gift System
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DIGITAL GIFT VAULT",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Text(
                        text = "Exclusive Vouchers & Compliments",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkObsidian
                    )
                    Spacer(Modifier.height(10.dp))
                    RonalCatalog.digitalGifts.forEach { gift ->
                        val isClaimed = claimedGifts.contains(gift.id)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isClaimed) extra.accent.copy(alpha = 0.08f) else Color(0xFFF9FAFB),
                            border = BorderStroke(
                                1.dp,
                                if (isClaimed) extra.accent else Color(0xFFE5E7EB)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.CardGiftcard,
                                        contentDescription = gift.title,
                                        tint = ChampagneGold
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = gift.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = InkObsidian
                                        )
                                        Text(
                                            text = "${gift.subtitle} · ${gift.valueDisplay}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { onClaimDigitalGift(gift) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isClaimed) ChampagneGold else InkObsidian,
                                        contentColor = PureWhite
                                    ),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(if (isClaimed) "${gift.codePrefix}-26" else "Claim")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Premium Download Options
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PREMIUM DOWNLOAD OPTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Text(
                        text = "Save VIP Pass & 2026 Price Dossier",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkObsidian
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val content = buildString {
                                    appendLine("RONAL IMPORTS — UK VIP PASS")
                                    appendLine("Member: ${prefs.memberName}")
                                    appendLine("Tier: ${vipLevel.title} (${prefs.vipPoints} pts)")
                                    appendLine("WhatsApp Line: ${prefs.whatsappNumber}")
                                    appendLine("Location: Mayfair, London, UK")
                                }
                                scope.launch {
                                    val res = WhatsAppService.saveDocumentToDownloads(
                                        context,
                                        "Ronal_VIP_Pass_${System.currentTimeMillis()}.txt",
                                        content
                                    )
                                    onStatusMessage(res.getOrElse { it.localizedMessage ?: "Download error" })
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Save VIP Pass")
                        }
                        OutlinedButton(
                            onClick = {
                                val dossier = buildString {
                                    appendLine("RONAL IMPORTS · TRJ 2026 UK CATALOG")
                                    RonalCatalog.products.forEach {
                                        appendLine("• ${it.name} (${it.varietyLabel}) — ${it.priceDisplay} ${it.unitSuffix}")
                                    }
                                }
                                WhatsAppService.shareTextDocument(context, "Ronal Imports Catalog", dossier)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share Dossier")
                        }
                    }
                }
            }
        }

        // Profile Badges, Animated Frame & Welcome Message Editor
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PROFILE BADGES & ANIMATED FRAME",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        badges.forEach { badge ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = ChampagneGold.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkObsidian,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ProfileFrameStyle.entries) { frame ->
                            val active = frame.id == selectedFrame
                            Surface(
                                onClick = { selectedFrame = frame.id },
                                shape = RoundedCornerShape(12.dp),
                                color = if (active) InkObsidian else Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = frame.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (active) PureWhite else InkObsidian,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("VIP Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = welcomeInput,
                        onValueChange = { welcomeInput = it },
                        label = { Text("Custom Welcome Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    PremiumShimmerButton(
                        text = "Save VIP Profile",
                        onClick = { onSaveProfile(nameInput, welcomeInput, selectedFrame) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Interactive UK Booking Calendar
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = extra.accent)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "INTERACTIVE UK CALENDAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = extra.accent
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(upcomingDates) { (iso, label) ->
                            val selected = iso == selectedBookingDate
                            Surface(
                                onClick = { onSelectBookingDate(iso) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) InkObsidian else Color(0xFFF3F4F6),
                                border = if (selected) BorderStroke(1.dp, ChampagneGold) else null
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) PureWhite else InkObsidian,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = bookingTitle,
                        onValueChange = { bookingTitle = it },
                        label = { Text("Reservation / Stay Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onAddCalendarBooking(selectedBookingDate, bookingTitle, "Mayfair, London UK")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = InkObsidian),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pin UK Date ($selectedBookingDate)")
                    }
                    if (bookings.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        bookings.forEach { b ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${b.dateIso} · ${b.title}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkObsidian,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { onDeleteCalendarBooking(b.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove booking", tint = Color(0xFF9CA3AF))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Personal Notes (Offline Room Persistence)
        item {
            Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditNote, contentDescription = "Notes", tint = extra.accent)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "PERSONAL CONCIERGE NOTES (OFFLINE)",
                            style = MaterialTheme.typography.labelSmall,
                            color = extra.accent
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Note Title (e.g. Tea Gift List)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("note_title_input")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteBody,
                        onValueChange = { noteBody = it },
                        label = { Text("Private Note Details") },
                        modifier = Modifier.fillMaxWidth().testTag("note_body_input")
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onAddNote(noteTitle, noteBody, "VIP Note")
                            noteTitle = ""
                            noteBody = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = InkObsidian),
                        modifier = Modifier.fillMaxWidth().testTag("save_note_button")
                    ) {
                        Text("Save Note Offline")
                    }
                    Spacer(Modifier.height(8.dp))
                    notes.forEach { note ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF9FAFB),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = InkObsidian
                                    )
                                    if (note.body.isNotBlank()) {
                                        Text(
                                            text = note.body,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(onClick = { onDeleteNote(note.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete note")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.ProfileFrameStyle
import com.example.data.model.RonalCatalog
import com.example.data.model.UkLocationPin
import com.example.data.model.WallpaperStyle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.InkObsidian
import com.example.ui.theme.LocalRonalColors
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AchievementBanner
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedWhiteLuxuryBackground(
    wallpaperStyle: WallpaperStyle,
    customBgUri: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val extra = LocalRonalColors.current
    val infinite = rememberInfiniteTransition(label = "bg_transition")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_phase"
    )
    val floatDrift by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_drift"
    )

    Box(modifier = modifier.fillMaxSize().background(PureWhite)) {
        if (wallpaperStyle == WallpaperStyle.CUSTOM_PHOTO && customBgUri.isNotBlank()) {
            AsyncImage(
                model = Uri.parse(customBgUri),
                contentDescription = "Custom user wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.22f
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Crisp white animated gradient wash
            val shiftX = w * (0.5f + 0.28f * cos(phase))
            val shiftY = h * (0.3f + 0.20f * sin(phase))
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        extra.accent.copy(alpha = 0.09f),
                        extra.glow.copy(alpha = 0.05f),
                        PureWhite
                    ),
                    center = Offset(shiftX, shiftY),
                    radius = w * 1.15f
                )
            )

            when (wallpaperStyle) {
                WallpaperStyle.AURORA_PRISM -> {
                    val orb1 = Offset(w * 0.2f + w * 0.1f * floatDrift, h * 0.22f)
                    val orb2 = Offset(w * 0.82f - w * 0.1f * floatDrift, h * 0.72f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(extra.glow.copy(alpha = 0.14f), Color.Transparent),
                            center = orb1,
                            radius = w * 0.55f
                        ),
                        center = orb1,
                        radius = w * 0.55f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(extra.accent.copy(alpha = 0.14f), Color.Transparent),
                            center = orb2,
                            radius = w * 0.55f
                        ),
                        center = orb2,
                        radius = w * 0.55f
                    )
                }
                WallpaperStyle.ROYAL_GRID -> {
                    val step = 72f
                    val gridColor = extra.accent.copy(alpha = 0.07f)
                    var x = (phase * 14f) % step
                    while (x < w) {
                        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                        x += step
                    }
                    var y = (phase * 10f) % step
                    while (y < h) {
                        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                        y += step
                    }
                }
                else -> Unit
            }

            // Magical floating particles across all white themes
            val particleCount = if (wallpaperStyle == WallpaperStyle.STARLIGHT_DUST) 28 else 16
            for (i in 0 until particleCount) {
                val seed = i * 1.37f
                val px = ((sin(seed + phase * 0.25f) * 0.45f + 0.5f) * w)
                val py = (((i / particleCount.toFloat()) + (phase / (2f * PI.toFloat())) * (0.4f + (i % 3) * 0.15f)) % 1f) * h
                val radius = (2.2f + (i % 4) * 1.4f)
                val pColor = if (i % 2 == 0) extra.accent else extra.glow
                drawCircle(
                    color = pColor.copy(alpha = 0.22f + (i % 3) * 0.08f),
                    radius = radius,
                    center = Offset(px, py)
                )
            }
        }

        content()
    }
}

@Composable
fun Glass3DCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    glowEnabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val extra = LocalRonalColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "card_scale"
    )
    val tiltX by animateFloatAsState(
        targetValue = if (isPressed) 2.5f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "card_tilt"
    )

    val shape = RoundedCornerShape(cornerRadius)
    val shadowColor = if (glowEnabled) extra.glow.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationX = tiltX
                cameraDistance = 12f * density
            }
            .shadow(
                elevation = if (glowEnabled) 14.dp else 8.dp,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        PureWhite.copy(alpha = 0.96f),
                        PureWhite.copy(alpha = 0.86f),
                        extra.accent.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        PureWhite,
                        extra.accent.copy(alpha = 0.35f),
                        extra.glow.copy(alpha = 0.30f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun AnimatedGradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    val infinite = rememberInfiniteTransition(label = "text_gradient")
    val offset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_shimmer"
    )
    val brush = Brush.linearGradient(
        colors = listOf(InkObsidian, extra.accent, extra.glow, InkObsidian),
        start = Offset(offset, 0f),
        end = Offset(offset + 420f, 120f)
    )
    Text(
        text = text,
        style = style.copy(brush = brush),
        modifier = modifier
    )
}

@Composable
fun PremiumShimmerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val extra = LocalRonalColors.current
    val infinite = rememberInfiniteTransition(label = "btn_sheen")
    val sheen by infinite.animateFloat(
        initialValue = -200f,
        targetValue = 700f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "btn_sheen_offset"
    )

    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = PureWhite,
            disabledContainerColor = Color(0xFFE5E7EB),
            disabledContentColor = Color(0xFF9CA3AF)
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 50.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = shape,
                ambientColor = extra.glow.copy(alpha = 0.35f),
                spotColor = extra.accent.copy(alpha = 0.35f)
            )
            .background(
                brush = if (enabled) {
                    Brush.linearGradient(
                        colors = listOf(
                            InkObsidian,
                            extra.accent,
                            extra.glow.copy(alpha = 0.85f)
                        ),
                        start = Offset(sheen, 0f),
                        end = Offset(sheen + 480f, 180f)
                    )
                } else {
                    Brush.linearGradient(listOf(Color(0xFFE5E7EB), Color(0xFFD1D5DB)))
                },
                shape = shape
            )
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun Interactive3DPrismCanvas(
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    var yaw by remember { mutableFloatStateOf(0.45f) }
    var pitch by remember { mutableFloatStateOf(0.32f) }
    val infinite = rememberInfiniteTransition(label = "prism_idle")
    val autoSpin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "prism_spin"
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    yaw += dragAmount.x * 0.012f
                    pitch = (pitch + dragAmount.y * 0.012f).coerceIn(-1.2f, 1.2f)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = minOf(size.width, size.height) * 0.36f
            val effectiveYaw = yaw + autoSpin * 0.35f

            // Octahedron vertices in 3D space
            val verts = listOf(
                floatArrayOf(0f, -1.25f, 0f),  // top apex
                floatArrayOf(1f, 0f, 0f),
                floatArrayOf(0f, 0f, 1f),
                floatArrayOf(-1f, 0f, 0f),
                floatArrayOf(0f, 0f, -1f),
                floatArrayOf(0f, 1.25f, 0f)    // bottom apex
            )

            fun project(v: FloatArray): Offset {
                val x1 = v[0] * cos(effectiveYaw) - v[2] * sin(effectiveYaw)
                val z1 = v[0] * sin(effectiveYaw) + v[2] * cos(effectiveYaw)
                val y2 = v[1] * cos(pitch) - z1 * sin(pitch)
                val z2 = v[1] * sin(pitch) + z1 * cos(pitch)
                val perspective = 2.8f / (3.4f - z2)
                return Offset(cx + x1 * r * perspective, cy + y2 * r * perspective)
            }

            val pts = verts.map { project(it) }

            // Soft glow halo behind prism
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(extra.glow.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = r * 1.45f
                ),
                radius = r * 1.45f,
                center = Offset(cx, cy)
            )

            // Shaded facets
            val upperFaces = listOf(Triple(0, 1, 2), Triple(0, 2, 3), Triple(0, 3, 4), Triple(0, 4, 1))
            val lowerFaces = listOf(Triple(5, 1, 2), Triple(5, 2, 3), Triple(5, 3, 4), Triple(5, 4, 1))

            upperFaces.forEachIndexed { idx, (a, b, c) ->
                val path = Path().apply {
                    moveTo(pts[a].x, pts[a].y)
                    lineTo(pts[b].x, pts[b].y)
                    lineTo(pts[c].x, pts[c].y)
                    close()
                }
                val facetColor = if (idx % 2 == 0) extra.accent else extra.glow
                drawPath(path, color = facetColor.copy(alpha = 0.16f + idx * 0.04f))
            }
            lowerFaces.forEachIndexed { idx, (a, b, c) ->
                val path = Path().apply {
                    moveTo(pts[a].x, pts[a].y)
                    lineTo(pts[b].x, pts[b].y)
                    lineTo(pts[c].x, pts[c].y)
                    close()
                }
                val facetColor = if (idx % 2 == 0) extra.glow else extra.accent
                drawPath(path, color = facetColor.copy(alpha = 0.10f + idx * 0.03f))
            }

            // Crisp metallic edges
            val edges = listOf(
                0 to 1, 0 to 2, 0 to 3, 0 to 4,
                5 to 1, 5 to 2, 5 to 3, 5 to 4,
                1 to 2, 2 to 3, 3 to 4, 4 to 1
            )
            edges.forEach { (i, j) ->
                drawLine(
                    brush = Brush.linearGradient(listOf(extra.accent, extra.glow)),
                    start = pts[i],
                    end = pts[j],
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
            pts.forEach { p ->
                drawCircle(color = PureWhite, radius = 4.5f, center = p)
                drawCircle(color = extra.accent, radius = 2.8f, center = p)
            }
        }
    }
}

@Composable
fun UkConciergeMapCard(
    selectedPin: UkLocationPin,
    onSelectPin: (UkLocationPin) -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalRonalColors.current
    Glass3DCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "UK CONCIERGE LOCATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.accent
                    )
                    Text(
                        text = "Offline Great Britain Atlas",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkObsidian
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = extra.glow.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "No GPS Needed",
                        style = MaterialTheme.typography.labelSmall,
                        color = extra.glow,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            ) {
                // Stylized Great Britain outline + route connections
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Subtle longitude/latitude grid
                    for (i in 1..5) {
                        val gx = w * i / 6f
                        val gy = h * i / 6f
                        drawLine(Color(0xFFE2E8F0), Offset(gx, 0f), Offset(gx, h), 1f)
                        drawLine(Color(0xFFE2E8F0), Offset(0f, gy), Offset(w, gy), 1f)
                    }

                    // Great Britain stylized landmass polygon
                    val ukPath = Path().apply {
                        moveTo(w * 0.42f, h * 0.10f) // Highlands
                        lineTo(w * 0.56f, h * 0.12f)
                        lineTo(w * 0.52f, h * 0.25f) // Edinburgh coast
                        lineTo(w * 0.59f, h * 0.38f)
                        lineTo(w * 0.66f, h * 0.54f) // East Anglia
                        lineTo(w * 0.72f, h * 0.68f) // Kent
                        lineTo(w * 0.64f, h * 0.79f) // London/South
                        lineTo(w * 0.46f, h * 0.83f)
                        lineTo(w * 0.31f, h * 0.88f) // Cornwall
                        lineTo(w * 0.36f, h * 0.72f) // Wales
                        lineTo(w * 0.32f, h * 0.58f)
                        lineTo(w * 0.39f, h * 0.43f)
                        lineTo(w * 0.35f, h * 0.26f)
                        close()
                    }
                    drawPath(
                        path = ukPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(PureWhite, extra.accent.copy(alpha = 0.14f))
                        )
                    )
                    drawPath(
                        path = ukPath,
                        color = extra.accent.copy(alpha = 0.55f),
                        style = Stroke(width = 2.5f)
                    )

                    // Route line between pins
                    val pins = RonalCatalog.ukLocations
                    for (i in 0 until pins.size - 1) {
                        val p1 = Offset(w * pins[i].xFraction, h * pins[i].yFraction)
                        val p2 = Offset(w * pins[i + 1].xFraction, h * pins[i + 1].yFraction)
                        drawLine(
                            color = extra.glow.copy(alpha = 0.45f),
                            start = p1,
                            end = p2,
                            strokeWidth = 2f
                        )
                    }
                }

                // Interactive location pins
                RonalCatalog.ukLocations.forEach { pin ->
                    val isSelected = pin.id == selectedPin.id
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Surface(
                            onClick = { onSelectPin(pin) },
                            shape = CircleShape,
                            color = if (isSelected) extra.accent else PureWhite,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) extra.glow else extra.accent
                            ),
                            shadowElevation = if (isSelected) 8.dp else 3.dp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = (pin.xFraction * 270).dp,
                                    y = (pin.yFraction * 165).dp
                                )
                                .size(if (isSelected) 36.dp else 28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = pin.name,
                                    tint = if (isSelected) PureWhite else extra.accent,
                                    modifier = Modifier.size(if (isSelected) 20.dp else 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RonalCatalog.ukLocations.take(3).forEach { pin ->
                    val active = pin.id == selectedPin.id
                    Surface(
                        onClick = { onSelectPin(pin) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) extra.accent.copy(alpha = 0.14f) else Color(0xFFF3F4F6),
                        border = if (active) BorderStroke(1.dp, extra.accent) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = pin.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = InkObsidian,
                                maxLines = 1
                            )
                            Text(
                                text = pin.district,
                                style = MaterialTheme.typography.labelSmall,
                                color = extra.accent,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "${selectedPin.name} (${selectedPin.district}) · ${selectedPin.specialty}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedProfileAvatar(
    initials: String,
    frameStyle: ProfileFrameStyle,
    profileColor: Color,
    size: Dp = 64.dp,
    showVerifiedBadge: Boolean = true
) {
    val extra = LocalRonalColors.current
    val infinite = rememberInfiniteTransition(label = "profile_frame")
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "frame_rot"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "frame_pulse"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 6f
            val r = (this.size.minDimension / 2f) - strokeW
            when (frameStyle) {
                ProfileFrameStyle.GOLDEN_HALO -> {
                    rotate(rotation) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(ChampagneGold, PureWhite, extra.accent, ChampagneGold)
                            ),
                            radius = r,
                            style = Stroke(width = strokeW)
                        )
                    }
                }
                ProfileFrameStyle.NEON_PULSE -> {
                    drawCircle(
                        brush = Brush.sweepGradient(listOf(extra.glow, extra.accent, extra.glow)),
                        radius = r * pulse,
                        style = Stroke(width = strokeW)
                    )
                }
                ProfileFrameStyle.ROYAL_CREST -> {
                    rotate(rotation) {
                        drawCircle(
                            color = extra.accent,
                            radius = r,
                            style = Stroke(width = strokeW)
                        )
                        val orbitX = center.x + r * cos(0f)
                        val orbitY = center.y + r * sin(0f)
                        drawCircle(color = extra.glow, radius = 6f, center = Offset(orbitX, orbitY))
                    }
                }
                ProfileFrameStyle.PEARL_AURA -> {
                    drawCircle(
                        brush = Brush.linearGradient(listOf(PureWhite, profileColor, extra.accent)),
                        radius = r,
                        style = Stroke(width = strokeW)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(size - 12.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(profileColor, extra.accent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials.take(2).uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = PureWhite
            )
        }

        if (showVerifiedBadge) {
            Surface(
                shape = CircleShape,
                color = PureWhite,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Golden Verified VIP Badge",
                    tint = ChampagneGold,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun AchievementUnlockOverlay(
    banner: AchievementBanner?,
    onDismiss: () -> Unit
) {
    val extra = LocalRonalColors.current
    AnimatedVisibility(
        visible = banner != null,
        enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.88f, animationSpec = spring(dampingRatio = 0.7f)),
        exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.92f)
    ) {
        if (banner != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Glass3DCard(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    cornerRadius = 28.dp,
                    onClick = onDismiss
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ChampagneGold.copy(alpha = 0.16f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Achievement unlocked",
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "ACHIEVEMENT UNLOCKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = extra.accent
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = InkObsidian,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = banner.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = extra.glow.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "+${banner.pointsAwarded} VIP Points",
                                style = MaterialTheme.typography.labelLarge,
                                color = InkObsidian,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RonalCustomLoadingScreen(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(tween(450))
    ) {
        val infinite = rememberInfiniteTransition(label = "splash_ring")
        val angle by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
            label = "splash_angle"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        rotate(angle) {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    listOf(ChampagneGold, Color(0xFF06B6D4), PureWhite, ChampagneGold)
                                ),
                                radius = size.minDimension / 2f - 6f,
                                style = Stroke(width = 6f)
                            )
                        }
                    }
                    Text(
                        text = "TRJ",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkObsidian
                    )
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "RONAL IMPORTS",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkObsidian
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Mayfair · London · Ceylon Reserve",
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold
                )
            }
        }
    }
}

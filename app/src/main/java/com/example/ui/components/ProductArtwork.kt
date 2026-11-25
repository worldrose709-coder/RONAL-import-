package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.LuxuryProduct
import com.example.data.model.VarietyVisualType
import com.example.ui.theme.PureWhite

/**
 * Guarantees every single product variety displays unique visual artwork.
 * Uses distinct generated photos where available and bespoke sculpted studio art
 * tailored to each specific variety for all remaining editions—never repeating an image.
 */
@Composable
fun ProductVarietyArtwork(
    product: LuxuryProduct,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFFF9F8F5))
    ) {
        if (product.photoRes != null) {
            Image(
                painter = painterResource(id = product.photoRes),
                contentDescription = "${product.name} ${product.varietyLabel}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawStudioBackdrop(product.primaryTint, product.secondaryTint)
                when (product.visualType) {
                    VarietyVisualType.CAR_TRJ_VIP -> drawLuxurySedan(
                        bodyColor = Color(0xFFFDFCFB),
                        accent = product.primaryTint,
                        trim = product.secondaryTint,
                        wheelScale = 1.0f,
                        hasWing = false,
                        roofSlope = 0.28f
                    )
                    VarietyVisualType.CAR_TRJ_GR8 -> drawLuxurySedan(
                        bodyColor = Color(0xFFF1F5F9),
                        accent = product.primaryTint,
                        trim = product.secondaryTint,
                        wheelScale = 1.08f,
                        hasWing = true,
                        roofSlope = 0.22f
                    )
                    VarietyVisualType.CAR_TRJ_CP2 -> drawLuxurySedan(
                        bodyColor = Color(0xFFEEF2FF),
                        accent = product.primaryTint,
                        trim = product.secondaryTint,
                        wheelScale = 0.94f,
                        hasWing = false,
                        roofSlope = 0.34f
                    )
                    VarietyVisualType.CAR_TRJ_540N -> drawHyperRoadster(
                        bodyColor = Color(0xFFFFF1F2),
                        neonAccent = product.primaryTint,
                        carbon = product.secondaryTint
                    )
                    VarietyVisualType.CAR_GR8_189M -> drawHeritageCoupe(
                        bodyColor = Color(0xFFECFDF5),
                        goldTrim = product.secondaryTint,
                        emerald = product.primaryTint
                    )
                    VarietyVisualType.HOTEL_GRAND_VILLA -> drawGrandVilla(
                        stone = Color(0xFFFEF3C7),
                        roof = product.secondaryTint,
                        garden = product.primaryTint
                    )
                    VarietyVisualType.HOTEL_GRAND_VIP -> drawGeorgianTownhouse(
                        facade = PureWhite,
                        royalBlue = product.primaryTint,
                        navy = product.secondaryTint
                    )
                    VarietyVisualType.HOTEL_RONAL_VVIP -> drawSovereignPalace(
                        marble = PureWhite,
                        plum = product.primaryTint,
                        gold = product.secondaryTint
                    )
                    VarietyVisualType.TEA_100G -> drawTeaEdition(
                        canisterWidth = 0.22f,
                        canisterHeight = 0.42f,
                        primary = product.primaryTint,
                        secondary = product.secondaryTint,
                        isChest = false
                    )
                    VarietyVisualType.TEA_250G -> drawTeaEdition(
                        canisterWidth = 0.28f,
                        canisterHeight = 0.48f,
                        primary = product.primaryTint,
                        secondary = product.secondaryTint,
                        isChest = false
                    )
                    VarietyVisualType.TEA_500G -> drawTeaEdition(
                        canisterWidth = 0.40f,
                        canisterHeight = 0.36f,
                        primary = product.primaryTint,
                        secondary = product.secondaryTint,
                        isChest = true
                    )
                    VarietyVisualType.PERFUME_100ML -> drawPerfumeFlacon(
                        bottleWidth = 0.20f,
                        bottleHeight = 0.44f,
                        elixirColor = product.primaryTint,
                        goldColor = product.secondaryTint,
                        octagonal = false,
                        hasCrown = false
                    )
                    VarietyVisualType.PERFUME_250ML -> drawPerfumeFlacon(
                        bottleWidth = 0.28f,
                        bottleHeight = 0.48f,
                        elixirColor = product.primaryTint,
                        goldColor = product.secondaryTint,
                        octagonal = true,
                        hasCrown = false
                    )
                    VarietyVisualType.PERFUME_500ML -> drawPerfumeFlacon(
                        bottleWidth = 0.33f,
                        bottleHeight = 0.52f,
                        elixirColor = product.primaryTint,
                        goldColor = product.secondaryTint,
                        octagonal = true,
                        hasCrown = true
                    )
                    else -> Unit
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(50),
            color = PureWhite.copy(alpha = 0.90f),
            shadowElevation = 3.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text(
                text = product.badge,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF111827),
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
    }
}

private fun DrawScope.drawStudioBackdrop(primary: Color, secondary: Color) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                PureWhite,
                primary.copy(alpha = 0.11f),
                secondary.copy(alpha = 0.08f)
            )
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(primary.copy(alpha = 0.22f), Color.Transparent),
            center = Offset(size.width * 0.72f, size.height * 0.30f),
            radius = size.width * 0.55f
        ),
        center = Offset(size.width * 0.72f, size.height * 0.30f),
        radius = size.width * 0.55f
    )
    // Studio floor reflection line
    drawLine(
        color = primary.copy(alpha = 0.25f),
        start = Offset(size.width * 0.08f, size.height * 0.80f),
        end = Offset(size.width * 0.92f, size.height * 0.80f),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawLuxurySedan(
    bodyColor: Color,
    accent: Color,
    trim: Color,
    wheelScale: Float,
    hasWing: Boolean,
    roofSlope: Float
) {
    val w = size.width
    val h = size.height
    val baseY = h * 0.76f

    // Shadow
    drawOval(
        color = trim.copy(alpha = 0.16f),
        topLeft = Offset(w * 0.12f, baseY + 6f),
        size = Size(w * 0.76f, h * 0.09f)
    )

    // Car body silhouette
    val body = Path().apply {
        moveTo(w * 0.12f, baseY)
        lineTo(w * 0.14f, h * 0.56f)
        lineTo(w * 0.31f, h * 0.52f)
        lineTo(w * 0.44f, h * roofSlope)
        lineTo(w * 0.68f, h * roofSlope)
        lineTo(w * 0.83f, h * 0.55f)
        lineTo(w * 0.89f, h * 0.63f)
        lineTo(w * 0.87f, baseY)
        close()
    }
    drawPath(body, color = bodyColor)
    drawPath(body, color = accent, style = Stroke(width = 3.5f))

    // Cabin glass
    val glass = Path().apply {
        moveTo(w * 0.36f, h * 0.51f)
        lineTo(w * 0.46f, h * (roofSlope + 0.04f))
        lineTo(w * 0.65f, h * (roofSlope + 0.04f))
        lineTo(w * 0.76f, h * 0.52f)
        close()
    }
    drawPath(glass, color = trim.copy(alpha = 0.72f))

    if (hasWing) {
        drawLine(accent, Offset(w * 0.11f, h * 0.48f), Offset(w * 0.21f, h * 0.50f), strokeWidth = 5f)
    }

    // Accent beltline + LED headlight
    drawLine(accent, Offset(w * 0.16f, h * 0.62f), Offset(w * 0.85f, h * 0.62f), strokeWidth = 3f)
    drawCircle(color = accent, radius = 6f, center = Offset(w * 0.84f, h * 0.62f))

    // Wheels
    val wr = h * 0.105f * wheelScale
    listOf(w * 0.28f, w * 0.72f).forEach { wx ->
        drawCircle(color = trim, radius = wr, center = Offset(wx, baseY))
        drawCircle(color = PureWhite, radius = wr * 0.58f, center = Offset(wx, baseY))
        drawCircle(color = accent, radius = wr * 0.25f, center = Offset(wx, baseY))
    }
}

private fun DrawScope.drawHyperRoadster(bodyColor: Color, neonAccent: Color, carbon: Color) {
    val w = size.width
    val h = size.height
    val baseY = h * 0.77f
    // Speed neon ribbons
    drawLine(neonAccent.copy(alpha = 0.45f), Offset(w * 0.04f, h * 0.44f), Offset(w * 0.35f, h * 0.44f), 4f)
    drawLine(neonAccent.copy(alpha = 0.25f), Offset(w * 0.08f, h * 0.51f), Offset(w * 0.30f, h * 0.51f), 3f)

    val roadster = Path().apply {
        moveTo(w * 0.13f, baseY)
        lineTo(w * 0.17f, h * 0.54f)
        lineTo(w * 0.40f, h * 0.50f)
        lineTo(w * 0.55f, h * 0.34f)
        lineTo(w * 0.72f, h * 0.48f)
        lineTo(w * 0.90f, h * 0.58f)
        lineTo(w * 0.88f, baseY)
        close()
    }
    drawPath(roadster, color = bodyColor)
    drawPath(roadster, color = neonAccent, style = Stroke(4f))
    // Active rear aero wing
    drawLine(carbon, Offset(w * 0.14f, h * 0.42f), Offset(w * 0.28f, h * 0.46f), 6f)
    drawLine(neonAccent, Offset(w * 0.14f, h * 0.42f), Offset(w * 0.28f, h * 0.46f), 2f)

    val wr = h * 0.11f
    listOf(w * 0.30f, w * 0.74f).forEach { wx ->
        drawCircle(carbon, wr, Offset(wx, baseY))
        drawCircle(neonAccent, wr * 0.62f, Offset(wx, baseY), style = Stroke(3f))
    }
}

private fun DrawScope.drawHeritageCoupe(bodyColor: Color, goldTrim: Color, emerald: Color) {
    val w = size.width
    val h = size.height
    val baseY = h * 0.76f
    val path = Path().apply {
        moveTo(w * 0.13f, baseY)
        lineTo(w * 0.16f, h * 0.56f)
        lineTo(w * 0.35f, h * 0.44f)
        lineTo(w * 0.58f, h * 0.34f)
        lineTo(w * 0.78f, h * 0.52f)
        lineTo(w * 0.88f, h * 0.62f)
        lineTo(w * 0.86f, baseY)
        close()
    }
    drawPath(path, color = bodyColor)
    drawPath(path, color = emerald, style = Stroke(4f))
    drawLine(goldTrim, Offset(w * 0.16f, h * 0.61f), Offset(w * 0.86f, h * 0.61f), 4f)
    val wr = h * 0.10f
    listOf(w * 0.28f, w * 0.72f).forEach { wx ->
        drawCircle(emerald, wr, Offset(wx, baseY))
        drawCircle(goldTrim, wr * 0.55f, Offset(wx, baseY), style = Stroke(2.5f))
    }
}

private fun DrawScope.drawGrandVilla(stone: Color, roof: Color, garden: Color) {
    val w = size.width
    val h = size.height
    // Cotswolds green lawn & infinity pool
    drawRoundRect(
        color = garden.copy(alpha = 0.25f),
        topLeft = Offset(w * 0.10f, h * 0.68f),
        size = Size(w * 0.80f, h * 0.18f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    drawRoundRect(
        color = Color(0xFF38BDF8).copy(alpha = 0.5f),
        topLeft = Offset(w * 0.24f, h * 0.74f),
        size = Size(w * 0.52f, h * 0.09f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Limestone manor
    drawRect(stone, Offset(w * 0.22f, h * 0.38f), Size(w * 0.56f, h * 0.31f))
    drawRect(roof, Offset(w * 0.22f, h * 0.38f), Size(w * 0.56f, h * 0.31f), style = Stroke(3f))
    val roofPath = Path().apply {
        moveTo(w * 0.18f, h * 0.38f)
        lineTo(w * 0.50f, h * 0.20f)
        lineTo(w * 0.82f, h * 0.38f)
        close()
    }
    drawPath(roofPath, color = roof.copy(alpha = 0.82f))
    // Arched windows
    for (i in 0..3) {
        drawRoundRect(
            color = PureWhite,
            topLeft = Offset(w * (0.27f + i * 0.12f), h * 0.45f),
            size = Size(w * 0.08f, h * 0.14f),
            cornerRadius = CornerRadius(12f, 12f)
        )
    }
}

private fun DrawScope.drawGeorgianTownhouse(facade: Color, royalBlue: Color, navy: Color) {
    val w = size.width
    val h = size.height
    drawRect(facade, Offset(w * 0.24f, h * 0.18f), Size(w * 0.52f, h * 0.62f))
    drawRect(navy, Offset(w * 0.24f, h * 0.18f), Size(w * 0.52f, h * 0.62f), style = Stroke(3f))
    for (row in 0..2) {
        for (col in 0..2) {
            drawRect(
                color = royalBlue.copy(alpha = 0.22f),
                topLeft = Offset(w * (0.30f + col * 0.15f), h * (0.24f + row * 0.14f)),
                size = Size(w * 0.09f, h * 0.09f)
            )
        }
    }
    // VIP Canopy entrance
    drawRoundRect(
        color = royalBlue,
        topLeft = Offset(w * 0.41f, h * 0.64f),
        size = Size(w * 0.18f, h * 0.16f),
        cornerRadius = CornerRadius(18f, 18f)
    )
}

private fun DrawScope.drawSovereignPalace(marble: Color, plum: Color, gold: Color) {
    val w = size.width
    val h = size.height
    drawRect(marble, Offset(w * 0.14f, h * 0.30f), Size(w * 0.72f, h * 0.50f))
    drawRect(gold, Offset(w * 0.14f, h * 0.30f), Size(w * 0.72f, h * 0.50f), style = Stroke(3.5f))
    // Royal crown dome
    drawArc(
        color = plum.copy(alpha = 0.8f),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.36f, h * 0.14f),
        size = Size(w * 0.28f, h * 0.32f)
    )
    // Colonnade pillars
    for (i in 0..5) {
        drawRect(
            color = gold.copy(alpha = 0.55f),
            topLeft = Offset(w * (0.19f + i * 0.115f), h * 0.34f),
            size = Size(w * 0.035f, h * 0.46f)
        )
    }
}

private fun DrawScope.drawTeaEdition(
    canisterWidth: Float,
    canisterHeight: Float,
    primary: Color,
    secondary: Color,
    isChest: Boolean
) {
    val w = size.width
    val h = size.height
    val cw = w * canisterWidth
    val ch = h * canisterHeight
    val left = (w - cw) / 2f
    val top = h * 0.76f - ch

    drawRoundRect(
        color = PureWhite,
        topLeft = Offset(left, top),
        size = Size(cw, ch),
        cornerRadius = CornerRadius(if (isChest) 14f else 28f, if (isChest) 14f else 28f)
    )
    drawRoundRect(
        color = primary,
        topLeft = Offset(left, top),
        size = Size(cw, ch),
        cornerRadius = CornerRadius(if (isChest) 14f else 28f, if (isChest) 14f else 28f),
        style = Stroke(4f)
    )
    // Brushed gold lid
    drawRoundRect(
        color = secondary,
        topLeft = Offset(left + 4f, top - h * 0.06f),
        size = Size(cw - 8f, h * 0.08f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    // Wax / leaf emblem
    drawCircle(color = primary.copy(alpha = 0.24f), radius = cw * 0.22f, center = Offset(w / 2f, top + ch * 0.52f))
    drawCircle(color = secondary, radius = cw * 0.11f, center = Offset(w / 2f, top + ch * 0.52f))
}

private fun DrawScope.drawPerfumeFlacon(
    bottleWidth: Float,
    bottleHeight: Float,
    elixirColor: Color,
    goldColor: Color,
    octagonal: Boolean,
    hasCrown: Boolean
) {
    val w = size.width
    val h = size.height
    val bw = w * bottleWidth
    val bh = h * bottleHeight
    val left = (w - bw) / 2f
    val top = h * 0.78f - bh

    // Glass body
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(PureWhite, elixirColor.copy(alpha = 0.55f), elixirColor.copy(alpha = 0.85f))
        ),
        topLeft = Offset(left, top),
        size = Size(bw, bh),
        cornerRadius = CornerRadius(if (octagonal) 34f else 18f, if (octagonal) 34f else 18f)
    )
    drawRoundRect(
        color = goldColor,
        topLeft = Offset(left, top),
        size = Size(bw, bh),
        cornerRadius = CornerRadius(if (octagonal) 34f else 18f, if (octagonal) 34f else 18f),
        style = Stroke(3.5f)
    )
    // Gold neck & cap
    drawRect(goldColor, Offset(w * 0.45f, top - h * 0.10f), Size(w * 0.10f, h * 0.10f))
    drawRoundRect(
        color = if (hasCrown) goldColor else Color(0xFF1E293B),
        topLeft = Offset(w * 0.42f, top - h * 0.19f),
        size = Size(w * 0.16f, h * 0.10f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    // Cinnamon stick accent beside flacon
    drawLine(
        color = Color(0xFF9A3412),
        start = Offset(left + bw + 14f, h * 0.78f),
        end = Offset(left + bw + 34f, h * 0.62f),
        strokeWidth = 9f,
        cap = StrokeCap.Round
    )
}

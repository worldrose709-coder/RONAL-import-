package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class RonalExtraColors(
    val accent: Color = ChampagneGold,
    val glow: Color = NeonCyanGlow,
    val profileTint: Color = RoyalSapphire,
    val glassSurface: Color = GlassWhite,
    val subtleBorder: Color = HairlineSilver
)

val LocalRonalColors = staticCompositionLocalOf { RonalExtraColors() }

val RonalShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun RonalImportsTheme(
    accentColor: Color = ChampagneGold,
    glowColor: Color = NeonCyanGlow,
    profileColor: Color = RoyalSapphire,
    fontOption: AppFontOption = AppFontOption.PLAYFAIR,
    content: @Composable () -> Unit
) {
    // Theme is strictly crisp luxury White as requested, with customizable accent & glow
    val colorScheme = lightColorScheme(
        primary = accentColor,
        onPrimary = PureWhite,
        primaryContainer = accentColor.copy(alpha = 0.14f),
        onPrimaryContainer = InkObsidian,
        secondary = glowColor,
        onSecondary = PureWhite,
        secondaryContainer = glowColor.copy(alpha = 0.14f),
        onSecondaryContainer = InkObsidian,
        tertiary = profileColor,
        onTertiary = PureWhite,
        background = PureWhite,
        onBackground = InkObsidian,
        surface = AlabasterWhite,
        onSurface = InkObsidian,
        surfaceVariant = PearlSurface,
        onSurfaceVariant = SlateCharcoal,
        surfaceContainerLowest = PureWhite,
        surfaceContainerLow = AlabasterWhite,
        surfaceContainer = PearlSurface,
        surfaceContainerHigh = SoftMarble,
        outline = HairlineSilver,
        outlineVariant = HairlineSilver.copy(alpha = 0.6f),
        error = ErrorCrimson,
        onError = PureWhite
    )

    CompositionLocalProvider(
        LocalRonalColors provides RonalExtraColors(
            accent = accentColor,
            glow = glowColor,
            profileTint = profileColor
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = buildRonalTypography(fontOption),
            shapes = RonalShapes,
            content = content
        )
    }
}

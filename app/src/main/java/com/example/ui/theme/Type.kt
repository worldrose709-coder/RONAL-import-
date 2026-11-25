package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlayfairDisplayFont = FontFamily(Font(R.font.playfair_display, FontWeight.Normal))
val PlusJakartaSansFont = FontFamily(Font(R.font.plus_jakarta_sans, FontWeight.Normal))
val SpaceGroteskFont = FontFamily(Font(R.font.space_grotesk, FontWeight.Normal))
val CinzelFont = FontFamily(Font(R.font.cinzel, FontWeight.Normal))
val JetBrainsMonoFont = FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))

enum class AppFontOption(
    val id: String,
    val displayName: String,
    val tagline: String,
    val displayFamily: FontFamily,
    val bodyFamily: FontFamily
) {
    PLAYFAIR("playfair", "Playfair Atelier", "Editorial Luxury Serif", PlayfairDisplayFont, PlusJakartaSansFont),
    JAKARTA("jakarta", "Jakarta Modern", "Crisp Boutique Sans", PlusJakartaSansFont, PlusJakartaSansFont),
    SPACE("space", "Space Futura", "Geometric Supercar", SpaceGroteskFont, PlusJakartaSansFont),
    CINZEL("cinzel", "Cinzel Royal", "Sovereign Classical", CinzelFont, PlusJakartaSansFont),
    MONO("mono", "Atelier Mono", "Precision Concierge", JetBrainsMonoFont, JetBrainsMonoFont);

    companion object {
        fun fromId(id: String): AppFontOption = entries.firstOrNull { it.id == id } ?: PLAYFAIR
    }
}

fun buildRonalTypography(fontOption: AppFontOption): Typography {
    val display = fontOption.displayFamily
    val body = fontOption.bodyFamily
    return Typography(
        displayLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            letterSpacing = (-0.5).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        titleMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.sp
        ),
        labelLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.25.sp
        ),
        labelSmall = TextStyle(
            fontFamily = JetBrainsMonoFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp
        )
    )
}

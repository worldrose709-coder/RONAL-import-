package com.example.data.ai

import android.util.Log
import com.example.data.model.RonalCatalog
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiThemeRepository"

data class AiThemeResult(
    val themeName: String,
    val accentHex: String,
    val glowHex: String,
    val fontId: String,
    val wallpaperId: String,
    val tagline: String,
    val isOfflineFallback: Boolean = false
)

sealed interface AiThemeUiState {
    data object Idle : AiThemeUiState
    data object Loading : AiThemeUiState
    data class Success(val result: AiThemeResult) : AiThemeUiState
    data class Error(val message: String) : AiThemeUiState
}

class AiThemeRepository {

    private val themeSchema = Schema.obj(
        properties = mapOf(
            "themeName" to Schema.string(description = "Luxury theme name, 2-4 words"),
            "accentHex" to Schema.string(description = "Rich accent color hex like #C59B27 suitable for a crisp white luxury background"),
            "glowHex" to Schema.string(description = "Luminous neon highlight hex like #06B6D4"),
            "fontId" to Schema.enumeration(
                values = listOf("playfair", "jakarta", "space", "cinzel", "mono"),
                description = "Typography family ID"
            ),
            "wallpaperId" to Schema.enumeration(
                values = listOf("pearl_silk", "aurora_prism", "starlight_dust", "royal_grid"),
                description = "Animated white background style ID"
            ),
            "tagline" to Schema.string(description = "Short luxury tagline under 8 words")
        )
    )

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = themeSchema
            }
        )
    }

    suspend fun designTheme(moodPrompt: String, offlineMode: Boolean): AiThemeResult {
        if (offlineMode) {
            return offlineCuratedTheme(moodPrompt)
        }

        val prompt = """
            You are the AI Theme Designer for Ronal Imports (TRJ UK Luxury Concierge in Mayfair, London).
            The app uses a crisp white luxury porcelain base (#FFFFFF).
            Create an inspired accent and neon glow pairing, font choice, and animated white wallpaper style for this client request:
            "$moodPrompt"
            Ensure accentHex and glowHex are valid 6-digit #RRGGBB hex strings with strong contrast against white.
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }
            val rawJson = response.text
                ?: throw IllegalStateException("AI Theme Designer returned an empty response.")
            val json = JSONObject(rawJson)
            AiThemeResult(
                themeName = json.getString("themeName"),
                accentHex = normalizeHex(json.getString("accentHex"), "#C59B27"),
                glowHex = normalizeHex(json.getString("glowHex"), "#06B6D4"),
                fontId = json.optString("fontId", "playfair"),
                wallpaperId = json.optString("wallpaperId", "aurora_prism"),
                tagline = json.optString("tagline", "Bespoke Mayfair White Atelier"),
                isOfflineFallback = false
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI Theme Designer timed out", e)
            throw IllegalStateException("AI Theme Designer timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI Theme Designer generation failed", e)
            throw IllegalStateException(e.localizedMessage ?: "Unable to reach AI Theme Designer.")
        }
    }

    private fun offlineCuratedTheme(moodPrompt: String): AiThemeResult {
        val lower = moodPrompt.lowercase()
        val match = when {
            lower.contains("tea") || lower.contains("emerald") || lower.contains("cotswold") ->
                RonalCatalog.themeSwatches.first { it.family == "British Emerald" }
            lower.contains("cinnamon") || lower.contains("amber") || lower.contains("spice") ->
                RonalCatalog.themeSwatches.first { it.family == "Ceylon Amber" }
            lower.contains("neon") || lower.contains("cyber") || lower.contains("540") ->
                RonalCatalog.themeSwatches.first { it.family == "Neon Cyan" }
            lower.contains("rose") || lower.contains("perfume") ->
                RonalCatalog.themeSwatches.first { it.family == "Rose Quartz" }
            lower.contains("sapphire") || lower.contains("london") || lower.contains("royal") ->
                RonalCatalog.themeSwatches.first { it.family == "Royal Sapphire" }
            else -> RonalCatalog.themeSwatches.first { it.family == "Champagne Gold" }
        }
        return AiThemeResult(
            themeName = "${match.family} Offline Atelier",
            accentHex = colorToHex(match.accentColor),
            glowHex = colorToHex(match.glowColor),
            fontId = if (lower.contains("sport") || lower.contains("car")) "space" else "playfair",
            wallpaperId = "starlight_dust",
            tagline = "Offline Curated White Luxury Edition",
            isOfflineFallback = true
        )
    }

    private fun normalizeHex(raw: String, fallback: String): String {
        val cleaned = raw.trim().let { if (it.startsWith("#")) it else "#$it" }
        return if (Regex("^#[0-9A-Fa-f]{6}$").matches(cleaned)) cleaned.uppercase() else fallback
    }

    private fun colorToHex(color: androidx.compose.ui.graphics.Color): String {
        val r = (color.red * 255).toInt().coerceIn(0, 255)
        val g = (color.green * 255).toInt().coerceIn(0, 255)
        val b = (color.blue * 255).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", r, g, b)
    }
}

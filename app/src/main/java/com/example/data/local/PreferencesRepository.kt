package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.AmbientTrack
import com.example.core.WhatsAppService
import com.example.data.model.ProfileFrameStyle
import com.example.data.model.WallpaperStyle
import com.example.ui.theme.AppFontOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ronal_vip_prefs")

data class UserPreferencesState(
    val accentColorArgb: Long = 0xFFC59B27L,
    val glowColorArgb: Long = 0xFF06B6D4L,
    val profileColorArgb: Long = 0xFF1D4ED8L,
    val fontId: String = AppFontOption.PLAYFAIR.id,
    val wallpaperId: String = WallpaperStyle.PEARL_SILK.id,
    val customBgUri: String = "",
    val customWelcomeMessage: String = "Welcome back to the Mayfair Private Circle",
    val memberName: String = "Ronal VIP Guest",
    val profileFrameId: String = ProfileFrameStyle.GOLDEN_HALO.id,
    val profileTrackId: String = AmbientTrack.MAYFAIR_DAWN.id,
    val soundVolume: Float = 0.45f,
    val isSoundMuted: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val isOfflineMode: Boolean = false,
    val whatsappNumber: String = WhatsAppService.DEFAULT_WHATSAPP_LOCAL,
    val whatsappAutoApiEnabled: Boolean = false,
    val whatsappPhoneNumberId: String = "",
    val whatsappAccessToken: String = "",
    val vipPoints: Int = 650,
    val loginStreak: Int = 3,
    val lastRewardDate: String = "",
    val unlockedBadgesCsv: String = "Verified UK Member,Mayfair Early Access",
    val claimedGiftsCsv: String = "",
    val showClockWidget: Boolean = true,
    val showWeatherWidget: Boolean = true,
    val showVisitorWidget: Boolean = true,
    val showQuoteWidget: Boolean = true,
    val aiTagline: String = "Alabaster Porcelain & Champagne Gold Atelier"
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val ACCENT_ARGB = stringPreferencesKey("accent_argb")
        val GLOW_ARGB = stringPreferencesKey("glow_argb")
        val PROFILE_ARGB = stringPreferencesKey("profile_argb")
        val FONT_ID = stringPreferencesKey("font_id")
        val WALLPAPER_ID = stringPreferencesKey("wallpaper_id")
        val CUSTOM_BG_URI = stringPreferencesKey("custom_bg_uri")
        val WELCOME_MSG = stringPreferencesKey("welcome_msg")
        val MEMBER_NAME = stringPreferencesKey("member_name")
        val PROFILE_FRAME_ID = stringPreferencesKey("profile_frame_id")
        val PROFILE_TRACK_ID = stringPreferencesKey("profile_track_id")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val SOUND_MUTED = booleanPreferencesKey("sound_muted")
        val MUSIC_PLAYING = booleanPreferencesKey("music_playing")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        val WHATSAPP_NUMBER = stringPreferencesKey("whatsapp_number")
        val WHATSAPP_AUTO_API = booleanPreferencesKey("whatsapp_auto_api")
        val WHATSAPP_PHONE_ID = stringPreferencesKey("whatsapp_phone_id")
        val WHATSAPP_TOKEN = stringPreferencesKey("whatsapp_token")
        val VIP_POINTS = intPreferencesKey("vip_points")
        val LOGIN_STREAK = intPreferencesKey("login_streak")
        val LAST_REWARD_DATE = stringPreferencesKey("last_reward_date")
        val BADGES_CSV = stringPreferencesKey("badges_csv")
        val GIFTS_CSV = stringPreferencesKey("gifts_csv")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val SHOW_VISITOR = booleanPreferencesKey("show_visitor")
        val SHOW_QUOTE = booleanPreferencesKey("show_quote")
        val AI_TAGLINE = stringPreferencesKey("ai_tagline")
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.dataStore.data.map { prefs ->
        UserPreferencesState(
            accentColorArgb = prefs[Keys.ACCENT_ARGB]?.toLongOrNull() ?: 0xFFC59B27L,
            glowColorArgb = prefs[Keys.GLOW_ARGB]?.toLongOrNull() ?: 0xFF06B6D4L,
            profileColorArgb = prefs[Keys.PROFILE_ARGB]?.toLongOrNull() ?: 0xFF1D4ED8L,
            fontId = prefs[Keys.FONT_ID] ?: AppFontOption.PLAYFAIR.id,
            wallpaperId = prefs[Keys.WALLPAPER_ID] ?: WallpaperStyle.PEARL_SILK.id,
            customBgUri = prefs[Keys.CUSTOM_BG_URI] ?: "",
            customWelcomeMessage = prefs[Keys.WELCOME_MSG]
                ?: "Welcome back to the Mayfair Private Circle",
            memberName = prefs[Keys.MEMBER_NAME] ?: "Ronal VIP Guest",
            profileFrameId = prefs[Keys.PROFILE_FRAME_ID] ?: ProfileFrameStyle.GOLDEN_HALO.id,
            profileTrackId = prefs[Keys.PROFILE_TRACK_ID] ?: AmbientTrack.MAYFAIR_DAWN.id,
            soundVolume = prefs[Keys.SOUND_VOLUME] ?: 0.45f,
            isSoundMuted = prefs[Keys.SOUND_MUTED] ?: false,
            isMusicPlaying = prefs[Keys.MUSIC_PLAYING] ?: false,
            isOfflineMode = prefs[Keys.OFFLINE_MODE] ?: false,
            whatsappNumber = prefs[Keys.WHATSAPP_NUMBER] ?: WhatsAppService.DEFAULT_WHATSAPP_LOCAL,
            whatsappAutoApiEnabled = prefs[Keys.WHATSAPP_AUTO_API] ?: false,
            whatsappPhoneNumberId = prefs[Keys.WHATSAPP_PHONE_ID] ?: "",
            whatsappAccessToken = prefs[Keys.WHATSAPP_TOKEN] ?: "",
            vipPoints = prefs[Keys.VIP_POINTS] ?: 650,
            loginStreak = prefs[Keys.LOGIN_STREAK] ?: 3,
            lastRewardDate = prefs[Keys.LAST_REWARD_DATE] ?: "",
            unlockedBadgesCsv = prefs[Keys.BADGES_CSV] ?: "Verified UK Member,Mayfair Early Access",
            claimedGiftsCsv = prefs[Keys.GIFTS_CSV] ?: "",
            showClockWidget = prefs[Keys.SHOW_CLOCK] ?: true,
            showWeatherWidget = prefs[Keys.SHOW_WEATHER] ?: true,
            showVisitorWidget = prefs[Keys.SHOW_VISITOR] ?: true,
            showQuoteWidget = prefs[Keys.SHOW_QUOTE] ?: true,
            aiTagline = prefs[Keys.AI_TAGLINE] ?: "Alabaster Porcelain & Champagne Gold Atelier"
        )
    }

    suspend fun setThemeColors(accentArgb: Long, glowArgb: Long) {
        context.dataStore.edit {
            it[Keys.ACCENT_ARGB] = accentArgb.toString()
            it[Keys.GLOW_ARGB] = glowArgb.toString()
        }
    }

    suspend fun setProfileColor(argb: Long) {
        context.dataStore.edit { it[Keys.PROFILE_ARGB] = argb.toString() }
    }

    suspend fun setFont(fontId: String) {
        context.dataStore.edit { it[Keys.FONT_ID] = fontId }
    }

    suspend fun setWallpaper(wallpaperId: String) {
        context.dataStore.edit { it[Keys.WALLPAPER_ID] = wallpaperId }
    }

    suspend fun setCustomBgUri(uri: String) {
        context.dataStore.edit {
            it[Keys.CUSTOM_BG_URI] = uri
            if (uri.isNotBlank()) it[Keys.WALLPAPER_ID] = WallpaperStyle.CUSTOM_PHOTO.id
        }
    }

    suspend fun setProfileCustomization(
        memberName: String,
        welcomeMessage: String,
        frameId: String
    ) {
        context.dataStore.edit {
            it[Keys.MEMBER_NAME] = memberName
            it[Keys.WELCOME_MSG] = welcomeMessage
            it[Keys.PROFILE_FRAME_ID] = frameId
        }
    }

    suspend fun setAudioState(trackId: String, volume: Float, muted: Boolean, playing: Boolean) {
        context.dataStore.edit {
            it[Keys.PROFILE_TRACK_ID] = trackId
            it[Keys.SOUND_VOLUME] = volume
            it[Keys.SOUND_MUTED] = muted
            it[Keys.MUSIC_PLAYING] = playing
        }
    }

    suspend fun setOfflineMode(offline: Boolean) {
        context.dataStore.edit { it[Keys.OFFLINE_MODE] = offline }
    }

    suspend fun setWhatsAppConfig(
        number: String,
        autoApi: Boolean,
        phoneNumberId: String,
        token: String
    ) {
        context.dataStore.edit {
            it[Keys.WHATSAPP_NUMBER] = number.ifBlank { WhatsAppService.DEFAULT_WHATSAPP_LOCAL }
            it[Keys.WHATSAPP_AUTO_API] = autoApi
            it[Keys.WHATSAPP_PHONE_ID] = phoneNumberId
            it[Keys.WHATSAPP_TOKEN] = token
        }
    }

    suspend fun addVipPointsAndBadge(pointsToAdd: Int, badgeName: String? = null) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.VIP_POINTS] ?: 650
            prefs[Keys.VIP_POINTS] = current + pointsToAdd
            if (!badgeName.isNullOrBlank()) {
                val existing = (prefs[Keys.BADGES_CSV] ?: "Verified UK Member").split(",")
                    .filter { it.isNotBlank() }.toMutableSet()
                existing.add(badgeName)
                prefs[Keys.BADGES_CSV] = existing.joinToString(",")
            }
        }
    }

    suspend fun claimDailyReward(todayIso: String, bonusPoints: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_REWARD_DATE] = todayIso
            prefs[Keys.LOGIN_STREAK] = (prefs[Keys.LOGIN_STREAK] ?: 0) + 1
            prefs[Keys.VIP_POINTS] = (prefs[Keys.VIP_POINTS] ?: 650) + bonusPoints
        }
    }

    suspend fun markGiftClaimed(giftId: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.GIFTS_CSV] ?: "").split(",").filter { it.isNotBlank() }.toMutableSet()
            current.add(giftId)
            prefs[Keys.GIFTS_CSV] = current.joinToString(",")
        }
    }

    suspend fun setWidgetVisibility(clock: Boolean, weather: Boolean, visitor: Boolean, quote: Boolean) {
        context.dataStore.edit {
            it[Keys.SHOW_CLOCK] = clock
            it[Keys.SHOW_WEATHER] = weather
            it[Keys.SHOW_VISITOR] = visitor
            it[Keys.SHOW_QUOTE] = quote
        }
    }

    suspend fun setAiTagline(tagline: String) {
        context.dataStore.edit { it[Keys.AI_TAGLINE] = tagline }
    }
}

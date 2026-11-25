package com.example.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class AmbientTrack(
    val id: String,
    val title: String,
    val mood: String,
    val baseNotesHz: List<Double>,
    val stepDurationSec: Double
) {
    MAYFAIR_DAWN(
        id = "mayfair_dawn",
        title = "Mayfair Dawn Harp",
        mood = "Warm Champagne Piano & Harp",
        baseNotesHz = listOf(261.63, 329.63, 392.00, 493.88, 523.25, 392.00, 329.63, 293.66),
        stepDurationSec = 0.65
    ),
    CEYLON_RAIN(
        id = "ceylon_rain",
        title = "Ceylon Garden Chimes",
        mood = "Soft Velvet Pad & Tea Bells",
        baseNotesHz = listOf(293.66, 369.99, 440.00, 554.37, 587.33, 440.00, 369.99, 329.63),
        stepDurationSec = 0.75
    ),
    VELVET_LOUNGE(
        id = "velvet_lounge",
        title = "TRJ Velvet Lounge",
        mood = "Late-Night Mayfair Penthouse",
        baseNotesHz = listOf(220.00, 261.63, 329.63, 392.00, 440.00, 392.00, 329.63, 246.94),
        stepDurationSec = 0.80
    );

    companion object {
        fun fromId(id: String): AmbientTrack = entries.firstOrNull { it.id == id } ?: MAYFAIR_DAWN
    }
}

class AmbientMusicEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var playbackJob: Job? = null
    private var audioTrack: AudioTrack? = null

    @Volatile
    private var currentVolume: Float = 0.45f

    @Volatile
    private var isMuted: Boolean = false

    @Volatile
    private var currentTrack: AmbientTrack = AmbientTrack.MAYFAIR_DAWN

    fun start(track: AmbientTrack, volume: Float, muted: Boolean) {
        currentTrack = track
        currentVolume = volume.coerceIn(0f, 1f)
        isMuted = muted
        if (playbackJob?.isActive == true) return

        playbackJob = scope.launch {
            val sampleRate = 22050
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            val trackPlayer = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Exception) {
                return@launch
            }

            audioTrack = trackPlayer
            try {
                trackPlayer.play()
                val chunkSamples = 1024
                val buffer = ShortArray(chunkSamples)
                var globalSample = 0L

                while (isActive) {
                    val activeTrack = currentTrack
                    val effectiveGain = if (isMuted) 0f else currentVolume * 0.22f
                    val stepSamples = (sampleRate * activeTrack.stepDurationSec).toLong().coerceAtLeast(1L)
                    val notes = activeTrack.baseNotesHz

                    for (i in 0 until chunkSamples) {
                        val s = globalSample + i
                        val noteIndex = ((s / stepSamples) % notes.size).toInt()
                        val prevIndex = if (noteIndex == 0) notes.size - 1 else noteIndex - 1
                        val localSec = (s % stepSamples).toDouble() / sampleRate
                        val env = sin((localSec / activeTrack.stepDurationSec).coerceIn(0.0, 1.0) * PI) *
                            exp(-localSec * 1.4)

                        val freq = notes[noteIndex]
                        val padFreq = notes[prevIndex] * 0.5
                        val t = s.toDouble() / sampleRate

                        val melody = sin(2.0 * PI * freq * t) + 0.35 * sin(2.0 * PI * (freq * 2.0) * t)
                        val pad = 0.45 * sin(2.0 * PI * padFreq * t)
                        val mixed = (melody * env + pad * 0.4) * effectiveGain
                        val clamped = mixed.coerceIn(-0.95, 0.95)
                        buffer[i] = (clamped * Short.MAX_VALUE).toInt().toShort()
                    }

                    globalSample += chunkSamples
                    trackPlayer.write(buffer, 0, chunkSamples)
                }
            } catch (_: Exception) {
                // Gracefully stop if audio hardware is unavailable in headless environments
            } finally {
                try {
                    trackPlayer.pause()
                    trackPlayer.flush()
                    trackPlayer.release()
                } catch (_: Exception) {
                }
                if (audioTrack === trackPlayer) {
                    audioTrack = null
                }
            }
        }
    }

    fun updateSettings(track: AmbientTrack, volume: Float, muted: Boolean) {
        currentTrack = track
        currentVolume = volume.coerceIn(0f, 1f)
        isMuted = muted
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
    }
}

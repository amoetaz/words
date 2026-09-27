package com.moetaz.words.presentation.util

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import java.util.Locale

/**
 * Base TextToSpeech helper interface/class.
 * Provides no-op implementations in preview mode where android.speech.tts.TextToSpeech
 * is unavailable in the Android Studio Layoutlib runtime environment.
 */
open class TextToSpeechHelper {
    open fun speak(text: String, locale: Locale = Locale.US) {}
    open fun stop() {}
    open fun shutdown() {}
}

/**
 * Real implementation of TextToSpeechHelper used on actual devices at runtime.
 */
private class RealTextToSpeechHelper(context: Context) : TextToSpeechHelper() {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isInitialized = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun speak(text: String, locale: Locale) {
        if (isInitialized && text.isNotBlank()) {
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_WORD_ID")
        }
    }

    override fun stop() {
        tts?.stop()
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

/**
 * Remembers a [TextToSpeechHelper] instance across recompositions.
 * In Compose Preview inspection mode (`LocalInspectionMode.current == true`),
 * returns a dummy no-op helper to prevent `NoClassDefFoundError` when Layoutlib
 * attempts to load `android.speech.tts.TextToSpeech`.
 */
@Composable
fun rememberTextToSpeech(): TextToSpeechHelper {
    if (LocalInspectionMode.current) {
        return remember { TextToSpeechHelper() }
    }
    val context = LocalContext.current
    val ttsHelper = remember(context) { RealTextToSpeechHelper(context) }
    DisposableEffect(ttsHelper) {
        onDispose {
            ttsHelper.shutdown()
        }
    }
    return ttsHelper
}



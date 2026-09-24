package com.moetaz.words.presentation.util

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class TextToSpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isInitialized = true
            }
        }
    }

    fun speak(text: String, locale: Locale = Locale.US) {
        if (isInitialized && text.isNotBlank()) {
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_WORD_ID")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

@Composable
fun rememberTextToSpeech(): TextToSpeechHelper {
    val context = LocalContext.current
    val ttsHelper = remember(context) { TextToSpeechHelper(context) }
    DisposableEffect(ttsHelper) {
        onDispose {
            ttsHelper.shutdown()
        }
    }
    return ttsHelper
}

package com.pathvision.app.ar

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceGuidanceManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun speak(instruction: String) {
        if (isInitialized) {
            tts?.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, "PathVisionNavID")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
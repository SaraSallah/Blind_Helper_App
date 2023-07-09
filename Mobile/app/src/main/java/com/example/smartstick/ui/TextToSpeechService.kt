package com.example.smartstick.ui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TextToSpeechService : Service(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private var spokenText: String? = null

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                // Speech synthesis started
            }

            override fun onDone(utteranceId: String) {
                // Speech synthesis completed
                stopSelf() // Stop the service when speech synthesis is done
            }

            override fun onError(utteranceId: String) {
                // Speech synthesis encountered an error
                stopSelf() // Stop the service on error
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let { extras ->
            if (extras.containsKey("text")) {
                spokenText = extras.getString("text")
                spokenText?.let {
                    Log.d("Sara", "Speaking text: $it")

                    speakText(it)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun speakText(text: String) {
        Log.d("Sara", "Speaking text: $text")
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        Log.d("Sara", "Speaking text 2 : $text")

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            if (textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                textToSpeech.language = locale
            } else {
                Log.e("Sara", "Language not available: ${locale.language}")
            }
        } else {
            Log.e("Sara", "TextToSpeech initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        fun newIntent(context: Context, text: String): Intent {
            val intent = Intent(context, TextToSpeechService::class.java)
            intent.putExtra("text", text)
            return intent
        }
    }
}


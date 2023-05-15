package com.example.smartstick.ui.home
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class VoiceRecognitionManager(private val activity: Activity, private val listener: RecognitionListener) {

    private lateinit var speechRecognizer: SpeechRecognizer

    init {

        // Permission has already been granted, initialize SpeechRecognizer
        initSpeechRecognizer()
//        // Check if we have RECORD_AUDIO permission, request it if not
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
             != PackageManager.PERMISSION_GRANTED){
               ActivityCompat.requestPermissions(
                   activity,
                   arrayOf(Manifest.permission.RECORD_AUDIO),
                   REQUEST_RECORD_AUDIO_PERMISSION_CODE
               )
        }
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        speechRecognizer.setRecognitionListener(MyRecognitionListener())
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }

    private inner class MyRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            listener.onReadyForSpeech(params)
        }

        override fun onBeginningOfSpeech() {
            listener.onBeginningOfSpeech()
        }

        override fun onRmsChanged(rmsdB: Float) {
            listener.onRmsChanged(rmsdB)
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            listener.onBufferReceived(buffer)
        }

        override fun onEndOfSpeech() {
            listener.onEndOfSpeech()
        }

        override fun onError(error: Int) {
            listener.onError(error)
        }

        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
            if (text != null) {
                // Perform some action based on the recognized text
                if (text == "hello") {
                    // Do something
                }
            }
            listener.onResults(results)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            listener.onPartialResults(partialResults)
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            listener.onEvent(eventType, params)
        }
    }

    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1
    }
}
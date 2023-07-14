package com.example.connection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.smartstick.MainActivity
import com.example.smartstick.R
import com.example.smartstick.ui.TextToSpeechService

class MyBackgroundService : Service(), SocketListener {
    private lateinit var socketManager: SocketManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        socketManager = SocketManager(applicationContext, this)
        socketManager.connect()

        if (socketManager.isConnected()) {
            Log.e("Sara", "Socket is connected.")
        } else {
            Log.e("Sara", "Socket is not connected.")
        }

        // Start the service in the foreground
        startForegroundService()
    }

    override fun onDestroy() {
        super.onDestroy()

        socketManager.disconnect()
    }

    override fun onMessageReceived(message: String) {
        // Convert message to speech
        val textToSpeechIntent = Intent(applicationContext, TextToSpeechService::class.java)
        textToSpeechIntent.putExtra("text", message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(textToSpeechIntent)
        } else {
            startService(textToSpeechIntent)
        }

        Log.d("Sara", "Received message: $message")
    }

    private fun startForegroundService() {
        val channelId = "ForegroundServiceChannel"
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationWithChannel(channelId, pendingIntent)
        } else {
            createNotificationWithoutChannel(pendingIntent)
        }

        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationWithChannel(
        channelId: String,
        pendingIntent: PendingIntent,
    ): Notification {
        val channel = NotificationChannel(
            channelId,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.call)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationWithoutChannel(pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(this)
            .setContentTitle("Foreground Service")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.call)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        private const val FOREGROUND_SERVICE_ID = 1
    }
}
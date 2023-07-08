package com.example.smartstick.ui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyBackgroundService: Service(), SocketListener {
    private lateinit var socketManager: SocketManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        socketManager = SocketManager(applicationContext, this)
        socketManager.connect()
    }

    override fun onDestroy() {
        super.onDestroy()

        socketManager.disconnect()
    }

    override fun onMessageReceived(message: String) {
        Log.d("Rehab", "Received message: $message")
    }
}
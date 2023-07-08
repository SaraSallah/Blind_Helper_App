package com.example.smartstick.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.smartstick.MainActivity
import com.example.smartstick.R

class MyBackgroundService: Service(), SocketListener {
    private lateinit var socketManager: SocketManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.e("Sara" ,"hhhhh")
        socketManager = SocketManager(applicationContext, this)
        socketManager.connect()
//        startForegroundServiceCompat()

        Log.e("Sara" ,"ay haga ")
        if (socketManager.isConnected()) {
            Log.e("Sara", "Socket is connected.")
        } else {
            Log.e("Sara", "Socket is not connected.")
        }

    }
//    private fun startForegroundServiceCompat() {
//        val channelId =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                createNotificationChannel("my_service", "My Background Service")
//            } else {
//                ""
//            }
//
//        val notification = createNotification(channelId) // Create a notification object
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForeground(FOREGROUND_SERVICE_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
//        } else {
//            startForeground(FOREGROUND_SERVICE_ID, notification)
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(channelId: String, channelName: String): String {
//        val channel = NotificationChannel(
//            channelId,
//            channelName,
//            NotificationManager.IMPORTANCE_LOW
//        )
//        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.createNotificationChannel(channel)
//
//        return channelId
//    }
//
//    private fun createNotification(channelId: String): Notification {
//        val notificationTitle = "My Background Service"
//        val notificationText = "Service is running"
//        val notificationIcon = R.drawable.call // Replace with your notification icon resource
//
//        // Create a PendingIntent for when the user taps on the notification
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // Build the notification using NotificationCompat.Builder
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setContentTitle(notificationTitle)
//            .setContentText(notificationText)
//            .setSmallIcon(notificationIcon)
//            .setContentIntent(pendingIntent)
//            .setOngoing(true) // Make the notification ongoing (persistent)
//
//        // Return the built notification
//        return notificationBuilder.build()
//        // Build your notification here
//        // Customize the content, title, icon, etc. as per your requirements
//    }



    override fun onDestroy() {
        super.onDestroy()

        socketManager.disconnect()
    }

    override fun onMessageReceived(message: String) {
        //text to Speach
        Log.d("Sara", "Received message: $message")
    }
//    companion object {
//        private const val FOREGROUND_SERVICE_ID = 1
//    }
}
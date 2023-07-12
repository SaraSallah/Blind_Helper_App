package com.example.smartstick.ui

import android.content.Context
import android.os.Looper
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.util.logging.Handler

class SocketManager(private val context: Context, private val listener: SocketListener) {
    lateinit var socket: Socket
    private var connected: Boolean = false

    fun connect() {
        try {
            val options = IO.Options()
            options.forceNew = true
            socket = IO.socket("https://lingtr.onrender.com/", options)
            socket.connect()
            listenForMessages()
            setupEventHandlers()
        } catch (e: Exception) {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                connect() }, 2000)
            e.printStackTrace()
            Log.e("Sara", e.toString())
        }
    }

    fun isConnected(): Boolean {
        return connected
    }

   fun sendText(message: String) {
        if (connected) {
            socket.emit("message", message)
            Log.e("Sara", "Text sent: $message")
        } else {
            Log.e("Sara", "Cannot send text. Not connected to the server.")
        }
    }

    private fun listenForMessages() {
        socket.on("message") { args ->
            val message = args[0].toString()
            val strings = message.trim().split(" ")
            if ((strings.size ?: 0) >= 3
                && strings.getOrNull(0) == "ObjectsDetected:"
            ) {
                val data = strings.subList(1, strings.size).joinToString(" ")
                listener.onMessageReceived(message)
                val textToSpeechIntent = TextToSpeechService.newIntent(context, data)
                context.startService(textToSpeechIntent)
                Log.e("Sara", "message 1 : $message")
            }
        }
    }

    private fun setupEventHandlers() {
        socket.on(Socket.EVENT_CONNECT) {
            connected = true
            println("Connected to the server.")
            Log.e("Sara", "Connected to the server.")
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            connected = false
            handleDisconnection()
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            connected = false
            val error = args[0] as Exception
            handleConnectionError(error)
        }
    }

    private fun handleDisconnection() {
        println("Disconnected from the server.")
        Log.e("Sara", "Disconnected from the server.")
    }

    private fun handleConnectionError(error: Exception) {
        println("Connection error: ${error.message}")
        Log.e("Sara", "Connection error: ${error.message}")
    }

    fun disconnect() {
        socket.disconnect()
    }
}
package com.example.smartstick.ui

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

class SocketManager (private val context: Context, private val listener: SocketListener) {
    private lateinit var socket: Socket
    private var connected: Boolean = false

    fun connect() {
        try {
            val options = IO.Options()
            options.forceNew = true
            socket = IO.socket("https://lingtr.onrender.com/", options) // Replace with your server URL
            socket.connect()
            listenForMessages()
            setupEventHandlers()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Sara", e.toString())

        }
    }
    fun isConnected(): Boolean {
        return connected
    }

    private fun listenForMessages() {
        socket.on("message") { args ->
            val message = args[0].toString()
            listener.onMessageReceived(message)
            val textToSpeechIntent = TextToSpeechService.newIntent(context, message)
            context.startService(textToSpeechIntent)

            Log.e("Sara", "message 1 : $message")
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
package com.example.smartstick.connection

interface SocketListener {
    fun onMessageReceived(message: String)

}
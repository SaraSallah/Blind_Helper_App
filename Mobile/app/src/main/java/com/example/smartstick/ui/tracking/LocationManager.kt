package com.example.smartstick.ui.tracking

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LocationManager : Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var database: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().reference
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Permission not granted")
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("users").child(userId).child("location").setValue("$latitude, $longitude")
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "LocationService"
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 // 1 minute
    }
}

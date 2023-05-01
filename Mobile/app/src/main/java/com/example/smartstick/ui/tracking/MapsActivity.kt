package com.example.smartstick.ui.tracking

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstick.R
import com.example.smartstick.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val holderID = intent.getStringExtra("holderID") ?: ""
        initFirebase()
        initMap()
        getUserLocation(holderID)
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            mMap = map
        }
    }

    private fun getUserLocation(userId: String) {
        database.child("users").child(userId).child("location")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val locationString = dataSnapshot.value as String?
                    val locationArray = locationString?.split(", ")
                    val latitude = locationArray?.get(0)?.toDoubleOrNull()
                    val longitude = locationArray?.get(1)?.toDoubleOrNull()
                    val time = if ((locationArray?.size ?: 0) >= 3) locationArray?.get(2)?.toLongOrNull() else null


                    if (latitude != null && longitude != null && time != null ) {
                        val currentDate = Date(time)
                        val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                        val formattedDate = formatter.format(currentDate)
                        Log.e("Map",formattedDate.toString())
                        showFriendLocationOnMap(latitude, longitude)

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Error getting location data", databaseError.toException())
                }
            })
    }

     fun showFriendLocationOnMap(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions().position(latLng)
        BitmapDescriptorFactory.fromResource(R.drawable.person_marker)
        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

}
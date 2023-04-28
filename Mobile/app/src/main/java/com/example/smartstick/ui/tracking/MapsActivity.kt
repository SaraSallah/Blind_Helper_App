package com.example.smartstick.ui.tracking

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var database: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebase()
        initMap()
        initFriendLocationListener()
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

    private fun initFriendLocationListener() {
        val userId = mUser.uid
        database.child("Friends").child(userId).
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val holderID = snapshot.child("HolderID").getValue().toString()
                    getUserLocation(holderID)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("You don't have any holder")
            }
        })
    }

     fun getUserLocation(userId: String) {
        database.child("users").child(userId).child("location")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val locationString = dataSnapshot.value as String?
                    val locationArray = locationString?.split(", ")
                    val latitude = locationArray?.get(0)?.toDoubleOrNull()
                    val longitude = locationArray?.get(1)?.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
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

    private fun showToast(message: String) {
        Toast.makeText(this@MapsActivity, message, Toast.LENGTH_LONG).show()
    }
}
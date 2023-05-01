package com.example.smartstick.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.smartstick.MainActivity
import com.example.smartstick.data.User
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHomeBinding
import com.example.smartstick.ui.search.SearchAdapter
import com.example.smartstick.ui.tracking.LocationManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val TAG: String =this::class.simpleName.toString()
    private lateinit var adapter: FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>
    private lateinit var mUserRef: DatabaseReference
    private lateinit var options: FirebaseRecyclerOptions<User>

    override fun getViewBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)


    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        startLocationService()


    }






    private fun startLocationService() {
        if (isLocationPermissionGranted()) {
            val intent = Intent(requireContext(), LocationManager::class.java)
            requireContext().startService(intent)
        } else {
            requestLocationPermissions()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission required to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized)
            adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (::adapter.isInitialized)
            adapter.stopListening()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
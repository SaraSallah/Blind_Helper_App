package com.example.smartstick.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHolderBinding
import com.example.smartstick.ui.tracking.LocationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HolderFragment : BaseFragment<FragmentHolderBinding>(){
    override val TAG: String = this::class.simpleName.toString()

    override fun getViewBinding(): FragmentHolderBinding =
        FragmentHolderBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        startLocationService()
    }

    private fun makeCall(view: View){
        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val number =snapshot.child("Relative_Number").value.toString()
                    val intent = Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", number, null))
                    view.context.startActivity(intent)
                }
                override fun onCancelled(error: DatabaseError) {}
            })}

    private fun getCurrentDateAndTimeInArabic(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale("ar"))
        val date = dateFormat.format(calendar.time)
        val time = timeFormat.format(calendar.time)
        return "التاريخ: $date\nالوقت: $time"
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

    @Deprecated("Deprecated in Java")
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

}
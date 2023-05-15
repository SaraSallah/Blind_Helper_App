package com.example.smartstick.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
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

class HolderFragment : BaseFragment<FragmentHolderBinding>(), RecognitionListener {
    private lateinit var voiceRecognitionManager: VoiceRecognitionManager

    override val TAG: String = this::class.simpleName.toString()

    override fun getViewBinding(): FragmentHolderBinding =
        FragmentHolderBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        startLocationService()
        voiceRecognitionManager = VoiceRecognitionManager(requireActivity(), this)
        binding.start.setOnClickListener {
            voiceRecognitionManager.startListening()
        }
       getCurrentDateAndTime()
    }

    override fun onResults(results: Bundle?) {
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
        if (text != null) {
//            // Update the resultTextView with the recognized
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            // Check if the recognized text contains a destination
            val destinationRegx = Regex("(navigate to | go to ) (.+)")
            val matchResult = destinationRegx.find(text.toLowerCase())
            if(matchResult != null) {
                val destination = matchResult.groupValues[2]
                startNavigation(destination, "walking")
            }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}


    private fun makeCall(view: View) {
        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val number = snapshot.child("Relative_Number").value.toString()
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.fromParts("tel", number, null)
                    )
                    view.context.startActivity(intent)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("makeCall", "Database error occurred: ${error.message}")
                }
            })}

    private fun startNavigation(destination: String, mode: String) {
        val directionsMode = when (mode.toLowerCase()) {
            "walking" -> "w"
            "driving" -> "d"
            else -> "d"  }
        val uri = Uri.parse("google.navigation:q=$destination&mode=$directionsMode")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    //according to the device's language...
    private fun getCurrentDateAndTime(locale: Locale = Locale.getDefault()): String{
        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", locale)
        val timeFormat = SimpleDateFormat("hh:mm:ss a" , locale)
        val date = dateFormat.format(calender.time)
        val time = timeFormat.format(calender.time)
        return "Date: $date\nTime: $time"
    }

//    private fun getCurrentDateAndTimeInArabic(): String {
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
//        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale("ar"))
//        val date = dateFormat.format(calendar.time)
//        val time = timeFormat.format(calendar.time)
//        return "التاريخ: $date\nالوقت: $time"
//    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        voiceRecognitionManager.destroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {}

    override fun onError(error: Int) {}

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
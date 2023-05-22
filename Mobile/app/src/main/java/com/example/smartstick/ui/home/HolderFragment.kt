package com.example.smartstick.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.smartstick.MainActivity
import com.example.smartstick.R
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHolderBinding
import com.example.smartstick.ui.tracking.LocationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class HolderFragment : BaseFragment<FragmentHolderBinding>(), RecognitionListener {
    private lateinit var voiceRecognitionManager: VoiceRecognitionManager
    private lateinit var textToSpeech: TextToSpeech
    private var mUserRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null

    override val TAG: String = this::class.simpleName.toString()

    override fun getViewBinding(): FragmentHolderBinding =
        FragmentHolderBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
        mUserRef = FirebaseDatabase.getInstance().getReference("users")
        startLocationService()
        voiceRecognitionManager = VoiceRecognitionManager(requireActivity(), this)
        textToSpeech = TextToSpeech(requireContext())
        { status ->
            if (status == TextToSpeech.SUCCESS) { }
        }


        addCallBacks()
    }

    private fun addCallBacks(){
        binding.startRecord.setOnClickListener {
            voiceRecognitionManager.startListening()
        }

        binding.cardDate.setOnClickListener {
            val dateAndTime = getCurrentDateAndTime()
            textToSpeech.speak(dateAndTime, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        binding.cardMakeCall.setOnClickListener {
            makeCall(requireView())
        }

        binding.cardNavigation.setOnClickListener {
            getAddressFromDatabaseAndGoToHome()
        }
    }

    private fun getAddressFromDatabaseAndGoToHome() {
        mUserRef?.child(mUser!!.uid)?.child("Address")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val locationString = dataSnapshot.value as String?
                    val locationArray = locationString?.split(", ")
                    val latitude = locationArray?.get(0)?.toDoubleOrNull()
                    val longitude = locationArray?.get(1)?.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        log("$latitude , $longitude")
                        startNavigation(latitude, longitude, "w")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    override fun onResults(results: Bundle?) {
        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
        if (text != null) {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            val strings = text.trim().split(" ")
            if ((strings.size
                    ?: 0) >= 3
                && strings.getOrNull(0) == "go"
                && strings.getOrNull(1) == "to"
            ) {
                val wordsAfterTwo = strings.subList(2, strings.size)
                for (word in wordsAfterTwo) {
                    startNavigation(word, "w")
                }
            }
            if ((strings.size
                    ?: 0) >= 3
                && strings.getOrNull(0) == "اذهب"
                && strings.getOrNull(1) == "الى"
            ) {
                val thirdString = strings.getOrNull(2)
                if (thirdString != null) {
                    startNavigation(thirdString, "w")                }
            }
            if (text.contains("اتصل", ignoreCase = true)) {
                makeCall(requireView())
            }
            if (text.contains("make call", ignoreCase = true)) {
                makeCall(requireView())
            }
            // Check if the recognized text contains a destination
            val destinationRegx = Regex(getString(R.string.navigate_to_go_to))
            val matchResult = destinationRegx.find(text.toLowerCase())
            if (matchResult != null) {
                val destination = matchResult.groupValues[2]
                startNavigation(37.7749, -122.4194, "walking")
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
                    val number = snapshot.child("Relative Number").value.toString()
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.fromParts("tel", number, null)
                    )
                    view.context.startActivity(intent)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("makeCall", "Database error occurred: ${error.message}")
                }
            })
    }

    @SuppressLint("DefaultLocale")
    private fun startNavigation(latitude: Double, longitude: Double, mode: String) {
        val directionsMode = when (mode.toLowerCase()) {
            "walking" -> "w"
            "driving" -> "d"
            else -> "d"
        }
        val uri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=$directionsMode")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun startNavigation(destination: String, mode: String) {
        val directionsMode = when (mode.toLowerCase()) {
            "walking" -> "w"
            "driving" -> "d"
            else -> "d"
        }
        val uri = Uri.parse("google.navigation:q=$destination&mode=$directionsMode")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    //according to the device's language...
    private fun getCurrentDateAndTime(locale: Locale = Locale.getDefault()): String {
        val calender = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", locale)
        val timeFormat = SimpleDateFormat("hh:mm:ss a", locale)
        val date = dateFormat.format(calender.time)
        val time = timeFormat.format(calender.time)
        return "Date: $date\nTime: $time"
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
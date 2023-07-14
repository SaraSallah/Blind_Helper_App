package com.example.smartstick.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.connection.OpenAIManager
import com.example.connection.SocketListener
import com.example.connection.SocketManager
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
import io.socket.client.Socket
import java.text.SimpleDateFormat
import java.util.*


class HolderFragment : BaseFragment<FragmentHolderBinding>(), RecognitionListener,
    SocketListener, OpenAIManager.OnOpenAIResponseListener {
    private lateinit var voiceRecognitionManager: VoiceRecognitionManager
    private lateinit var textToSpeech: TextToSpeech
    private var mUserRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null
    private var socketManager: SocketManager? = null
    private lateinit var openAIManager: OpenAIManager


    override val TAG: String = this::class.simpleName.toString()

    override fun getViewBinding(): FragmentHolderBinding =
        FragmentHolderBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        setUpAppBar(true, "Holder Service")

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
        mUserRef = FirebaseDatabase.getInstance().getReference("users")
        startLocationService()
        voiceRecognitionManager = VoiceRecognitionManager(requireActivity(), this)
        textToSpeech = TextToSpeech(requireContext())
        { status ->
            if (status == TextToSpeech.SUCCESS) {
            }
        }
        val apiKey = "YOUR_API_KEY" // Replace with your actual OpenAI API key
        openAIManager = OpenAIManager(apiKey, this)


        addCallBacks()
    }

    private fun askQuestion(question: String) {
        openAIManager.sendQuestion(question)
    }

    private fun addCallBacks() {
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
                val wordsAfterTwo = strings.subList(2, strings.size).joinToString ()
                startNavigation(wordsAfterTwo, "w")
            }
            if ((strings.size
                    ?: 0) >= 3
                && strings.getOrNull(0) == "اذهب"
                && strings.getOrNull(1) == "الى"
            ) {

                val wordsAfterTwo = strings.subList(2, strings.size)
                startNavigation(wordsAfterTwo.toString(), "w")
            }
            if ((strings.size
                    ?: 0) >= 2
                && strings.getOrNull(0) == "call"
            ) {
                val wordsAfterTwo = strings.subList(1, strings.size)
                makeCall(wordsAfterTwo.toString())
            }
            if ((strings.size
                    ?: 0) >= 3
                && strings.getOrNull(0) == "اتصل"
            ) {
                val wordsAfterTwo = strings.subList(2, strings.size)
                makeCall(wordsAfterTwo.toString())
            }

            if (text.contains("اتصل", ignoreCase = true)) {
                makeCall(requireView())
            }
            if (text.contains("make call", ignoreCase = true)) {
                makeCall(requireView())
            }
            if (text.contains("mode explore", ignoreCase = true)
                ||
                (text.contains("mod explore", ignoreCase = true))
                ||
                (text.contains("mood explore", ignoreCase = true))
            ) {
                sendMessage("explore")
            }
            if (text.contains("mode detect", ignoreCase = true)
                ||
                (text.contains("mod detect", ignoreCase = true))
                ||
                (text.contains("mood detect", ignoreCase = true))
            ) {
                sendMessage("detect")
            }
            if (text.contains("language Arabic", ignoreCase = true)
            ) {
                Log.e("Sara", "before")
                sendMessage("arabic")
                Log.e("Sara", "after")

            }
            if (text.contains("language English", ignoreCase = true)
            ) {
                sendMessage("english")
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

    private fun sendMessage(text: String) {
        socketManager = SocketManager(requireContext(), this)
        socketManager!!.connect()
        socketManager!!.socket.on(Socket.EVENT_CONNECT) {
            if (text == "explore") {
                socketManager?.sendText("ChangeMode: explore")
            } else if (text == "detect") {
                socketManager?.sendText("ChangeMode: detect")
            } else if (text == "arabic") {
                socketManager?.sendText("ChangeLanguage: arabic")
            } else if (text == "english") {
                socketManager?.sendText("ChangeLanguage: english")
            }
        }
    }


    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}


    private fun makeCall(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        FirebaseDatabase.getInstance().reference.child("users")
            .child(currentUser?.uid ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val number = snapshot.child("Relative Number").value

                    // Check if the user has granted permission to make phone calls
                    if (ContextCompat.checkSelfPermission(
                            view.context,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            view.context as Activity,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            1
                        )
                    } else {
                        // Make the phone call
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:$number")

                        view.context.startActivity(callIntent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("makeCall", "Database error occurred: ${error.message}")
                }
            })
    }

    @SuppressLint("Recycle")
    private fun makeCall(contactName: String) {
        val contactUri = Uri.withAppendedPath(
            ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
            Uri.encode(contactName)
        )
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

        val cursor = activity?.contentResolver?.query(
            contactUri,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val phoneNumber = cursor.getString(columnIndex)

                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")

                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CALL_PHONE),
                        1
                    )
                } else {
                    startActivity(callIntent)
                }
            }
        }
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

    private fun startNavigation(destination: String, mode: String) {
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

    @RequiresApi(Build.VERSION_CODES.P)
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.FOREGROUND_SERVICE
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
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
        private val PERMISSIONS_REQUEST_CODE = 1

    }

    override fun onMessageReceived(message: String) {
        TODO("Not yet implemented")
    }

    override fun onResponse(answer: String) {
        TODO("Not yet implemented")
    }

    override fun onError(errorMessage: String) {
        TODO("Not yet implemented")
    }


}
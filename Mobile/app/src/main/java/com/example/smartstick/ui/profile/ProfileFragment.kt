package com.example.smartstick.ui.profile

import android.content.ContentValues
import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    private var mUserRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null
    var relativeNumber = ""

    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
        mUserRef = FirebaseDatabase.getInstance().getReference("users")
        loadAccountData()
        addCallBacks()
    }

    private fun addCallBacks(){
        binding.btnSaveData.setOnClickListener {
            updateRelativeNumber()
            addAddressInDataBase()
        }
        binding.btnUpdateData.setOnClickListener {
            getMyHomeLocation()
        }

    }

    private fun addAddressInDataBase() {
        mUserRef!!.child(mUser!!.uid).child("location")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val locationString = dataSnapshot.value as String?
                    val locationArray = locationString?.split(", ")
                    val latitude = locationArray?.get(0)?.toDoubleOrNull()
                    val longitude = locationArray?.get(1)?.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        mUserRef!!.child(mUser!!.uid).child("Address")
                            .setValue("$latitude, $longitude")
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    log("update address")
                                } else {
                                    log("Failed!!")
                                }

                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(
                        ContentValues.TAG,
                        "Error getting location data",
                        databaseError.toException()
                    )
                }
            })


    }


    private fun loadAccountData() {
        mUserRef!!.child(mUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    relativeNumber =dataSnapshot.child("Relative Number").value.toString()
                    val email = dataSnapshot.child("email").value.toString()
                    val password = dataSnapshot.child("password").value.toString()
                    binding.textViewEmail.text = email
                    binding.editTextPassword.editText?.setText(password)
                    binding.edittextPhone.setText(relativeNumber)

                } else {
                    Toast.makeText(requireContext(), "Data Not Exist", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),databaseError.message ,Toast.LENGTH_LONG).show()

            }
        })
    }

     private fun updateRelativeNumber(){
         val num  =binding.edittextPhone.text.toString()
         mUserRef!!.child(mUser!!.uid).child("Relative Number")
             .setValue(num).addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     binding.edittextPhone.setText(num)
                     Toast.makeText(requireContext()," You add your relative number  ",Toast.LENGTH_LONG).show()

                 } else {
                     val message = task.exception?.message ?: "Unknown error"
                     Toast.makeText(requireContext(),"Error$message",Toast.LENGTH_LONG).show()
                 }
             }
     }

    private fun getMyHomeLocation() {
        mUserRef!!.child(mUser!!.uid).child("location")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val locationString = dataSnapshot.value as String?
                    val locationArray = locationString?.split(", ")
                    val latitude = locationArray?.get(0)?.toDoubleOrNull()
                    val longitude = locationArray?.get(1)?.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        val address = getAddressLocation(requireContext(), latitude, longitude)
                        binding.editTextLocation.setText(address)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(ContentValues.TAG, "Error getting location data", databaseError.toException())
                }
            })
    }

    private fun getAddressLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context)
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                return addressList[0].getAddressLine(0) ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return "City not found"
    }
}
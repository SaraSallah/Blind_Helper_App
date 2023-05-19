package com.example.smartstick.ui.home

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstick.MainActivity
import com.example.smartstick.data.User
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHomeBinding
import com.example.smartstick.ui.search.SearchAdapter
import com.example.smartstick.ui.tracking.MapsActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val TAG: String =this::class.simpleName.toString()
    private lateinit var adapter: FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>
    private lateinit var friendRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    override fun getViewBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)


    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
//        startLocationService()
        friendRef = FirebaseDatabase.getInstance().getReference("Friends")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        getAllFriendsID()
    }

    private fun getAllFriendsID() {
        friendRef.child(mUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendIDs = mutableListOf<String>()
                for (friendSnapshot in snapshot.children) {
                    friendIDs.add(friendSnapshot.key.toString())
                }
                getFriendsInformation(friendIDs)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve friends: ${error.message}")
            }
        })
    }

    private fun getFriendsInformation(friendIDs: List<String>) {
        val userRef = FirebaseDatabase.getInstance().getReference("users")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener,
            HolderAdapter.UserInteractionListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                val friends = mutableListOf<String>()
                for (friendID in friendIDs) {
                    val friendSnapshot = snapshot.child(friendID)
                    val userEmail = friendSnapshot.child("email").value.toString()
                    val userProfileImageUrl =
                        friendSnapshot.child("profileImageUrl").value.toString()

                    getUserLastLocation(friendID) { it ->
                        val user = User(
                            userEmail, "", "", userProfileImageUrl, it
                        )
                        users.add(user)
                        friends.add(friendID)
                        val adapter = HolderAdapter(this, users, friends)
                        binding.recyclerViewFriends.adapter = adapter
                        binding.recyclerViewFriends.layoutManager =
                            LinearLayoutManager(requireContext())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve users: ${error.message}")
            }

            override fun onClickUser(userID: String) {
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                intent.putExtra("holderID", userID) // pass the user ID as an extra
                startActivity(intent)
            }
        })
    }

    private fun getUserLastLocation(friendID: String, callback: (String) -> Unit) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(friendID).child("location")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val locationString = dataSnapshot.value as String?
                    val locationArray = locationString?.split(", ")
                    val latitude = locationArray?.get(0)?.toDoubleOrNull()
                    val longitude = locationArray?.get(1)?.toDoubleOrNull()
                    val time = if ((locationArray?.size ?: 0) >= 3)
                        locationArray?.get(2)?.toLongOrNull() else null
                    if (latitude != null && longitude != null && time != null) {
                        val currentDate = Date(time)
                        val formatter = SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss",
                            Locale.getDefault()
                        )
                        val timeLast = formatter.format(currentDate).toString()
                        callback(timeLast)
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
}
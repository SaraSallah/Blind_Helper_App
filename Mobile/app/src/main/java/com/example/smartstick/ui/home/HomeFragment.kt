package com.example.smartstick.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstick.MainActivity
import com.example.smartstick.data.User
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHomeBinding
import com.example.smartstick.ui.search.SearchAdapter
import com.example.smartstick.ui.tracking.LocationManager
import com.example.smartstick.ui.tracking.MapsActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    private lateinit var adapter: FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>
    private lateinit var friendRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    override fun getViewBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)


    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        startLocationService()
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
                // Pass the list of friend IDs to a function to retrieve their information
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
                    val user = User(userEmail, "", "", userProfileImageUrl)
                    users.add(user)
//                    val friend = Friend(friendID)
                    friends.add(friendID)

                }
                // Pass the list of friends to your RecyclerView adapter to display them
                val adapter = HolderAdapter(this, users, friends)
                binding.recyclerViewFriends.adapter = adapter
                binding.recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())
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
package com.example.smartstick.ui.addrequest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentAddRequestBinding
import com.example.smartstick.ui.tracking.MapsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AddRequestFragment : BaseFragment<FragmentAddRequestBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    private lateinit var friendRef :DatabaseReference
    private lateinit var requestRef :DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var userID: String
    var currentState ="nothing_happen"
    override fun getViewBinding(): FragmentAddRequestBinding =
        FragmentAddRequestBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
         userID = arguments?.getString("userID").toString()
        requestRef = FirebaseDatabase.getInstance().reference.child("Requests")
        friendRef =FirebaseDatabase.getInstance().reference.child("Friends")

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        loadUserData(userID)
        addCallBacks()
        checkUserExistence(userID)
        getLocation(userID)
        setUpAppBar(true, "Add Request")

    }

    private fun addCallBacks(){
        binding.btnAddRequest.setOnClickListener {
            performAction(userID)
        }
    }

    private fun getLocation(userID: String?){
        binding.btnGetLocation.setOnClickListener {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            intent.putExtra("holderID", userID) // pass the user ID as an extra
            startActivity(intent)
        }
    }

    private fun checkUserExistence(userID: String?) {
        checkIfUsersAreFriends(userID)
        checkIfUserSentRequest(userID)
        checkIfUserReceivedRequest(userID)
        checkIfNothingHappened()
    }

    private fun checkIfUsersAreFriends(userID: String?) {
        friendRef.child(userID!!)
            .child(mUser.uid)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentState = "friend"
                        binding.btnAddRequest.text = "You are Connected"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    private fun checkIfUserSentRequest(userID: String?) {
        requestRef.child(mUser.uid).child(userID!!)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("status").value.toString() == "pending") {
                            currentState = "I_sent_pending"
                            binding.btnAddRequest.text = "Cancel Request"
                        }
                        if (snapshot.child("status").value.toString() == "decline") {
                            currentState = "I_sent_decline"
                            binding.btnAddRequest.text = "Cancel Request"
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    private fun checkIfUserReceivedRequest(userID: String?) {
        requestRef.child(userID!!).child(mUser.uid)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("status").value.toString() == "pending") {
                            currentState = "he_sent_pending"
                            binding.btnAddRequest.text = "Accept Request"
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    @SuppressLint("SetTextI18n")
    private fun checkIfNothingHappened() {
        if (currentState == "nothing_happen") {
            currentState = "nothing_happen"
            binding.btnAddRequest.text = "Send Request"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sendRelativeRequest(userID: String?) {
        val hashMap = hashMapOf<String, Any>("status" to "pending")
        requestRef.child(mUser.uid).child(userID!!).updateChildren(hashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(),"You Have Sent Relative Request"
                        ,Toast.LENGTH_LONG).show()
                    currentState ="I_sent_pending"
                    binding.btnAddRequest.text  = "Cancel  Request"
                } else {
                    Toast.makeText(requireContext(),
                        ""+task.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun cancelRelativeRequest(userID: String?) {
        requestRef.child(mUser.uid).child(userID!!).removeValue().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(requireContext(),
                    "You have Cancelled Relative Request",Toast.LENGTH_LONG).show()
                currentState= "nothing_happen"
                binding.btnAddRequest.text  = "Send Relative Request"
            } else{
                Toast.makeText(requireContext(),
                    ""+task.exception.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addFriend(userID: String?) {
        requestRef.child(userID!!).child(mUser.uid).removeValue().addOnCompleteListener{task->
            if(task.isSuccessful){
                val hashMap = hashMapOf<String, Any>(
                    "status" to "friend",
                )
                friendRef.child(userID).child(mUser.uid).updateChildren(hashMap).addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(requireContext(),
                            "You added Friend",Toast.LENGTH_LONG).show()
                        currentState = "friend"
                        binding.btnAddRequest.text = ("send Message")
                    }
                }
            }
        }
    }

    private fun performAction(userID: String?) {
        when (currentState) {
            "nothing_happen" -> {
                sendRelativeRequest(userID)
            }
            "I_sent_pending", "I_sent_decline" -> {
                cancelRelativeRequest(userID)
            }
            "he_sent_pending" -> {
                addFriend(userID)
            }
        }
    }

    private fun loadUserData(userId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                handleUserSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                handleError(error.message)
            }
        }
        userRef.addValueEventListener(valueEventListener)
    }

    private fun handleUserSnapshot(snapshot: DataSnapshot) {
        if (snapshot.exists()) {
            val userEmail = snapshot.child("email").value.toString()
            binding.tvUserEmail.text = userEmail
        } else {
            showDataNotFoundMessage()
        }
    }

    private fun handleError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showDataNotFoundMessage() {
        Toast.makeText(requireContext(), "Data not found", Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(userID: String): AddRequestFragment {
            val args = Bundle()
            args.putString("userID", userID)
            val fragment = AddRequestFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
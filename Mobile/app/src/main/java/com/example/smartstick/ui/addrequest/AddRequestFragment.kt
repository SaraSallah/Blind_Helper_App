package com.example.smartstick.ui.addrequest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentAddRequestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AddRequestFragment : BaseFragment<FragmentAddRequestBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    private lateinit var mUserRef: DatabaseReference
    private lateinit var friendRef :DatabaseReference
    private lateinit var requestRef :DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mUser: FirebaseUser
    var currentStage ="nothing_happen"
    override fun getViewBinding(): FragmentAddRequestBinding =
        FragmentAddRequestBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        val userID = arguments?.getString("userID")
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests")
        friendRef =FirebaseDatabase.getInstance().getReference().child("Friends")

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        loadUserData(userID!!)
        binding.btnAddRequest.setOnClickListener {
            performAction(userID)
        }
        checkUserExisance(userID)
    }

    private fun checkUserExisance(userID: String?) {
        friendRef.child(mUser.uid).child(userID!!).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    currentStage ="friend"
                    binding.btnAddRequest.text ="Send Message"

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        friendRef.child(userID).child(mUser.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    currentStage ="friend"
                    binding.btnAddRequest.text ="Send Message"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        requestRef.child(mUser.uid).child(userID).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").value.toString()=="pending"){
                        currentStage ="I_sent_pending"
                        binding.btnAddRequest.text  ="Cancel Friend Request"
//                        btn_decline.visibility =View.GONE
                    }

                    if(snapshot.child("status").value.toString()=="decline"){
                        currentStage ="I_sent_decline"
                        binding.btnAddRequest.text  ="Cancel Relative Request"
//                        btn_decline.visibility =View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        requestRef.child(userID).child(mUser.uid).addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").value.toString()=="pending"){
                        currentStage  ="he_sent_pending"
                        binding.btnAddRequest.text  ="Accept Relative Request"

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        if(currentStage =="nothing_happen")
        {
            currentStage ="nothing_happen"
            binding.btnAddRequest.text  ="Send Relative Request"

        }
        friendRef.child(mUser.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").value.toString()=="friend"){
                        currentStage ="Friend"
                        binding.btnAddRequest.text  ="Your are Connected"

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        friendRef.child(userID).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").value.toString()=="friend"){
                        currentStage ="Friend"
                        binding.btnAddRequest.text  ="Your are Connected"
//                        btn_decline.visibility =View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun performAction(userID: String?) {
        //val userID: String? =intent.getStringExtra("userKey")

        if(currentStage == "nothing_happen"){
            val hashMap = hashMapOf<String, Any>("status" to "pending")
            requestRef.child(mUser.uid).child(userID!!).updateChildren(hashMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(),"You Have Sent Relative Request"
                            ,Toast.LENGTH_LONG).show()
//                        btn_decline.setVisibility(View.GONE)
                        currentStage ="I_sent_pending"
                        binding.btnAddRequest.text  = "Cancel Relative Request"
                        // update successful
                    }
                    else {
                        // update failed
                        Toast.makeText(requireContext(),
                            ""+task.exception.toString(),Toast.LENGTH_LONG).show()
                    }
                }
        }
        if(currentStage == "I_sent_pending" || currentStage == "I_sent_decline"){
            requestRef.child(mUser.uid).child(userID!!).removeValue().addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(),
                        "You have Cancelled Relative Request",Toast.LENGTH_LONG).show()
                    currentStage= "nothing_happen"
                    binding.btnAddRequest.text  = "Send Relative Request"
//                    btn_decline.visibility = View.GONE
                }
                else{
                    Toast.makeText(requireContext(),
                        ""+task.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }

        if (currentStage == "he_sent_pending"){
            requestRef.child(userID!!).child(mUser.uid).removeValue().addOnCompleteListener{task->
                if(task.isSuccessful){
                    val hashMap = hashMapOf<String, Any>(
                        "status" to "friend",
                        "HolderID" to mUser.uid
                    )

                    friendRef.child(userID).updateChildren(hashMap).addOnCompleteListener{task ->

                        if(task.isSuccessful){
                            Toast.makeText(requireContext(),
                                "You added Friend",Toast.LENGTH_LONG).show()
                            currentStage = "friend"
                            binding.btnAddRequest.text = ("send Message")
                        }
                    }
                }
            }
        }

//        if(currentStage.equals("friend")){
//
//            btn_decline.text ="Get Current Location"
//            btn_decline.visibility =View.VISIBLE
//            btn_decline.setOnClickListener {
//                startActivity(Intent(this, MapsActivity::class.java))
//            }
//            binding.btnAddRequest.text.visibility= View.GONE
//
//
//
//        }
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
package com.example.smartstick.ui.profile

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    lateinit var mUserRef : DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var mUser : FirebaseUser

    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        mAuth =FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mUserRef = FirebaseDatabase.getInstance().getReference("users")
        loadAccountData()
    }

    fun loadAccountData(){
        mUserRef.child(mUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val email= dataSnapshot.child("email").value.toString()
                    val  password= dataSnapshot.child("password").value.toString()
                    binding.textViewEmail.text = email
                    binding.editTextPassword.setText(password)
                }
                //password = binding.editTextPassword.editText?.text.toString()
                else{
                    Toast.makeText(requireContext(),"Data Not Exist" ,Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),databaseError.message ,Toast.LENGTH_LONG).show()

            }
        })
    }



}
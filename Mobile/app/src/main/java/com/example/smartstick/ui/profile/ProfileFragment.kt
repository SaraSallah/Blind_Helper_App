package com.example.smartstick.ui.profile

import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    private var mUserRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null

    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()

        loadAccountData()
    }

    private fun loadAccountData() {
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
        mUserRef = FirebaseDatabase.getInstance().getReference("users")
        mUserRef!!.child(mUser?.uid ?: "").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val email = dataSnapshot.child("email").value.toString()
                    val password = dataSnapshot.child("password").value.toString()
                    binding.textViewEmail.text = email
                    binding.editTextPassword.editText?.setText(password)
                } else {
                    Toast.makeText(requireContext(), "Data Not Exist", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),databaseError.message ,Toast.LENGTH_LONG).show()

            }
        })
    }



}
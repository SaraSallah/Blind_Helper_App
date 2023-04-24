package com.example.smartstick.ui.auth

import android.util.Log
import com.example.smartstick.MainActivity
import com.example.smartstick.data.User
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentRegisterBinding
import com.example.smartstick.ui.home.HomeHolderFragment
import com.example.smartstick.utils.replaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private var fragmentLogin = LoginFragment()
    private val homeHolderFragment by lazy { HomeHolderFragment() }
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var userName = ""
    private var password = ""

    override val TAG: String = this::class.simpleName.toString()

    override fun getViewBinding(): FragmentRegisterBinding =
        FragmentRegisterBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).hideBottomNavigationView()
        addCallBacks()
    }

    private fun addCallBacks() {
        binding.btnSignUp.setOnClickListener {
            getUserInputs()
            signUp(userName, password)
        }
        binding.textViewSignIn.setOnClickListener{
            replaceFragment(fragmentLogin)
        }
    }

    private fun getUserInputs() {
        userName = binding.editTextEmail.editText?.text.toString().trim()
        password = binding.editTextPassword.editText?.text.toString()
    }

    private fun signUp(email: String?, password: String?) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            // Check if email and password are not empty or null
            Log.i("TAG", "Please enter email and password")
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(email, password)
                    addUser(user)
                    replaceFragment(homeHolderFragment)
                    Log.i("TAG", "Registration successful")
                } else {
                    // Handle registration errors
                    Log.e("TAG", "Registration failed", task.exception)
                }
            }
    }

    private fun addUser(user: User) {
        firebaseDatabase.reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user)
    }

}
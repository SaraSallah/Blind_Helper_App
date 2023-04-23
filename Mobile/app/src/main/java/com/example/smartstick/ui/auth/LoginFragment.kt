package com.example.smartstick.ui.auth

import android.widget.Toast
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentLoginBinding
import com.example.smartstick.utils.replaceFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val TAG: String =this::class.simpleName.toString()

    private val  fragmentRegister = RegisterFragment()

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
     private var email = ""
     private var password = ""
    
    override fun getViewBinding(): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

    override fun setUp() {
        addCallBacks()
    }

    private fun loginUser(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.isComplete
                }
            }}

    private fun addCallBacks() {
        binding.btnLogin.setOnClickListener {
            getUserInputs()
            loginUser(email, password)
            Toast.makeText(requireContext(), "Success" , Toast.LENGTH_LONG).show()
//            replaceFragment()
        }
        binding.textViewSignUp.setOnClickListener{
            replaceFragment(fragmentRegister)
        }
    }

    private fun getUserInputs() {
        email = binding.editTextEmail.editText?.text.toString().trim()
        password = binding.editTextPassword.editText?.text.toString()
    }

}
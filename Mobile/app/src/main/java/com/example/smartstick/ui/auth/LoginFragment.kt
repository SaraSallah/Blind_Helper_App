package com.example.smartstick.ui.auth


import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentLoginBinding
import com.example.connection.MyBackgroundService
import com.example.smartstick.ui.home.HolderFragment
import com.example.smartstick.ui.home.HomeFragment
import com.example.smartstick.utils.replaceFragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val TAG: String =this::class.simpleName.toString()
    private lateinit var userType : String

    private val fragmentRegister by lazy { RegisterFragment() }
    private val homeRelativeFragment by lazy { HomeFragment() }
    private val holderFragment by lazy { HolderFragment() }
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
     private var email = ""
     private var password = ""
    
    override fun getViewBinding(): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).hideBottomNavigationView()
        addCallBacks()
        setUpAppBar(false, "Login")
    }

    private fun addCallBacks() {
        checkUserType()
        binding.btnLogin.setOnClickListener {
            getUserInputs()
            loginUser(email, password)
        }
        binding.textViewSignUp.setOnClickListener{
            replaceFragment(fragmentRegister)
        }
    }

    private fun loginUser(email: String, password: String){
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please,Enter e-mail & password" ,
                Toast.LENGTH_LONG).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful ) {
                    val serviceIntent = Intent(requireContext(), MyBackgroundService::class.java)

                    if (userType == "Holder") {
                        replaceFragment(holderFragment)
                        requireContext().startService(serviceIntent)
                    } else if (userType == "Relative") {
                        replaceFragment(homeRelativeFragment)
                        requireContext().stopService(serviceIntent)

                    }
                }

                else
                {
                    Toast.makeText(requireContext(), "Please,Enter correct e-mail or password"
                        , Toast.LENGTH_LONG).show()

                }
            }}


    private fun checkUserType(){
        binding.typeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.relativeButton.id -> {
                    userType ="Relative"
                    Log.i("Tag",userType)
                    val sharedPref = requireActivity().getSharedPreferences("user_type", Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        putString("type", userType)
                        apply()
                    }
                }
                binding.holderButton.id -> {
                    userType = "Holder"
                    Log.i("Tag",userType)
                    val sharedPref = requireActivity().getSharedPreferences("user_type", Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        putString("type", userType)
                        apply()
                    }

                }
            }
        }
    }

    private fun getUserInputs() {
        email = binding.editTextEmail.editText?.text.toString().trim()
        password = binding.editTextPassword.editText?.text.toString()
    }

}

package com.example.smartstick.ui.auth


import android.util.Log
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentLoginBinding
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
                    if( userType =="Holder"){
                        Toast.makeText(requireContext(), "Success" , Toast.LENGTH_LONG).show()
                        replaceFragment(holderFragment)
                    }
                    else if( userType =="Relative"){
                        Toast.makeText(requireContext(), "Success" , Toast.LENGTH_LONG).show()
                        replaceFragment(homeRelativeFragment)
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
                }
                binding.holderButton.id -> {
                    userType = "Holder"
                    Log.i("Tag",userType)

                }
            }
        }
    }

    private fun getUserInputs() {
        email = binding.editTextEmail.editText?.text.toString().trim()
        password = binding.editTextPassword.editText?.text.toString()
    }

}

//    private fun navigateToFragment() {
//        val userType = sharedPrefs.getString("userType", "")
//        if (userType == "relative") {
//            replaceFragment(HomeFragment())
//        } else if (userType == "holder") {
//            replaceFragment(HolderFragment())
//        }
//    }
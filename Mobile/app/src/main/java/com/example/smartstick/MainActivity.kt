package com.example.smartstick

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartstick.databinding.ActivityMainBinding
import com.example.smartstick.ui.auth.LoginFragment
import com.example.smartstick.ui.home.HolderFragment
import com.example.smartstick.ui.home.HomeFragment
import com.example.smartstick.ui.profile.ProfileFragment
import com.example.smartstick.ui.search.SearchFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragmentProfile = ProfileFragment()
    private val fragmentSearch = SearchFragment()
    private val loginFragment = LoginFragment ()
    private val fragmentHome = HomeFragment()
    private val holderFragment = HolderFragment()
    private lateinit var userType :String
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences :SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSubView()
        addNavigationBottomListener()
        auth = Firebase.auth
    }

    private fun addNavigationBottomListener(){
        binding.bottomNav.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.homeFragment -> {
                    sharedPreferences = getSharedPreferences("user_type", Context.MODE_PRIVATE)
                    userType = sharedPreferences.getString("type", "") ?: ""
                    if (userType == "Holder") {
                        Log.i("Home","Holder")
                        replaceFragment(holderFragment)
                    } else if (userType == "Relative") {
                        Log.i("Home","Relative")
                        replaceFragment(fragmentHome)
                    }
                    true
                }
                R.id.profileFragment ->{
                    replaceFragment(fragmentProfile)
                    true
                }
                R.id.searchFragment ->{
                    replaceFragment(fragmentSearch)
                    true
                }
                R.id.logout ->{
                    val alertDialog = AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { _, _ ->
                            sharedPreferences.edit().remove("userType").apply() // Clear the user type from the shared preference
                            auth.signOut()
                            replaceFragment(loginFragment)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                    alertDialog.show()
                    true
                }
                else -> false
            }
        }
    }

    fun showBottomNavigationView() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    fun hideBottomNavigationView() {
        binding.bottomNav.visibility = View.GONE
    }
    private fun initSubView() {
        addFragment(loginFragment)
    }
    private fun replaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }

    private fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            sharedPreferences = getSharedPreferences("user_type", Context.MODE_PRIVATE)
            userType = sharedPreferences.getString("type", "") ?: ""
            if (userType == "Holder") {
                Log.i("Home","Holder")
                replaceFragment(HolderFragment())
            } else if (userType == "Relative") {
                Log.i("Home","Relative")
                replaceFragment(fragmentHome)
            }
        }
        else{
            replaceFragment(loginFragment)
        }
    }


}
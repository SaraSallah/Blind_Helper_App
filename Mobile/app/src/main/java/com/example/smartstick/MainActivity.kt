package com.example.smartstick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.smartstick.databinding.ActivityMainBinding
import com.example.smartstick.ui.auth.LoginFragment
import com.example.smartstick.ui.auth.RegisterFragment
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
    private val  fragmentRegister = RegisterFragment()
    private val loginFragment =LoginFragment ()
    private val fragmentHome = HomeFragment()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        initSubView()
        addNavigationBottomListener()

    }
    private fun addNavigationBottomListener(){
        binding.bottomNav.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.homeFragment ->{
                    replaceFragment(fragmentHome)
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
                    auth.signOut()
                    replaceFragment(loginFragment)
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
        addFragment(fragmentProfile)
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
            replaceFragment(fragmentHome)
        }
    }
}
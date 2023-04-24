package com.example.smartstick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.smartstick.databinding.ActivityMainBinding
import com.example.smartstick.ui.auth.LoginFragment
import com.example.smartstick.ui.auth.RegisterFragment
import com.example.smartstick.ui.home.HomeHolderFragment
import com.example.smartstick.ui.home.HomeRelativeFragment
import com.example.smartstick.ui.profile.ProfileFragment
import com.example.smartstick.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragmentProfile = ProfileFragment()
    private val fragmentSearch = SearchFragment()
    private val  fragmentRegister = RegisterFragment()
    private val loginFragment =LoginFragment ()
    private val fragmentHomeRelative = HomeRelativeFragment()
    private val fragmentHomeHolder = HomeHolderFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSubView()
        addNavigationBottomListener()

    }
    private fun addNavigationBottomListener(){
        binding.bottomNav.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.homeFragment ->{
                    replaceFragment(fragmentHomeHolder)
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
        addFragment(fragmentRegister)
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
}
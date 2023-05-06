package com.example.smartstick.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartstick.MainActivity
import com.example.smartstick.R
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHolderBinding
import com.example.smartstick.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HolderFragment : BaseFragment<FragmentHolderBinding>(){
    override val TAG: String = this::class.simpleName.toString()

    override fun getViewBinding(): FragmentHolderBinding =
        FragmentHolderBinding.inflate(layoutInflater)


    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
//        startLocationService()
//        friendRef = FirebaseDatabase.getInstance().getReference("Friends")
//        mAuth = FirebaseAuth.getInstance()
//        mUser = mAuth.currentUser!!
//        getAllFriendsID()
    }

}
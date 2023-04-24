package com.example.smartstick.ui.home

import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentHomeHolderBinding

class HomeHolderFragment : BaseFragment<FragmentHomeHolderBinding>() {
    override val TAG: String =this::class.simpleName.toString()

    override fun getViewBinding(): FragmentHomeHolderBinding =
        FragmentHomeHolderBinding.inflate(layoutInflater)


    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()

    }

}
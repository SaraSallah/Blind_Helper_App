package com.example.smartstick.ui.addrequest

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentAddRequestBinding
import com.example.smartstick.ui.profile.ProfileFragment

class AddRequestFragment : BaseFragment<FragmentAddRequestBinding>() {
    override val TAG: String =this::class.simpleName.toString()
    override fun getViewBinding(): FragmentAddRequestBinding
    = FragmentAddRequestBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        val userID = arguments?.getString("userID")
        Toast.makeText(requireContext(), userID, Toast.LENGTH_LONG).show()

    }
    companion object {
        fun newInstance(userID: String): AddRequestFragment {
            val args = Bundle()
            args.putString("userID", userID)
            val fragment = AddRequestFragment()
            fragment.arguments = args
            return fragment
        }
    }


}
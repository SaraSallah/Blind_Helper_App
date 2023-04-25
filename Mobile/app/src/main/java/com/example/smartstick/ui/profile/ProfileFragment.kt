package com.example.smartstick.ui.profile

import android.os.Bundle
import android.widget.Toast
import com.example.smartstick.MainActivity
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val TAG: String = this::class.simpleName.toString()
    private var userId: String? = null

    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        val userID = arguments?.getString("userID")
        Toast.makeText(requireContext(), userID, Toast.LENGTH_LONG).show()
//

//        arguments?.let { bundle ->
//             userId = bundle.getString("userId")
//            Toast.makeText(requireContext(),userId,Toast.LENGTH_LONG).show()
//        }
    }

    companion object {
        fun newInstance(userID: String): ProfileFragment {
            val args = Bundle()
            args.putString("userID", userID)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }


}
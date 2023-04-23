package com.example.smartstick.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartstick.R
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentSearchBinding

class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val TAG: String =this ::class.simpleName.toString()
    override fun getViewBinding(): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater)

    override fun setUp() {
    }

}
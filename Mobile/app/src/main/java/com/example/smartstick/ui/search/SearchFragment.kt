package com.example.smartstick.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.example.smartstick.MainActivity
import com.example.smartstick.R
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentSearchBinding
import com.google.android.material.search.SearchBar

class SearchFragment : BaseFragment<FragmentSearchBinding>(),SearchView.OnQueryTextListener {
    override val TAG: String =this ::class.simpleName.toString()
    override fun getViewBinding(): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}
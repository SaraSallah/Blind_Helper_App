package com.example.smartstick.ui.search

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstick.MainActivity
import com.example.smartstick.data.User
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentSearchBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : BaseFragment<FragmentSearchBinding>(),SearchView.OnQueryTextListener {

    private lateinit var adapter: FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>
    private lateinit var mUserRef: DatabaseReference
    private lateinit var options : FirebaseRecyclerOptions<User>
    override val TAG: String =this ::class.simpleName.toString()
    override fun getViewBinding(): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mUserRef , User::class.java).build()
        adapter = SearchAdapter(options,requireContext())
        binding.recyclerViewSearch.adapter = adapter
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        mUserRef =FirebaseDatabase.getInstance().getReference("users")
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}
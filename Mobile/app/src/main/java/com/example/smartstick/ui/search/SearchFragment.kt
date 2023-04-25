package com.example.smartstick.ui.search

import android.util.Log
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
        Log.i("TAG", "Error here")
        mUserRef =FirebaseDatabase.getInstance().getReference("users")
        options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(mUserRef , User::class.java).build()
        adapter = SearchAdapter(options)
        binding.recyclerViewSearch.adapter = adapter
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}
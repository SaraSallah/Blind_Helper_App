package com.example.smartstick.ui.search

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstick.MainActivity
import com.example.smartstick.data.User
import com.example.smartstick.data.base.BaseFragment
import com.example.smartstick.databinding.FragmentSearchBinding
import com.example.smartstick.ui.profile.ProfileFragment
import com.example.smartstick.utils.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : BaseFragment<FragmentSearchBinding>(),SearchView.OnQueryTextListener,
    android.widget.SearchView.OnQueryTextListener,SearchAdapter.UserInteractionListener {

    private lateinit var adapter: FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>
    private lateinit var mUserRef: DatabaseReference
    private lateinit var options: FirebaseRecyclerOptions<User>
    private val fragmentProfile = ProfileFragment()
    override val TAG: String = this::class.simpleName.toString()
    override fun getViewBinding(): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater)

    override fun setUp() {
        (activity as MainActivity).showBottomNavigationView()
        addCallBacks()
    }

    private fun addCallBacks() {
        addSearchListener()
    }

    private fun addSearchListener() {
        binding.searchBar.setOnQueryTextListener(this)
    }


    private fun getDataFromFirebaseToRecyclerView(query: String? = "") {
        mUserRef = FirebaseDatabase.getInstance().getReference("users")
        val queryRef = if (query?.isEmpty()!!) {
            mUserRef
        } else {
            mUserRef.orderByChild("email").startAt(query).endAt("$query\uf8ff")
        }
        options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(queryRef , User::class.java).build()
        adapter = SearchAdapter(this,options)
        binding.recyclerViewSearch.adapter = adapter
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
    }



    private fun searchByQueryAndSetDataInAdapter(query: String?) {
        binding.recyclerViewSearch.visibility =
            if (query!!.isNotEmpty()) View.VISIBLE else View.GONE
        getDataFromFirebaseToRecyclerView(query)
        adapter.startListening()

    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { searchByQueryAndSetDataInAdapter(it) }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { searchByQueryAndSetDataInAdapter(it) }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized)
            adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (::adapter.isInitialized)
            adapter.stopListening()
    }

    override fun onClickUser(userID: String) {
        val profileFragment = ProfileFragment.newInstance(userID)
        replaceFragment(profileFragment)

    }


}
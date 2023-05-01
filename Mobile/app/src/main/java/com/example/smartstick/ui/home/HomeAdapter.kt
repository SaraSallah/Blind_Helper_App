package com.example.smartstick.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstick.R
import com.example.smartstick.data.User
import com.example.smartstick.databinding.SingleViewFindFriendBinding
import com.example.smartstick.ui.search.SearchAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class HomeAdapter (
    private val listener: SearchAdapter.UserInteractionListener,
    private val options: FirebaseRecyclerOptions<User>
) :
    FirebaseRecyclerAdapter<User, HomeAdapter.HomeViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_view_find_friend, parent, false)
        return HomeAdapter.HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int, model: User) {
        TODO("Not yet implemented")
    }




    class HomeViewHolder(viewItem: View) :
        RecyclerView.ViewHolder(viewItem) {
        val binding = SingleViewFindFriendBinding.bind(itemView)
    }
}
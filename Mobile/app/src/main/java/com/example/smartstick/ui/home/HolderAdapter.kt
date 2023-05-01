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

class HolderAdapter (
    private val listener: HolderAdapter.UserInteractionListener,
    private val relativeFriends: List<User>
) :
    RecyclerView.Adapter<HolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_view_find_friend, parent, false)
        return HolderAdapter.ViewHolder(view)
    }

    override fun getItemCount() = relativeFriends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = relativeFriends[position]
        holder.binding.userEmail.text = friend.email
        holder.binding.root.setOnClickListener { listener.onClickUser(friend(position).key.toString()) }
    }

    interface UserInteractionListener {
        fun onClickUser(userID: String)
    }

    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val binding = SingleViewFindFriendBinding.bind(itemView)
    }

}

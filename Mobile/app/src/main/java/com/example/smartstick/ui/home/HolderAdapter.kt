package com.example.smartstick.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstick.R
import com.example.smartstick.data.User
import com.example.smartstick.databinding.ItemGetAllHolderBinding

class HolderAdapter(
    private val listener: UserInteractionListener,
    private val relativeFriends: List<User>,
    private val keys: List<String>
) :
    RecyclerView.Adapter<HolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_get_all_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = relativeFriends.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = relativeFriends[position]
        holder.binding.userEmail.text = friend.email
        holder.binding.lastSeen.text = friend.lastSeenLocation
        holder.binding.root.setOnClickListener { listener.onClickUser(keys[position]) }
    }

    interface UserInteractionListener {
        fun onClickUser(userID: String)
    }

    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val binding = ItemGetAllHolderBinding.bind(itemView)
    }

}

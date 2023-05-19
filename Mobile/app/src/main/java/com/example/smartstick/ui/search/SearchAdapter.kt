package com.example.smartstick.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstick.R
import com.example.smartstick.data.User
import com.example.smartstick.databinding.SingleViewFindFriendBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class SearchAdapter (
    private val listener: UserInteractionListener,
    private val options: FirebaseRecyclerOptions<User>) :
    FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_view_find_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        holder.binding.userEmail.text = model.email
        holder.binding.root.setOnClickListener { listener.onClickUser(getRef(position).key.toString()) }
    }

    class ViewHolder(viewItem: View) :
        RecyclerView.ViewHolder(viewItem) {
        val binding = SingleViewFindFriendBinding.bind(itemView)
    }

    interface UserInteractionListener {
        fun onClickUser(userID: String)
    }
}


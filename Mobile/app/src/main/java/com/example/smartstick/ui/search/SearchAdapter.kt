package com.example.smartstick.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstick.R
import com.example.smartstick.data.User
import com.example.smartstick.databinding.SingleViewFindFriendBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SearchAdapter (private val options: FirebaseRecyclerOptions<User>) :
    FirebaseRecyclerAdapter<User, SearchAdapter.ViewHolder>(options) {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SingleViewFindFriendBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
            holder.bind(model)
    }

    open class ViewHolder (private val binding: SingleViewFindFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: User) {
            binding.userEmail.text = model.email
//            binding.userType.text = model.type
            binding.imageProfile.setImageResource(R.drawable.defualt)
        }
    }
}

//holder.itemView.setOnClickListener {
//                val intent = Intent(context, SearchFragment::class.java)
//                intent.putExtra("userKey", getRef(position).key.toString())
//                context.startActivity(intent)
//}



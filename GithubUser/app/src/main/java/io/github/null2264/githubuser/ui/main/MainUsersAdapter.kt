package io.github.null2264.githubuser.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.null2264.githubuser.databinding.ItemRowUserBinding
import io.github.null2264.githubuser.lib.User

class MainUsersAdapter(private val users: List<User>) :
    RecyclerView.Adapter<MainUsersAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = users[position]
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(user.avatar)
            .circleCrop()
            .into(holder.binding.ivItemAvatar)
        holder.binding.tvItemName.text = user.name ?: user.username
        holder.binding.tvItemFollower.text = StringBuilder(user.followers.toString())
            .append(" followers")
        holder.binding.tvItemFollowing.text = StringBuilder(user.following.toString())
            .append(" following")
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(users[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = users.size

    class ListViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}
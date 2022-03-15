package io.github.null2264.githubuser.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.null2264.githubuser.databinding.ItemRowUserBinding
import io.github.null2264.githubuser.lib.User
import io.github.null2264.githubuser.lib.UserDiffCallback

class MainUsersAdapter :
    RecyclerView.Adapter<MainUsersAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var users: List<User> = listOf()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
            Glide.with(itemView.context)
                .asBitmap()
                .load(user.avatar)
                .circleCrop()
                .into(binding.ivItemAvatar)

            binding.apply {
                tvItemName.text = user.name ?: user.username
                tvItemFollower.text = StringBuilder(user.followers.toString())
                    .append(" followers")
                tvItemFollowing.text = StringBuilder(user.following.toString())
                    .append(" following")
            }

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(users[adapterPosition])
            }
        }
    }

    fun setList(newUsers: List<User>) {
        val diffCallback = UserDiffCallback(users, newUsers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = newUsers
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = users.size

    class ListViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}
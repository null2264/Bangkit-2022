package io.github.null2264.githubuser.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.null2264.githubuser.databinding.ItemRowUserBinding
import io.github.null2264.githubuser.lib.User

class UsersRecyclerAdapter : RecyclerView.Adapter<UsersRecyclerAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }

    private val differs = AsyncListDiffer(this, DiffCallback())
    fun submitList(list: List<User>) = differs.submitList(list)

    class ListViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = differs.currentList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val users = differs.currentList
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
                onItemClickCallback.onItemClicked(users[absoluteAdapterPosition])
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}
package io.github.null2264.githubuser.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.null2264.githubuser.data.database.entity.UserEntity
import io.github.null2264.githubuser.databinding.ItemRowUserBinding
import io.github.null2264.githubuser.ui.detail.DetailActivity

class UsersRecyclerAdapter : RecyclerView.Adapter<UsersRecyclerAdapter.ListViewHolder>() {
    inner class DiffCallback : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean = oldItem == newItem
    }

    private val differs = AsyncListDiffer(this, DiffCallback())
    fun submitList(list: List<UserEntity>) = differs.submitList(list)

    class ListViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserEntity) {
            Glide.with(itemView.context)
                .asBitmap()
                .load(user.avatarUrl)
                .circleCrop()
                .into(binding.ivItemAvatar)

            binding.apply {
                tvItemName.text = user.name ?: user.login
                tvItemFollower.text = StringBuilder(user.followers.toString())
                    .append(" followers")
                tvItemFollowing.text = StringBuilder(user.following.toString())
                    .append(" following")
            }
            itemView.setOnClickListener {
                val ctx = itemView.context
                ctx.startActivity(Intent(ctx, DetailActivity::class.java)
                    .putExtra("USER_DATA", user))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = differs.currentList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(differs.currentList[position])
    }
}
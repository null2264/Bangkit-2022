package io.github.null2264.dicodingstories.data.story

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.model.Story
import io.github.null2264.dicodingstories.databinding.ItemRowStoryBinding
import io.github.null2264.dicodingstories.ui.story.DashboardFragmentDirections
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class StoriesRecyclerAdapter(private val fragment: Fragment) : RecyclerView.Adapter<StoriesRecyclerAdapter.ListViewHolder>() {
    inner class DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem == newItem
    }

    private val differs = AsyncListDiffer(this, DiffCallback())
    fun submitList(list: List<Story>) = differs.submitList(list)

    class ListViewHolder(
        private val fragment: Fragment,
        private val binding: ItemRowStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            val transitionImgName = fragment.requireContext().getString(R.string.transition_image_name, story.id)
            binding.apply {
                ivStory.transitionName = transitionImgName
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivStory)
                tvAuthor.text = story.name
                tvDescription.text = story.description
                val dateTime = ZonedDateTime.parse(story.createdAt, DateTimeFormatter.ISO_DATE_TIME)
                    .withZoneSameInstant(ZoneId.of(TimeZone.getDefault().id))
                    .format(DateTimeFormatter.ofPattern(fragment.requireContext().getString(R.string.date_time_format)))
                tvDate.text = dateTime
            }

            itemView.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    binding.ivStory to transitionImgName
                )

                val action = DashboardFragmentDirections.actionShowDetail(story)
                it.findNavController().navigate(action, extras)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            fragment,
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(differs.currentList[position])
    }

    override fun getItemCount(): Int = differs.currentList.size
}
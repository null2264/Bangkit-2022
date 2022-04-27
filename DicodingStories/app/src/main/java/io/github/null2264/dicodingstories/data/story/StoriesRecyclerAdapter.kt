package io.github.null2264.dicodingstories.data.story

import android.content.Context
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
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

class StoriesRecyclerAdapter(private val context: Context) :
    PagingDataAdapter<Story, StoriesRecyclerAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(
        private val context: Context,
        private val binding: ItemRowStoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            val transitionImgName = context.getString(R.string.transition_image_name, story.id)
            binding.apply {
                ivStory.transitionName = transitionImgName
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivStory)

                if (story.lat != null && story.lon != null) {
                    tvLocation.isGone = false
                    tvLocation.text = StringBuilder("${story.lat}, ${story.lon}")
                }

                tvAuthor.text = story.name
                tvDescription.text = story.description
                val dateTime = ZonedDateTime.parse(story.createdAt, DateTimeFormatter.ISO_DATE_TIME)
                    .withZoneSameInstant(ZoneId.of(TimeZone.getDefault().id))
                    .format(DateTimeFormatter.ofPattern(context.getString(R.string.date_time_format)))
                tvDate.text = dateTime
            }

            var alreadyClicked = false // prevent crash when user double tap an item

            itemView.setOnClickListener {
                if (!alreadyClicked) {
                    val extras = FragmentNavigatorExtras(
                        binding.ivStory to transitionImgName
                    )
                    val action = DashboardFragmentDirections.actionShowDetail(story)
                    alreadyClicked = true
                    it.findNavController().navigate(action, extras)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position) as Story)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            context,
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean = oldItem == newItem
        }
    }
}
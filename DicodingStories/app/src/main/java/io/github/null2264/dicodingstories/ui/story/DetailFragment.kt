package io.github.null2264.dicodingstories.ui.story

import android.graphics.drawable.Drawable
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Explode
import androidx.transition.Slide
import androidx.transition.TransitionInflater
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.databinding.FragmentDetailBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DetailFragment : Fragment() {
    private val binding: FragmentDetailBinding by viewBinding(CreateMethod.INFLATE)
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_bounds)
        enterTransition = Explode()
        returnTransition = Slide()
        postponeEnterTransition()
        val story = args.story
        binding.apply {
            val activity = (requireActivity() as AppCompatActivity)
            activity.setSupportActionBar(appbar)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
            appbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            ivStory.transitionName = getString(R.string.transition_image_name, story.id)
            tvAuthor.text = story.name
            tvDescription.text = story.description
            val dateTime = ZonedDateTime.parse(story.createdAt, DateTimeFormatter.ISO_DATE_TIME)
                .withZoneSameInstant(ZoneId.of(TimeZone.getDefault().id))
                .format(DateTimeFormatter.ofPattern(requireContext().getString(R.string.date_time_format)))
            tvDate.text = dateTime
        }

        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }
        }
        Glide.with(requireContext())
            .load(story.photoUrl)
            .dontAnimate()
            .dontTransform()
            .onlyRetrieveFromCache(true)
            .listener(listener)
            .into(binding.ivStory)
        return binding.root
    }
}
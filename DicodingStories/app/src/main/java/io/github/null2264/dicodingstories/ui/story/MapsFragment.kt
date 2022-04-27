package io.github.null2264.dicodingstories.ui.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.model.Story
import io.github.null2264.dicodingstories.databinding.FragmentMapsBinding

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {
    private val binding: FragmentMapsBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val activity = (requireActivity() as AppCompatActivity)
            activity.setSupportActionBar(appbar.toolbar)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
            appbar.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        @Suppress("UNCHECKED_CAST")
        val data = arguments?.get("stories") as Array<Story>?
        data?.forEach { story ->
            if (story.lat != null && story.lon != null) {
                val pos = LatLng(story.lat.toDouble(), story.lon.toDouble())
                googleMap.addMarker(MarkerOptions().position(pos).title(story.name).snippet(story.description))
            }
        }
    }
}
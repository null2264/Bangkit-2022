package io.github.null2264.dicodingstories.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.databinding.ActivitySelectionMapBinding

class SelectionMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding: ActivitySelectionMapBinding by viewBinding(CreateMethod.INFLATE)
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(appbar.toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
            appbar.toolbar.setNavigationOnClickListener { finish() }

            btnSelect.setOnClickListener {
                if (marker != null) {
                    val intent = Intent()
                    intent.putExtra("latLng", marker!!.position)
                    setResult(200, intent)
                }
                finish()
            }
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setMarker(googleMap: GoogleMap, position: LatLng) {
        marker?.remove()
        marker = googleMap.addMarker(MarkerOptions().position(position))
        binding.btnSelect.isEnabled = marker != null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener {
            setMarker(googleMap, it)
        }
    }
}
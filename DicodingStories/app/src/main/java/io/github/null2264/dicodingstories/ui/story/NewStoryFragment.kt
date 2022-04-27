package io.github.null2264.dicodingstories.ui.story

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.Gravity.TOP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.databinding.FragmentNewStoryBinding
import io.github.null2264.dicodingstories.lib.Common
import io.github.null2264.dicodingstories.ui.CameraActivity
import io.github.null2264.dicodingstories.ui.SelectionMapActivity
import io.github.null2264.dicodingstories.widget.dialog.ZiAlertDialog
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NewStoryFragment : Fragment() {
    @Inject
    lateinit var apiService: ApiService

    private val binding: FragmentNewStoryBinding by viewBinding(CreateMethod.INFLATE)
    private var getFile: File? = null
    private var manualLocation: LatLng? = null
    private var userLocation: LatLng? = null
    private var locationMode: Int = 1
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    private fun setLocationMode(location: LatLng?) {
        if (location == null)
            setLocationMode(1)
        else {
            this.manualLocation = location
            setLocationMode(2)
        }
    }

    private fun setLocationMode(mode: Int) {
        if (mode != locationMode)
            locationMode = mode

        binding.apply {
            when (mode) {
                0 -> {
                    btnLocation.text = getString(R.string.none)
                }
                1 -> {
                    btnLocation.text = getString(R.string.auto)
                }
                2 -> {
                    btnLocation.text = StringBuilder("${manualLocation?.latitude}, ${manualLocation?.longitude}")
                }
                else -> {}
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createLocationRequest()
        createLocationCallback()
        startLocationUpdates()
        setLocationMode(locationMode)
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

            btnLocation.setOnClickListener {
                val popup = PopupMenu(
                    requireContext(), btnLocation, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0)
                popup.menuInflater.inflate(R.menu.menu_location, popup.menu)

                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_action_location_none -> {
                            setLocationMode(0)
                            true
                        }

                        R.id.menu_action_location_auto -> {
                            setLocationMode(1)
                            true
                        }

                        R.id.menu_action_location_manual -> {
                            launcherIntentSelectionMap.launch(
                                Intent(requireContext(), SelectionMapActivity::class.java))
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }

                popup.show()
            }

            etDesc.apply {
                editText.gravity = TOP
            }

            btnCamera.setOnClickListener {
                launcherIntentCameraX.launch(Intent(requireContext(), CameraActivity::class.java))
            }

            btnGallery.setOnClickListener {
                val intent = Intent()
                intent.action = ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                launcherIntentGallery.launch(chooser)
            }

            btnUpload.setOnButtonClickListener {
                if (getFile != null) {
                    val file = Common.compressImage(getFile as File)

                    if (etDesc.validateInput()) {
                        btnUpload.onStartLoading()
                        val description = etDesc.text?.toString() as String
                        val requestImgFile = file.asRequestBody("image.jpeg".toMediaTypeOrNull())
                        val imageMultipart = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            requestImgFile
                        )

                        val curLoc = when (locationMode) {
                            1 -> userLocation
                            2 -> manualLocation
                            else -> null
                        }

                        lifecycleScope.launch {
                            val resp = apiService.newStory(
                                imageMultipart,
                                description,
                                curLoc?.latitude,
                                curLoc?.longitude
                            )
                            if (resp.isSuccessful && resp.body() != null) {
                                findNavController().apply {
                                    previousBackStackEntry?.savedStateHandle?.set("isSuccess", true)
                                    popBackStack()
                                }
                            } else if (resp.errorBody() != null) {
                                val errJSON = Common.parseError(resp.errorBody()!!.string())
                                Common.quickDialog(
                                    requireContext(), ZiAlertDialog.ERROR, getString(R.string.fail), errJSON.message)
                            }
                            btnUpload.onStopLoading()
                        }
                    } else {
                        Common.quickDialog(
                            requireContext(),
                            ZiAlertDialog.ERROR,
                            getString(R.string.fail),
                            getString(R.string.description_empty)
                        )
                    }
                } else {
                    Common.quickDialog(
                        requireContext(),
                        ZiAlertDialog.ERROR,
                        getString(R.string.fail),
                        getString(R.string.image_empty)
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocationUpdates()
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SUCCESS) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile

            val result = Common.rotateCapturedImage(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.ivPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            getFile = Common.uriToFile(selectedImg, requireContext())
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getUserLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getUserLocation()
            }
            else -> {}
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getUserLocation()
            }
            .addOnFailureListener {
                setLocationMode(0)
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {}
    }

    private fun getUserLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exc: SecurityException) {
            Log.e("ziError:", exc.message.toString())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val launcherIntentSelectionMap = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_SUCCESS) {
            setLocationMode(it.data?.getParcelableExtra("latLng"))
        }
    }

    companion object {
        const val RESULT_SUCCESS = 200
    }
}
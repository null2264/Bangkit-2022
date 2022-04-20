package io.github.null2264.dicodingstories.ui.story

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Gravity.TOP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.databinding.FragmentNewStoryBinding
import io.github.null2264.dicodingstories.lib.Common
import io.github.null2264.dicodingstories.ui.CameraActivity
import io.github.null2264.dicodingstories.widget.dialog.ZiAlertDialog
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class NewStoryFragment : Fragment() {
    @Inject
    lateinit var apiService: ApiService

    private val binding: FragmentNewStoryBinding by viewBinding(CreateMethod.INFLATE)
    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
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
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
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

                        lifecycleScope.launch {
                            val resp = apiService.newStory(imageMultipart, description)
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

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
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

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}
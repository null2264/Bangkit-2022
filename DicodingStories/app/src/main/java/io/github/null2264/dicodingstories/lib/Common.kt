package io.github.null2264.dicodingstories.lib

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.gson.Gson
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.model.CommonResponse
import io.github.null2264.dicodingstories.widget.dialog.ZiAlertDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

object Common {
    var API_URL = "https://story-api.dicoding.dev/v1/"

    val timeStamp: String = SimpleDateFormat(
        "dd-MMM-yyyy",
        Locale.US,
    ).format(System.currentTimeMillis())

    fun getDrawableWithAttrTint(ctx: Context, resId: Int, @AttrRes colorId: Int): Drawable? {
        val typedValue = TypedValue()
        ctx.theme.resolveAttribute(colorId, typedValue, true)
        return getDrawableWithTint(ctx, resId, typedValue.data)
    }

    fun getDrawableWithTint(ctx: Context, resId: Int, @ColorRes colorId: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(ctx, resId)
        if (drawable != null) {
            val wrapped = DrawableCompat.wrap(drawable)
            val color = try {
                ContextCompat.getColor(ctx, colorId)
            } catch (exc: NotFoundException) {
                colorId
            }
            DrawableCompat.setTint(wrapped.mutate(), color)
            return wrapped
        }
        return null
    }

    fun quickDialog(
        context: Context,
        type: Int,
        title: String,
        message: String,
        callback: ZiAlertDialog.ZiOnClickListener? = null,
    ) {
        ZiAlertDialog(context).apply {
            this.type = type
            this.title = title
            content = message
            cancelText = context.getString(android.R.string.ok)
            if (callback != null)
                when (type) {
                    ZiAlertDialog.SUCCESS -> {
                        isConfirmEnabled = true
                        isCancelEnabled = false
                        setOnConfirmListener(callback)
                    }
                    ZiAlertDialog.ERROR -> {
                        isConfirmEnabled = false
                        isCancelEnabled = true
                        setOnCancelListener(callback)
                    }
                    else -> {}
                }
        }.show()
    }

    fun hideKeyboard(context: Context, windowToken: android.os.IBinder) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }

    fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDir = if (mediaDir != null && mediaDir.exists()) mediaDir else application.filesDir

        return File(outputDir, "$timeStamp.jpg")
    }

    fun rotateCapturedImage(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()

        return if (isBackCamera) {
//            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
//            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun parseError(string: String): CommonResponse = Gson().fromJson(string, CommonResponse::class.java)

    fun isNightModeOn(context: Context): Boolean {
        return when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> true
        }
    }
}
package io.github.null2264.dicodingstories.lib

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat

object Extension {
    fun Int.px(context: Context) = this * context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
}
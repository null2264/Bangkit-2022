package io.github.null2264.dicodingstories.lib

import android.content.Context
import android.util.DisplayMetrics

object Extension {
    fun Int.px(context: Context) = this * context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
}
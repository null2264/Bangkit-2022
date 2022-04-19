package io.github.null2264.dicodingstories.widget.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.lib.Common
import io.github.null2264.dicodingstories.lib.Common.getDrawableWithAttrTint
import io.github.null2264.dicodingstories.lib.Extension.px

open class Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : AppCompatButton(context, attrs, defStyleAttr ?: android.R.attr.buttonStyle)
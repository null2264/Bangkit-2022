package io.github.null2264.dicodingstories.widget.base

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

open class Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : AppCompatButton(context, attrs, defStyleAttr ?: android.R.attr.buttonStyle)
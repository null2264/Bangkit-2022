package io.github.null2264.dicodingstories.widget.button

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.lib.Common.getDrawableWithAttrTint
import io.github.null2264.dicodingstories.lib.Extension.px
import io.github.null2264.dicodingstories.widget.base.Button

class TextButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {
    init {
        this.setTextColor(
            context.resources.getColor(R.color.textButtonColor, context.theme)
        )
        this.setTypeface(null, Typeface.BOLD)
    }
}
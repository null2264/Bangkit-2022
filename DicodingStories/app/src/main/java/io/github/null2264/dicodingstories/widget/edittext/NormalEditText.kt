package io.github.null2264.dicodingstories.widget.edittext

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.lib.Extension.px

class NormalEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : AppCompatEditText(context, attrs, defStyleAttr ?: android.R.attr.editTextStyle) {

    init {
        this.setPadding(
            paddingLeft + 16.px(context),
            paddingTop + 8.px(context),
            paddingRight + 16.px(context),
            paddingBottom + 8.px(context)
        )
        compoundDrawablePadding = 16.px(context)
        background = ContextCompat.getDrawable(context, R.drawable.bg_clickable_focusable)
    }
}
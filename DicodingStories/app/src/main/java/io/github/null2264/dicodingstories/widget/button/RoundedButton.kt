package io.github.null2264.dicodingstories.widget.button

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.lib.Common.getDrawableWithTint
import io.github.null2264.dicodingstories.lib.Extension.px
import io.github.null2264.dicodingstories.widget.base.Button

class RoundedButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : Button(context, attrs, defStyleAttr) {
    init {
        background = getDrawableWithTint(context, R.drawable.bg_clickable_focusable_ripple, R.color.button_color)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        this.setTextColor(typedValue.data)
        this.setPadding(
            paddingLeft + 16.px(context),
            paddingTop + 8.px(context),
            paddingRight + 16.px(context),
            paddingBottom + 8.px(context)
        )
    }
}
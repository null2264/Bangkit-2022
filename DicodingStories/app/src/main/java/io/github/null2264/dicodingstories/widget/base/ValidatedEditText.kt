package io.github.null2264.dicodingstories.widget.base

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.lib.Extension.px

abstract class ValidatedEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : TextInputLayout(context, attrs, defStyleAttr ?: com.google.android.material.R.attr.textInputStyle) {
    private var _editText: AppCompatEditText
    val editText get() = _editText

    var text
        get() = _editText.text
        set(newValue) {
            _editText.text = newValue
        }

    init {
        errorIconDrawable = null // overlapping other icon
        boxStrokeWidth = 0
        boxStrokeWidthFocused = 0

        this.setWillNotDraw(false)
        _editText = TextInputEditText(context)
        _editText.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setPadding(
                paddingLeft + 16.px(context),
                paddingTop + 8.px(context),
                paddingRight + 16.px(context),
                paddingBottom + 8.px(context)
            )
            compoundDrawablePadding = 16.px(context)
            background = ContextCompat.getDrawable(context, R.drawable.bg_clickable_focusable)
            this.addTextChangedListener { if (error != null || isErrorEnabled) clearError() }
        }
        this.addView(_editText)
        this.isHintEnabled = false
    }

    private fun clearError() {
        error = null
        isErrorEnabled = false
    }

    abstract fun validateInput(): Boolean
}
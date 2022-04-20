package io.github.null2264.dicodingstories.widget.edittext

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.util.AttributeSet
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.widget.base.ValidatedEditText

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : ValidatedEditText(context, attrs, defStyleAttr) {
    init {
        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        editText.hint = context.getString(R.string.email)
    }

    override fun validateInput(): Boolean {
        if (text.isNullOrEmpty()) {
            error = context.getString(R.string.empty_email)
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
            error = context.getString(R.string.invalid_email)
            return false
        }

        return true
    }
}
package io.github.null2264.dicodingstories.widget.edittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.widget.base.ValidatedEditText

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : ValidatedEditText(context, attrs, defStyleAttr) {
    private val minLength = 6

    init {
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        endIconMode = END_ICON_PASSWORD_TOGGLE
        endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_toggle) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        editText.hint = context.getString(R.string.password)
    }

    override fun validateInput(): Boolean {
        if ((text?.length ?: 0) < minLength) {
            error = context.getString(R.string.invalid_password)
            return false
        }
        return true
    }
}
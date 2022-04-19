package io.github.null2264.dicodingstories.widget.edittext

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.widget.base.ValidatedEditText

class NotEmptyEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int? = null) : ValidatedEditText(context, attrs, defStyleAttr) {
    private var errorMessage: String? = null
    @StringRes
    private var errorMessageId: Int = 0

    init {
        if (attrs != null) {
            val attr = context.theme.obtainStyledAttributes(attrs, R.styleable.NotEmptyEditText, 0, 0)
            errorMessageId = attr.getResourceId(R.styleable.NotEmptyEditText_errorMessage, 0)
            errorMessage = attr.getString(R.styleable.NotEmptyEditText_errorMessage)
        }
    }

    override fun validateInput(): Boolean {
        if (text.isNullOrEmpty()) {
            var msg = errorMessage ?: "Can't be empty"
            if (errorMessageId != 0)
                msg = context.getString(errorMessageId)
            this.error = msg
            return false
        }

        return true
    }
}
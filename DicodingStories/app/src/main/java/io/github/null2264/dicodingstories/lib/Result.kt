package io.github.null2264.dicodingstories.lib

import androidx.annotation.StringRes

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(@StringRes val stringId: Int) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
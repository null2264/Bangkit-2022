package io.github.null2264.githubuser.data.common

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val stringRes: Int, val value: String?) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
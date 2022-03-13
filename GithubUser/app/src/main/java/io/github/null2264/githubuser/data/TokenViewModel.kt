package io.github.null2264.githubuser.data

import androidx.lifecycle.ViewModel

open class TokenViewModel(private val token: String) : ViewModel() {
    fun getToken(): String = token
}
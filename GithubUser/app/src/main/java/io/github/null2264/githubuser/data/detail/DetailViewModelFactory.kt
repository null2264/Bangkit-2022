package io.github.null2264.githubuser.data.detail

import androidx.lifecycle.ViewModel
import io.github.null2264.githubuser.data.TokenViewModelFactory
import io.github.null2264.githubuser.lib.User

class DetailViewModelFactory(val token: String, private val user: User) : TokenViewModelFactory(token) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java, User::class.java).newInstance(token, user)
    }
}
package io.github.null2264.githubuser.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.null2264.githubuser.lib.User

open class TokenViewModelFactory(private val token: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (TokenViewModel::class.java.isAssignableFrom(modelClass))
            modelClass.getConstructor(String::class.java).newInstance(token)
        else
            super.create(modelClass)
    }
}
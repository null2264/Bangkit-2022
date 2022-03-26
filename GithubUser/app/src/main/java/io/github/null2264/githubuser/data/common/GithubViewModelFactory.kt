package io.github.null2264.githubuser.data.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class GithubViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (GithubViewModel::class.java.isAssignableFrom(modelClass))
            modelClass.getConstructor(AppRepository::class.java).newInstance(appRepository)
        else
            super.create(modelClass)
    }
}
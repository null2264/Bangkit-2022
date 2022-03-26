package io.github.null2264.githubuser.data.common

import androidx.lifecycle.ViewModel

open class GithubViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun getRepository(): AppRepository = appRepository
}
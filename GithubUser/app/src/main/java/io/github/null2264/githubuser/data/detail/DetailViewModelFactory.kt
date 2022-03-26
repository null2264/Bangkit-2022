package io.github.null2264.githubuser.data.detail

import androidx.lifecycle.ViewModel
import io.github.null2264.githubuser.data.common.AppRepository
import io.github.null2264.githubuser.data.common.GithubViewModelFactory
import io.github.null2264.githubuser.data.database.entity.UserEntity

class DetailViewModelFactory(
    val appRepository: AppRepository,
    private val user: UserEntity,
) : GithubViewModelFactory(appRepository) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AppRepository::class.java, UserEntity::class.java)
            .newInstance(appRepository, user)
    }
}
package io.github.null2264.githubuser.lib

import android.content.Context
import io.github.null2264.githubuser.data.common.AppRepository
import io.github.null2264.githubuser.data.database.AppDatabase
import io.github.null2264.githubuser.lib.api.Apollo

object ActivityInjection {
    fun provideRepository(ctx: Context, token: Token): AppRepository {
        val apollo = Apollo.getInstance(token)
        val database = AppDatabase.getInstance(ctx)
        val dao = database.userDao()
        return AppRepository.getInstance(apollo, dao)
    }
}
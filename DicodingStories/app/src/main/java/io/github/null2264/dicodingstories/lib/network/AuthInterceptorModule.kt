package io.github.null2264.dicodingstories.lib.network

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthInterceptorModule {
    @Binds
    abstract fun bindAuthInterceptor(authInterceptor: AuthInterceptor): Interceptor
}
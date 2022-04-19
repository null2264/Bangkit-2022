package io.github.null2264.dicodingstories.lib.network

import io.github.null2264.dicodingstories.data.preference.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val prefs: PreferencesHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getToken() ?: ""
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
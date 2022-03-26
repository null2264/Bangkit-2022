package io.github.null2264.githubuser.lib.api

import io.github.null2264.githubuser.BuildConfig
import io.github.null2264.githubuser.lib.Common
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OAuthConfig {
    companion object {
        fun retrofitClient(): OAuthClient {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(
                    if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
                )
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(Common.GITHUB_BASE_URL + "login/oauth/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(OAuthClient::class.java)
        }
    }
}
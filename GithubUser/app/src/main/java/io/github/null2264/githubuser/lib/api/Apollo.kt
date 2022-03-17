package io.github.null2264.githubuser.lib.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class Apollo {
    companion object {
        @Volatile
        var instance: ApolloClient? = null

        @Volatile
        var currentToken: String? = null

        private class AuthorizationInterceptor(val token: String) : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()

                return chain.proceed(request)
            }
        }

        @JvmStatic
        fun getInstance(token: String): ApolloClient {
            if (instance != null && token == currentToken) {
                return instance!!
            }

            currentToken = token

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(token))
                .build()

            instance = ApolloClient.Builder()
                .serverUrl("https://api.github.com/graphql")
                .webSocketServerUrl("wss://api.github.com/graphql")
                .okHttpClient(okHttpClient)
                .build()

            return instance!!
        }
    }
}
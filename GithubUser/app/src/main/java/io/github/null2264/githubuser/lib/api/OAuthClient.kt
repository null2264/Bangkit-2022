package io.github.null2264.githubuser.lib.api

import io.github.null2264.githubuser.lib.Token
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface OAuthClient {
    @Headers("Accept: application/json")
    @POST("access_token")
    @FormUrlEncoded
    fun getToken(
        @Field("client_id")
        clientId: String,
        @Field("client_secret")
        clientSecret: String,
        @Field("code")
        code: String,
    ): Call<Token>
}
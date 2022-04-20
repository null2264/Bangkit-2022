package io.github.null2264.dicodingstories.data.api

import io.github.null2264.dicodingstories.data.model.CommonResponse
import io.github.null2264.dicodingstories.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun register(
        @Body data: String,
    ): Response<CommonResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun login(
        @Body data: String,
    ): Response<LoginResponse>
}
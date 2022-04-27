package io.github.null2264.dicodingstories.data.api

import io.github.null2264.dicodingstories.data.model.CommonResponse
import io.github.null2264.dicodingstories.data.model.Stories
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    suspend fun getStories(): Response<Stories>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int = 15,
        @Query("location") locationOnly: Int = 0,
    ): Response<Stories>

    @Multipart
    @POST("stories")
    suspend fun newStory(
        @Part file: MultipartBody.Part,
        @Part("description") desc: String,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null,
    ): Response<CommonResponse>
}
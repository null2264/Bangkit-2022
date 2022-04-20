package io.github.null2264.dicodingstories.data.api

import io.github.null2264.dicodingstories.data.model.CommonResponse
import io.github.null2264.dicodingstories.data.model.LoginResponse
import io.github.null2264.dicodingstories.data.model.Stories
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface ApiService {
    @GET("stories")
    suspend fun getStories(): Response<Stories>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int = 15
    ): Response<Stories>

    @Multipart
    @POST("stories")
    suspend fun newStory(
        @Part file: MultipartBody.Part,
        @Part("description") desc: String
    ): Response<CommonResponse>
}
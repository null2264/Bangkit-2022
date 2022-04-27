package io.github.null2264.dicodingstories.data.api

import io.github.null2264.dicodingstories.data.helper.DataDummy
import io.github.null2264.dicodingstories.data.model.CommonResponse
import io.github.null2264.dicodingstories.data.model.Stories
import okhttp3.MultipartBody
import retrofit2.Response

class FakeApiService : ApiService {
    override suspend fun getStories(): Response<Stories> {
        return DataDummy.generateDummyStoriesResponse()
    }

    override suspend fun getStories(page: Int, size: Int, locationOnly: Int): Response<Stories> {
        return DataDummy.generateDummyStoriesResponse(size)
    }

    override suspend fun newStory(
        file: MultipartBody.Part,
        desc: String,
        lat: Double?,
        lon: Double?,
    ): Response<CommonResponse> {
        TODO("Not yet implemented")
    }
}
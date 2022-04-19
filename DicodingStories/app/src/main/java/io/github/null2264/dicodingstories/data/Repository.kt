package io.github.null2264.dicodingstories.data

import com.google.gson.JsonObject
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.data.model.CommonResponse
import io.github.null2264.dicodingstories.data.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import io.github.null2264.dicodingstories.lib.Result

class Repository(private val apiService: ApiService) {
    fun fetchStories(): Flow<Result<List<Story>>> = flow {
        emit(Result.Loading)

        val resp = apiService.getStories()
        if (resp.isSuccessful && resp.body() != null) {
            val body = resp.body()!!
            emit(Result.Success(body.listStory))
        } else {
            emit(Result.Error(R.string.stories_fail))
        }
    }
}
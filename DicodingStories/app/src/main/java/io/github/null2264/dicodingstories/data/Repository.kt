package io.github.null2264.dicodingstories.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.data.model.Story
import io.github.null2264.dicodingstories.data.story.StoryFilter
import io.github.null2264.dicodingstories.data.story.StoryPagingSource
import io.github.null2264.dicodingstories.lib.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    fun fetchPagedStories(filter: StoryFilter? = null): Flow<PagingData<Story>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { StoryPagingSource(apiService, filter) }
    ).flow

    companion object {
        const val PAGE_SIZE = 15
    }
}
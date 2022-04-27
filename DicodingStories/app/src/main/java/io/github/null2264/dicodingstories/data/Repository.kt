package io.github.null2264.dicodingstories.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.data.model.Story
import io.github.null2264.dicodingstories.data.story.StoryFilter
import io.github.null2264.dicodingstories.data.story.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class Repository(private val apiService: ApiService) {
    fun fetchStories(filter: StoryFilter? = null): Flow<PagingData<Story>> = Pager(
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
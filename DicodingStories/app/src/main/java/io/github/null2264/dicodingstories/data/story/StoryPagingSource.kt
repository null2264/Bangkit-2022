package io.github.null2264.dicodingstories.data.story

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.data.model.Story
import java.io.IOException

class StoryPagingSource(
    private val service: ApiService,
) : PagingSource<Int, Story>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val pos = params.key ?: INITIAL_PAGE_INDEX
        return try {
            val resp = service.getStories(pos, params.loadSize)
            val repos = resp.body()!!.listStory
            val nextKey = if (repos.isEmpty()) null else pos + 1

            LoadResult.Page(
                data = repos,
                prevKey = if (pos == INITIAL_PAGE_INDEX) null else pos - 1,
                nextKey = nextKey
            )
        } catch (exc: Exception) {
            LoadResult.Error(exc)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
package io.github.null2264.dicodingstories.data

import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.github.null2264.dicodingstories.data.api.ApiService
import io.github.null2264.dicodingstories.data.api.FakeApiService
import io.github.null2264.dicodingstories.data.helper.DataDummy.generateDummyStories
import io.github.null2264.dicodingstories.data.helper.InstantTaskExecutorExtension
import io.github.null2264.dicodingstories.data.helper.Util
import io.github.null2264.dicodingstories.data.story.StoryFilter
import io.github.null2264.dicodingstories.data.story.StoryPagingSource
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class
)
class RepositoryTest {
    @MockK
    private lateinit var repo: Repository
    private val apiService: ApiService = FakeApiService()
    private val dummyStories = generateDummyStories(2)

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("when Paging Source load page successfully")
    fun pagingSourceLoaded() = testScope.runTest {
        val pagingSource = StoryPagingSource(apiService, StoryFilter())
        assertEquals(
            PagingSource.LoadResult.Page(
                data = dummyStories,
                prevKey = null,
                nextKey = 2,
            ),
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    @DisplayName("when Stories Fetched successfully")
    fun storiesFetched() = testScope.runTest {
        val expectedData = generateDummyStories(15)
        val mockedData = flowOf(PagingData.from(expectedData))
        every { repo.fetchStories() } returns mockedData

        val differ = Util.setupDiffer()

        val repoRes = repo.fetchStories()
        repoRes.collect { differ.submitData(it) }

        advanceUntilIdle()

        verify { repo.fetchStories() }
        confirmVerified(repo)
        assertNotNull(differ.snapshot())
        assertEquals(expectedData.size, differ.snapshot().size)
        assertEquals(expectedData[0].id, differ.snapshot()[0]?.id)
    }
}
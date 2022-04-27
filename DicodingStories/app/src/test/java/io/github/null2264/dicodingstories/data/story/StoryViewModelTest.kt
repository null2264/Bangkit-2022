package io.github.null2264.dicodingstories.data.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.filter
import io.github.null2264.dicodingstories.data.helper.DataDummy.generateDummyStories
import io.github.null2264.dicodingstories.data.helper.InstantTaskExecutorExtension
import io.github.null2264.dicodingstories.data.helper.Util.getOrAwaitValue
import io.github.null2264.dicodingstories.data.helper.Util.setupDiffer
import io.github.null2264.dicodingstories.data.model.Story
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class
)
class StoryViewModelTest {
    @MockK
    private lateinit var viewModel: StoryViewModel
    private val dummyStories = generateDummyStories()

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
    @DisplayName("when Get Stories should not null")
    fun storiesNotNull() = testScope.runTest {
        val expectedData = dummyStories
        val data: LiveData<PagingData<Story>> = MutableLiveData(PagingData.from(expectedData))
        every { viewModel.stories } returns data

        val actualData = viewModel.stories.getOrAwaitValue()

        val differ = setupDiffer()
        differ.submitData(actualData)

        advanceUntilIdle()

        verify { viewModel.stories }
        confirmVerified(viewModel)
        assertNotNull(differ.snapshot())
        assertEquals(expectedData.size, differ.snapshot().size)
        assertEquals(expectedData[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when Stories updated on change filter and not null`() = testScope.runTest {
        val expectedData = dummyStories
        val changedData = generateDummyStories(5, 420.69f, 69.420f)
        val mutableData = MutableLiveData(PagingData.from(expectedData))
        val data: LiveData<PagingData<Story>> = mutableData

        val newState = StoryFilter(true)
        every { viewModel.setFilterState(newState) } answers { mutableData.value = PagingData.from(changedData) }

        every { viewModel.stories } returns data

        val differ = setupDiffer()

        viewModel.setFilterState(newState)
        val actualData = viewModel.stories.getOrAwaitValue().filter { it.lon != null && it.lat != null }

        differ.submitData(actualData)

        advanceUntilIdle()

        verify { viewModel.setFilterState(newState) }
        verify { viewModel.stories }
        confirmVerified(viewModel)
        assertNotNull(differ.snapshot())
        assertNotNull(differ.snapshot()[0]?.lon)
        assertNotNull(differ.snapshot()[0]?.lat)

        assertNotEquals(expectedData.size, differ.snapshot().size)
        assertEquals(changedData.size, differ.snapshot().size)

        assertNotEquals(expectedData[expectedData.lastIndex].id, differ.snapshot()[differ.snapshot().lastIndex]?.id)
        assertEquals(changedData[changedData.lastIndex].id, differ.snapshot()[differ.snapshot().lastIndex]?.id)
    }
}
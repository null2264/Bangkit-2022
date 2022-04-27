package io.github.null2264.dicodingstories.ui.story

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.helper.Util
import io.github.null2264.dicodingstories.lib.Common
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class DashboardFragmentTest {
    private val mockWebServer = MockWebServer()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            Common.API_URL = "http://127.0.0.1:8080"
        }
    }

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun launchDashboardFragment() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val navController = TestNavHostController(ctx)

        Util.launchFragmentInHiltContainer<DashboardFragment> {
            navController.setGraph(R.navigation.main_navigation)
            navController.setCurrentDestination(R.id.dashboardFragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun storiesFetch_Success() {
        launchDashboardFragment()

        val mockResp = MockResponse()
            .setResponseCode(200)
            .setBody(Util.stringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResp)

        onView(withId(R.id.rv_stories_container))
            .check(matches(isDisplayed()))


        onView(withText("hehehe"))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv_stories_container))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("a"))
                )
            )
    }

    @Test
    fun storiesFetch_Error() {
        launchDashboardFragment()

        val mockResp = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResp)

        onView(withId(R.id.tv_error))
            .check(matches(isDisplayed()))

        onView(withText("Oops! Something went wrong."))
            .check(matches(isDisplayed()))
    }
}
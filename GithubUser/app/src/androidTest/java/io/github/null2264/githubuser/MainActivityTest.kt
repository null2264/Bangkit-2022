package io.github.null2264.githubuser

import android.content.res.Resources
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import io.github.null2264.githubuser.ui.main.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    class QuerySearchView(private val query: String, private val submit: Boolean) : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
        }

        override fun getDescription(): String {
            return "Query search view text"
        }

        override fun perform(uiController: UiController, view: View) {
            (view as SearchView).setQuery(query, submit)
        }
    }

    class RecyclerViewMatcher(private val recyclerViewId: Int) {
        fun atPosition(position: Int): Matcher<View> = atPositionOnView(position, -1)

        fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                var childView: View? = null
                var resources: Resources? = null

                override fun describeTo(description: Description) {
                    var descId = recyclerViewId.toString()
                    if (resources != null) {
                        descId = try {
                            resources!!.getResourceName(recyclerViewId)
                        } catch (exc: Resources.NotFoundException) {
                            "${Integer.valueOf(recyclerViewId)} (resource name not found)"
                        }
                    }

                    description.appendText("with id: $descId")
                }

                override fun matchesSafely(view: View): Boolean {
                    this.resources = view.resources

                    if (childView == null) {
                        val recyclerView: RecyclerView? = view.rootView.findViewById(recyclerViewId)
                        if (recyclerView != null && recyclerView.id == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                        } else
                            return false
                    }

                    return if (targetViewId == -1)
                        view == childView
                    else
                        view == childView?.findViewById(targetViewId)
                }
            }
        }
    }

    private fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher = RecyclerViewMatcher(recyclerViewId)

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    @Test
    fun testSearchViewExists() {
        onView(withId(R.id.nav_main_explore_fragment)).perform(click())
        onView(withId(R.id.sv_main)).perform(QuerySearchView("null2264", true))
        onView(isRoot()).perform(waitFor(1500L))
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).check(ViewAssertions.matches(
            isDisplayed()))
    }

    @Test
    fun testSearchViewNotExists() {
        onView(withId(R.id.nav_main_explore_fragment)).perform(click())
        onView(withId(R.id.sv_main)).perform(QuerySearchView(
            "User that shouldn't exists hrdjrdjr some junk text just in case", true))
        onView(isRoot()).perform(waitFor(1500L))
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).check(ViewAssertions.matches(
            not(isDisplayed())))
    }

    @Test
    fun testFavorite() {
        onView(withId(R.id.nav_main_explore_fragment)).perform(click())
        onView(withId(R.id.sv_main)).perform(QuerySearchView("null2264", true))
        onView(isRoot()).perform(waitFor(1500L))
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).check(ViewAssertions.matches(
            isDisplayed()))
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).perform(click())
        onView(withId(R.id.fab_favorite)).perform(click())
        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.nav_main_favorites_fragment)).perform(click())
        // check if user actually added to the list
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).check(ViewAssertions.matches(
            isDisplayed()))
        // revert changes so it'll not interrupt next tests
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).perform(click())
        onView(withId(R.id.fab_favorite)).perform(click())
        onView(isRoot()).perform(pressBack())
        // check if actions successfully reverted
        onView(withRecyclerView(R.id.rv_users).atPositionOnView(0, R.id.tv_item_follower)).check(ViewAssertions.matches(
            not(isDisplayed())))
    }
}
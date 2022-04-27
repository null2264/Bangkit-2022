package io.github.null2264.dicodingstories.data.helper

import io.github.null2264.dicodingstories.data.model.Stories
import io.github.null2264.dicodingstories.data.model.Story
import retrofit2.Response

object DataDummy {
    fun generateDummyStory(count: Int, lon: Float? = null, lat: Float? = null) =
        Story(
            "story-$count",
            "User$count",
            "yep",
            "https://testing.test/$count",
            "2022-03-14T00:00:00Z",
            lon,
            lat,
        )

    fun generateDummyStories(range: Int = 10, lon: Float? = null, lat: Float? = null): List<Story> {
        val storiesList = ArrayList<Story>()
        for (i in 0..range)
            storiesList.add(
                generateDummyStory(i, lon, lat)
            )
        return storiesList
    }

    fun generateDummyStoriesResponse(range: Int = 10): Response<Stories> {
        return Response.success(Stories(false, "YEET", generateDummyStories(range)))
    }
}
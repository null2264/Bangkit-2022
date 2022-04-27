package io.github.null2264.dicodingstories.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Stories(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>,
)

@Parcelize
data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lon: Float?,
    val lat: Float?,
) : Parcelable
package io.github.null2264.githubuser.lib

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val name: String?,
    val avatar: String,
    val repository: Int,
    val followers: Int,
    val following: Int,
    val company: String?,
    val location: String?,
) : Parcelable
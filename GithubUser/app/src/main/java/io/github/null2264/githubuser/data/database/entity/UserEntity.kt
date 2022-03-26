package io.github.null2264.githubuser.data.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "user",
    indices = [Index(name = "userId", value = ["id"], unique = true)]
)
data class UserEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = false)
    val id: Int,

    @field:ColumnInfo(name = "login")
    val login: String,

    @field:ColumnInfo(name = "name")
    val name: String?,

    @field:ColumnInfo(name = "avatarUrl")
    val avatarUrl: String,

    @field:ColumnInfo(name = "followers")
    val followers: Int,

    @field:ColumnInfo(name = "following")
    val following: Int,

    @field:ColumnInfo(name = "company")
    val company: String?,

    @field:ColumnInfo(name = "location")
    val location: String?,

    @field:ColumnInfo(name = "repository")
    val repository: Int,
) : Parcelable
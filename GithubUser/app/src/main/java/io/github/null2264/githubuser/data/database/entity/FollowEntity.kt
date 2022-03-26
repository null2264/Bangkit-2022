package io.github.null2264.githubuser.data.database.entity

import androidx.room.*

@Entity(
    tableName = "follow",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["followerId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index(name = "id", value = ["userId", "followerId"], unique = true)]
)
data class FollowEntity(
    @field:PrimaryKey(autoGenerate = true)
    val id: Int,
    @field:ColumnInfo(name = "userId", index = true)
    val userId: Int,
    @field:ColumnInfo(name = "followerId", index = true)
    val followerId: Int,
)
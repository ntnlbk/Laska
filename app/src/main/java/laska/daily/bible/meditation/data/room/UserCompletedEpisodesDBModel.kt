package laska.daily.bible.meditation.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_episodes_activity_table")
data class UserCompletedEpisodesDBModel(
    @PrimaryKey
    val episodeId: Int
)

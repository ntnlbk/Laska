package laska.daily.bible.meditation.data.room.coursescache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_cache")
data class ApiCacheEntity(
    @PrimaryKey
    val endpointKey: String,
    val jsonResponse: String,
    val timestamp: Long
)

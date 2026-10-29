package laska.daily.bible.meditation.data.room.coursescache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApiCacheDao {

    @Query("SELECT * FROM api_cache WHERE endpointKey = :endpointKey")
    suspend fun getCache(endpointKey: String): ApiCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(apiCacheEntity: ApiCacheEntity)

}
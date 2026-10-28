package laska.daily.bible.meditation.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserCompletedEpisodesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCompletedEpisode(userCompletedEpisode: UserCompletedEpisodesDBModel)

    @Query("SELECT * FROM users_episodes_activity_table")
    suspend fun getAllUserCompletedEpisodes(): List<UserCompletedEpisodesDBModel>

}
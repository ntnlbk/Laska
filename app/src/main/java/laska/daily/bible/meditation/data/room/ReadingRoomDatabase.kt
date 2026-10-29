package laska.daily.bible.meditation.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import laska.daily.bible.meditation.data.room.coursescache.ApiCacheDao
import laska.daily.bible.meditation.data.room.coursescache.ApiCacheEntity

@Database(
    entities = [ReadingDBModel::class, UserCompletedEpisodesDBModel::class, ApiCacheEntity::class],
    version = 2
)
abstract class ReadingRoomDatabase : RoomDatabase() {
    abstract fun readingDao(): ReadingDao

    abstract fun userCompletedEpisodesDao(): UserCompletedEpisodesDAO
    abstract fun apiCacheDao(): ApiCacheDao

    companion object {
        @Volatile
        private var INSTANCE: ReadingRoomDatabase? = null

        fun getDatabase(context: Context): ReadingRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReadingRoomDatabase::class.java,
                    "readings_db"
                ).fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
package mobi.laska.daily.bible.meditation.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReadingDBModel::class], version = 1)
abstract class ReadingRoomDatabase: RoomDatabase() {
    abstract fun readingDao(): ReadingDao

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
                )
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
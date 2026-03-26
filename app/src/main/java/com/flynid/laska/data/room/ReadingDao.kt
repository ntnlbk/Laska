package com.flynid.laska.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings_table WHERE date=:date AND language=:language LIMIT 1")
    suspend fun getReadingByDateAndLanguage(date: String, language: String): ReadingDBModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(vararg readings: ReadingDBModel)

}
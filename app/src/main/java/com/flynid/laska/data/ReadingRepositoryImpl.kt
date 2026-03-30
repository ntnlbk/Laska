package com.flynid.laska.data

import android.app.Application
import android.util.Log
import com.flynid.laska.data.mappers.LanguageMapper
import com.flynid.laska.data.mappers.DbModelMapper
import com.flynid.laska.data.room.ReadingDBModel
import com.flynid.laska.data.room.ReadingRoomDatabase
import com.flynid.laska.domain.Language
import com.flynid.laska.domain.ReadingItem
import com.flynid.laska.domain.ReadingRepository
import javax.inject.Inject

class ReadingRepositoryImpl @Inject constructor(
    private val application: Application,
    private val dbMapper: DbModelMapper,
    private val languageMapper: LanguageMapper
): ReadingRepository {

    override suspend fun getReading(
        date: String,
        language: Language
    ): ReadingItem {

        val dbDao = ReadingRoomDatabase.getDatabase(application).readingDao()

        val languageString = languageMapper.mapLanguageToString(language)



        return TODO("")

    }

}
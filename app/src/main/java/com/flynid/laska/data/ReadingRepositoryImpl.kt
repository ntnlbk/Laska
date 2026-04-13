package com.flynid.laska.data

import android.app.Application
import com.flynid.laska.data.mappers.DbModelMapper
import com.flynid.laska.data.mappers.LanguageMapper
import com.flynid.laska.data.retrofit.LaskaApiService
import com.flynid.laska.data.room.ReadingRoomDatabase
import com.flynid.laska.domain.Language
import com.flynid.laska.domain.ReadingItem
import com.flynid.laska.domain.ReadingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingRepositoryImpl @Inject constructor(
    private val application: Application,
    private val dbMapper: DbModelMapper,
    private val languageMapper: LanguageMapper
) : ReadingRepository {

    override suspend fun getReading(
        date: String,
        language: Language
    ): ReadingItem {

        val dbDao = ReadingRoomDatabase.getDatabase(application).readingDao()
        val languageString = languageMapper.mapLanguageToString(language)

        val readingFromDB = dbDao.getReadingByDateAndLanguage(
            date, languageString
        )

        if (readingFromDB == null) {
            val readingsFromApi = LaskaApiService.LaskaApi.retrofitService.getReadings(date)
            readingsFromApi.data.forEach {
                dbDao.insertReadings(
                    dbMapper.mapDtoToDbModel(it)
                )
            }
            val readingFromApiReadings =
                dbDao.getReadingByDateAndLanguage(
                    date, languageString
                )
            if (readingFromApiReadings == null) {
                throw Exception("Reading is null")
            } else {
                return dbMapper.mapDbModelToDataModel(readingFromApiReadings)
            }

        } else {
            return dbMapper.mapDbModelToDataModel(readingFromDB)
        }

    }
}

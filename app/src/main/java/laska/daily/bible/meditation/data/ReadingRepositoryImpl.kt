package laska.daily.bible.meditation.data

import android.app.Application
import laska.daily.bible.meditation.data.mappers.DbModelMapper
import laska.daily.bible.meditation.data.mappers.LanguageMapper
import laska.daily.bible.meditation.data.retrofit.LaskaApiService
import laska.daily.bible.meditation.data.room.ReadingRoomDatabase
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.ReadingItem
import laska.daily.bible.meditation.domain.ReadingRepository
import laska.daily.bible.meditation.presentation.uils.DateUtils.Companion.plusSevenDays
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
            val readingsFromApi = LaskaApiService.LaskaApi.retrofitService.getReadings(date, plusSevenDays(date), lang = languageString)
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

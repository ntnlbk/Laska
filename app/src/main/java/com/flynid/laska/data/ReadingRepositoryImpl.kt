package com.flynid.laska.data

import android.app.Application
import android.util.Log
import com.flynid.laska.data.mappers.LanguageToDbStringMapper
import com.flynid.laska.data.mappers.ReadingDbModelToDataMapper
import com.flynid.laska.data.room.ReadingDBModel
import com.flynid.laska.data.room.ReadingRoomDatabase
import com.flynid.laska.di.LaskaApplication
import com.flynid.laska.domain.Language
import com.flynid.laska.domain.ReadingItem
import com.flynid.laska.domain.ReadingRepository

class ReadingRepositoryImpl (
    private val application: Application
): ReadingRepository {

    override suspend fun getReading(
        date: String,
        language: Language
    ): ReadingItem {

        val dbDao = ReadingRoomDatabase.getDatabase(application).readingDao()

        val languageMapper = LanguageToDbStringMapper(application)

        val languageString = languageMapper.mapLanguageToString(language)

        val readingItemFromDb = dbDao.getReadingByDateAndLanguage(date, languageString)

        val readingItemDbMapper = ReadingDbModelToDataMapper(application)

        val readingItem =  if(readingItemFromDb != null){
            Log.d("TEST", "FROM DB")
            readingItemDbMapper.mapDbModelToDataModel(readingItemFromDb)

        }
        else {
            //get from API
            Log.d("TEST", "FROM API")
            dbDao.insertReadings(
                ReadingDBModel(
                    date = date,
                    dateFormatted = "da",
                    language = "RU",
                    bibleReference = "eq",
                    bibleText = "weqe",
                    bibleTextPlain = "qwe",
                    feastName = "ewqe",
                    reflectionTextFirst = "i am from db",
                    reflectionTextSecond = "dsa",
                    authorName = "ds",
                    audioURL = "sd",
                    imageURL = "dsa",
                    audioLocalPath = "sda",
                    imageLocalPath = "ad",
                    permalink = "sad"
                )
            )
            ReadingItem(
                id = 1,
                date = date,
                dateFormatted = "1",
                language = Language.RU,
                bibleReference = "2",
                bibleText = "3",
                bibleTextPlain = "4",
                feastName = "5",
                reflectionTextFirst = "Anton HIIII",
                reflectionTextSecond = "12",
                authorName = "32",
                audioURL = "123",
                imageURL = "23",
                audioLocalPath = "123",
                imageLocalPath = "321",
                permalink = "132"
            )
        }

        return readingItem

    }

}
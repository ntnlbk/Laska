package com.flynid.laska.data.mappers

import android.app.Application
import com.flynid.laska.data.room.ReadingDBModel
import com.flynid.laska.domain.ReadingItem

class ReadingDbModelToDataMapper(
    private val context: Application
) {

    fun mapDbModelToDataModel(readingDBModel: ReadingDBModel): ReadingItem {
        return ReadingItem(
            id = readingDBModel.id,
            date = readingDBModel.date,
            dateFormatted = readingDBModel.dateFormatted,
            language = LanguageToDbStringMapper(context).mapStringToLanguage(readingDBModel.language),
            bibleReference = readingDBModel.bibleReference,
            bibleText = readingDBModel.bibleText,
            bibleTextPlain = readingDBModel.bibleTextPlain,
            feastName = readingDBModel.feastName,
            reflectionTextFirst = readingDBModel.reflectionTextFirst,
            reflectionTextSecond = readingDBModel.reflectionTextSecond,
            authorName = readingDBModel.authorName,
            audioURL = readingDBModel.audioURL,
            imageURL = readingDBModel.imageURL,
            audioLocalPath = readingDBModel.audioLocalPath,
            imageLocalPath = readingDBModel.imageLocalPath,
            permalink = readingDBModel.permalink
        )
    }
}
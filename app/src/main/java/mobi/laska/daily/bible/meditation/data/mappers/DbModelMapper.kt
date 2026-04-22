package mobi.laska.daily.bible.meditation.data.mappers

import android.app.Application
import mobi.laska.daily.bible.meditation.data.retrofit.ReadingDTO
import mobi.laska.daily.bible.meditation.data.room.ReadingDBModel
import mobi.laska.daily.bible.meditation.domain.ReadingItem
import javax.inject.Inject

class DbModelMapper @Inject constructor(
    private val context: Application
) {

    fun mapDbModelToDataModel(readingDBModel: ReadingDBModel): ReadingItem {
        return ReadingItem(
            id = readingDBModel.id,
            date = readingDBModel.date,
            dateFormatted = readingDBModel.dateFormatted,
            language = LanguageMapper(context).mapStringToLanguage(readingDBModel.language),
            bibleReference = readingDBModel.bibleReference,
            bibleText = readingDBModel.bibleText,
            bibleTextPlain = readingDBModel.bibleTextPlain,
            feastName = readingDBModel.feastName,
            reflectionTextIntro = readingDBModel.reflectionTextIntro,
            reflectionTextBody = readingDBModel.reflectionTextBody,
            authorName = readingDBModel.authorName,
            audioURL = readingDBModel.audioURL,
            imageURL = readingDBModel.imageURL,
            audioLocalPath = readingDBModel.audioLocalPath,
            imageLocalPath = readingDBModel.imageLocalPath,
            permalink = readingDBModel.permalink
        )
    }
    
    fun mapDtoToDbModel(readingDTO: ReadingDTO): ReadingDBModel{
        return ReadingDBModel(
            id = readingDTO.id,
            date = readingDTO.date,
            dateFormatted = readingDTO.dateFormatted,
            language = readingDTO.language,
            bibleReference = readingDTO.bibleReference ?: EMPTY_STRING,
            bibleText = readingDTO.bibleText ?: EMPTY_STRING,
            bibleTextPlain = readingDTO.bibleText ?: EMPTY_STRING,
            feastName = readingDTO.feastName ?: EMPTY_STRING,
            reflectionTextIntro = readingDTO.reflectionIntro ?: EMPTY_STRING,
            reflectionTextBody = readingDTO.reflectionBody ?: EMPTY_STRING,
            authorName = readingDTO.authorName ?: EMPTY_STRING,
            audioURL = readingDTO.audioFile ?: EMPTY_STRING,
            imageURL = readingDTO.mediaAsset ?: EMPTY_STRING,
            audioLocalPath = EMPTY_STRING,
            imageLocalPath = EMPTY_STRING,
            permalink = readingDTO.permalink ?: EMPTY_STRING
        )
    }

    companion object{
        private const val EMPTY_STRING = ""
    }
}
package mobi.laska.daily.bible.meditation.data.retrofit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadingDTO(
    val id: Int,
    val date: String,
    @SerialName("date_formatted")
    val dateFormatted: String,
    val language: String,
    @SerialName("bible_reference")
    val bibleReference: String?,
    @SerialName("bible_text")
    val bibleText: String?,
    @SerialName("bible_text_plain")
    val bibleTextPlain: String?,
    @SerialName("feast_name")
    val feastName: String?,
    @SerialName("reflection_intro")
    val reflectionIntro: String?,
    @SerialName("reflection_body")
    val reflectionBody: String?,
    @SerialName("has_reflection")
    val hasReflection: Boolean,

    //DEPRECATED, TODO("remove")
    @SerialName("reflection_text")
    val reflectionText: String?,

    @SerialName("author_name")
    val authorName: String?,
    @SerialName("audio_file")
    val audioFile: String?,
    @SerialName("media_asset")
    val mediaAsset: String?,
    val permalink: String?

)

package mobi.laska.daily.bible.meditation.presentation.mainfragment

import java.io.Serializable


data class TextsToShow(
    val bibleTextPlain: String = "",
    val feastName: String = "",
    val reflectionTextIntro: String = "",
    val reflectionTextBody: String = ""
): Serializable


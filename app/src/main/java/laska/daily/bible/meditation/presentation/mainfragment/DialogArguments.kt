package laska.daily.bible.meditation.presentation.mainfragment

import java.io.Serializable


data class DialogArguments(
    val bibleTextPlain: String = "",
    val bibleRef: String = "",
    val reflectionTextIntro: String = "",
    val reflectionTextBody: String = "",
    val songMaxProgress: Int = 0,
    val actualProgress: Int = 0,
    val date: String = "",
    val episodeId: Int = 0,
    val episodeNumber: Int = 0,
    val isLastEpisodeToComplete: Boolean = false,
    val courseTitle: String = ""
): Serializable


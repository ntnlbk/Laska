package laska.daily.bible.meditation.domain.courses

import laska.daily.bible.meditation.domain.Language

data class Course(
    val id: Int,
    val title: String,
    val language: Language,
    val homePlacement: HomePlacement,
    val shortDescription: String,
    val description: String,
    val imageURL: String,
    val authors: List<Author>,
    val episodeCount: Int,
    val episodeAvailableCount: Int
)
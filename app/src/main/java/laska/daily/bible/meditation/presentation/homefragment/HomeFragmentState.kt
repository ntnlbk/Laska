package laska.daily.bible.meditation.presentation.homefragment

import laska.daily.bible.meditation.domain.courses.Author

sealed class HomeFragmentState {
    object Progress : HomeFragmentState()
    data class Content(
        val date: String,
        val bibleReference: String,
        val featuredSectionTitle: String,
        val featuredCourseImageURL: String,
        val featuredCourseSessionsNumber: Int,
        val featuredCourseTitle: String,
        val featuredCourseDescription: String,
        val featuredCourseAuthors: List<Author>
    ) : HomeFragmentState()

    data class Error(val message: String) : HomeFragmentState()
}
package laska.daily.bible.meditation.presentation.coursefragment

import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.domain.courses.Episode

sealed class CourseFragmentState {
    object Progress : CourseFragmentState()
    data class Error(val message: String) : CourseFragmentState()
    data class Content(
        val course: Course,
        val episodes: List<Episode>
    ) : CourseFragmentState()
}
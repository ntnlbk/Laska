package laska.daily.bible.meditation.presentation.coursesmainfragment

import laska.daily.bible.meditation.domain.courses.Course

sealed class CoursesMainFragmentState {
    object Progress : CoursesMainFragmentState()
    data class Error(val message: String) : CoursesMainFragmentState()
    data class Content(
        val courses: List<Course>
    ) : CoursesMainFragmentState()
}
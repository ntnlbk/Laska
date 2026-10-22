package laska.daily.bible.meditation.domain.courses

import laska.daily.bible.meditation.domain.Language

interface CoursesRepository {
    suspend fun getCourseForHomeScreen(language: Language): Course
    suspend fun getCourses(language: Language): List<Course>
    suspend fun getEpisodesByCourseId(courseId: Int): List<Episode>
}
package laska.daily.bible.meditation.data

import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.domain.courses.CoursesRepository
import laska.daily.bible.meditation.domain.courses.Episode
import javax.inject.Inject

class CoursesRepositoryImpl @Inject constructor(): CoursesRepository {
    override suspend fun getCourseForHomeScreen(language: Language): Course {
        TODO("Not yet implemented")
    }

    override suspend fun getCourses(language: Language): List<Course> {
        TODO("Not yet implemented")
    }

    override suspend fun getEpisodesByCourseId(courseId: Int): List<Episode> {
        TODO("Not yet implemented")
    }
}
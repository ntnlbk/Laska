package laska.daily.bible.meditation.data

import laska.daily.bible.meditation.data.mappers.CoursesMapper
import laska.daily.bible.meditation.data.mappers.HomePlacementMapper
import laska.daily.bible.meditation.data.mappers.LanguageMapper
import laska.daily.bible.meditation.data.retrofit.courses.CoursesApiService
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.domain.courses.CoursesRepository
import laska.daily.bible.meditation.domain.courses.Episode
import laska.daily.bible.meditation.domain.courses.HomePlacement
import javax.inject.Inject

class CoursesRepositoryImpl @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val courseMapper: CoursesMapper,
    private val homePlacementMapper: HomePlacementMapper
) : CoursesRepository {
    override suspend fun getCourseForHomeScreen(language: Language): Course {

        val coursesFromApi = CoursesApiService.CoursesApi.retrofitService.getCourses()
        val featuredCourse =
            coursesFromApi.items.first {
                it.language == languageMapper.mapLanguageToString(language) && it.homePlacement == homePlacementMapper.mapHomePlacementToString(
                    HomePlacement.FEATURED
                )
            }
        return courseMapper.mapCourseShortDtoToCourseDataModel(featuredCourse)

    }


    override suspend fun getCourses(language: Language): List<Course> {

        TODO("Not yet implemented")

    }

    override suspend fun getEpisodesByCourseId(courseId: Int): List<Episode> {
        TODO("Not yet implemented")
    }
}
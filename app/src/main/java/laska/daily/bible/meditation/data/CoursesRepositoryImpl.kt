package laska.daily.bible.meditation.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
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
import javax.inject.Singleton

@Singleton
class CoursesRepositoryImpl @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val courseMapper: CoursesMapper,
    private val homePlacementMapper: HomePlacementMapper,
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

        val coursesFromApi = CoursesApiService.CoursesApi.retrofitService.getCourses()
        return coursesFromApi.items.
            filter {
                it.language == languageMapper.mapLanguageToString(language)
            }
            .map {
            courseMapper.mapCourseShortDtoToCourseDataModel(it)
        }

    }

    override suspend fun getEpisodesByCourseId(courseId: Int): List<Episode> {
        TODO("Not yet implemented")
    }

    override suspend fun getFeaturedSectionNameByLanguage(language: Language): String {
        val db = Firebase.firestore
        val featuredSectionNameDocument =
            db.collection("courses_android").document("featured_section_title").get().await()
        return when (language){
            Language.RU -> featuredSectionNameDocument.getString("value_ru") ?: "Новинка LASKA"
            Language.BY -> featuredSectionNameDocument.getString("value_by") ?: "Навінка LASKA"
        }
    }
}
package laska.daily.bible.meditation.data

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import laska.daily.bible.meditation.data.mappers.CoursesMapper
import laska.daily.bible.meditation.data.mappers.HomePlacementMapper
import laska.daily.bible.meditation.data.mappers.LanguageMapper
import laska.daily.bible.meditation.data.retrofit.courses.CoursesApiService
import laska.daily.bible.meditation.data.room.ReadingRoomDatabase
import laska.daily.bible.meditation.data.room.UserCompletedEpisodesDBModel
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.domain.courses.CoursesRepository
import laska.daily.bible.meditation.domain.courses.Episode
import laska.daily.bible.meditation.domain.courses.EpisodeState
import laska.daily.bible.meditation.domain.courses.HomePlacement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepositoryImpl @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val courseMapper: CoursesMapper,
    private val homePlacementMapper: HomePlacementMapper,
    private val application: Application,
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
        return coursesFromApi.items.filter {
            it.language == languageMapper.mapLanguageToString(language)
        }
            .map {
                courseMapper.mapCourseShortDtoToCourseDataModel(it)
            }

    }

    override suspend fun getEpisodesByCourseId(courseId: Int): List<Episode> {
        val episodesFromApi = CoursesApiService.CoursesApi.retrofitService.getEpisodes(courseId)
        val episodesAllAvailable = episodesFromApi.items.map {
            courseMapper.mapShortEpisodeDTOToEpisodeDataModel(it)
        }

        val dbDao = ReadingRoomDatabase.getDatabase(application).userCompletedEpisodesDao()
        val userCompletedEpisodes = dbDao.getAllUserCompletedEpisodes()

        val completedEpisodeIds = userCompletedEpisodes.map { it.episodeId }.toSet()

        val sortedEpisodes = episodesAllAvailable.sortedBy { it.number }

        val episodes = sortedEpisodes.mapIndexed { index, episode ->
            val isCompleted = completedEpisodeIds.contains(episode.id)

            val newState = when {
                isCompleted -> EpisodeState.COMPLETED

                index < 3 -> EpisodeState.AVAILABLE

                else -> {
                    val requiredEpisode = sortedEpisodes[index - 3]
                    if (completedEpisodeIds.contains(requiredEpisode.id)) {
                        EpisodeState.AVAILABLE
                    } else {
                        EpisodeState.LOCKED
                    }
                }
            }

            episode.copy(state = newState)
        }

        return episodes
    }

    override suspend fun getFeaturedSectionNameByLanguage(language: Language): String {
        val db = Firebase.firestore
        val featuredSectionNameDocument =
            db.collection("courses_android").document("featured_section_title").get().await()
        return when (language) {
            Language.RU -> featuredSectionNameDocument.getString("value_ru") ?: "Новинка LASKA"
            Language.BY -> featuredSectionNameDocument.getString("value_by") ?: "Навінка LASKA"
        }
    }

    override suspend fun getCourse(courseId: Int): Course {
        val courseFromApi = CoursesApiService.CoursesApi.retrofitService.getCourse(courseId)
        return courseMapper.mapCourseDTOToCourseDataModel(courseFromApi)
    }

    override suspend fun getEpisode(
        courseId: Int,
        episodeId: Int
    ): Episode {
        val episodeFromApi =
            CoursesApiService.CoursesApi.retrofitService.getEpisode(courseId, episodeId)
        return courseMapper.mapEpisodeDTOToEpisodeDataModel(episodeFromApi)
    }

    override suspend fun markEpisodeAsCompleted(episodeId: Int) {
        val dbDao = ReadingRoomDatabase.getDatabase(application).userCompletedEpisodesDao()
        dbDao.insertUserCompletedEpisode(UserCompletedEpisodesDBModel(episodeId))
    }
}
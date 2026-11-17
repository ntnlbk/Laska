package laska.daily.bible.meditation.data

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import laska.daily.bible.meditation.data.donations.courses.CoursesDonationPrefs
import laska.daily.bible.meditation.data.mappers.CoursesMapper
import laska.daily.bible.meditation.data.mappers.HomePlacementMapper
import laska.daily.bible.meditation.data.mappers.LanguageMapper
import laska.daily.bible.meditation.data.retrofit.courses.CoursesApiService
import laska.daily.bible.meditation.data.room.ReadingRoomDatabase
import laska.daily.bible.meditation.data.room.UserCompletedEpisodesDBModel
import laska.daily.bible.meditation.data.room.coursescache.ApiCacheEntity
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.Course
import laska.daily.bible.meditation.domain.courses.CoursesRepository
import laska.daily.bible.meditation.domain.courses.Episode
import laska.daily.bible.meditation.domain.courses.EpisodeState
import laska.daily.bible.meditation.domain.courses.HomePlacement
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class CoursesRepositoryImpl @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val courseMapper: CoursesMapper,
    private val homePlacementMapper: HomePlacementMapper,
    private val application: Application,
    private val gson: Gson,
    private val coursesDonationPrefs: CoursesDonationPrefs
) : CoursesRepository {

    private suspend inline fun <reified T> fetchWithCache(
        cacheKey: String,
        crossinline apiCall: suspend () -> T
    ): T {
        val cacheDao = ReadingRoomDatabase.getDatabase(application).apiCacheDao()

        try {
            val freshDto = apiCall()
            val freshJson = gson.toJson(freshDto)

            val cachedEntity = cacheDao.getCache(cacheKey)

            if (cachedEntity == null || cachedEntity.jsonResponse != freshJson) {
                cacheDao.insertCache(
                    ApiCacheEntity(
                        endpointKey = cacheKey,
                        jsonResponse = freshJson,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            return freshDto

        } catch (e: Exception) {
            val cachedEntity = cacheDao.getCache(cacheKey)
            if (cachedEntity != null) {
                return gson.fromJson(cachedEntity.jsonResponse, T::class.java)
            }

            throw e
        }
    }

    override suspend fun getCourseForHomeScreen(language: Language): Course {
        val coursesFromApi = fetchWithCache("courses_list") {
            CoursesApiService.CoursesApi.retrofitService.getCourses()
        }

        val featuredCourse = coursesFromApi.items.first {
            it.language == languageMapper.mapLanguageToString(language) &&
                    it.homePlacement == homePlacementMapper.mapHomePlacementToString(HomePlacement.FEATURED)
        }
        return courseMapper.mapCourseShortDtoToCourseDataModel(featuredCourse)
    }

    override suspend fun getCourses(language: Language): List<Course> {
        val coursesFromApi = fetchWithCache("courses_list") {
            CoursesApiService.CoursesApi.retrofitService.getCourses()
        }

        val courses = coursesFromApi.items.filter {
            it.language == languageMapper.mapLanguageToString(language)
        }.map {
            courseMapper.mapCourseShortDtoToCourseDataModel(it)
        }
        val updatedCourses = courses.map { course ->
            course.copy(
                authors = course.authors.map { author ->
                    fetchWithCache("author_${author.id}") {
                        courseMapper.mapAuthorDetailedDTOToAuthorDomainModel(
                            CoursesApiService.CoursesApi.retrofitService.getAuthor(
                                author.id
                            )
                        )
                    }
                }

            )
        }
        return updatedCourses
    }

    override suspend fun getEpisodesByCourseId(courseId: Int): List<Episode> {
        val cacheKey = "course_${courseId}_episodes"

        val episodesFromApi = fetchWithCache(cacheKey) {
            CoursesApiService.CoursesApi.retrofitService.getEpisodes(courseId)
        }

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

        if (!coursesDonationPrefs.checkIfCourseWasMarkedAsCompletedFor20(courseId)) {
            val isCourseCompletedFor20 = isPercentCompleted(episodes, 20)
            if (isCourseCompletedFor20){
                coursesDonationPrefs.is20Completed = true
                coursesDonationPrefs.markCourseAsCompletedFor20(courseId)
            }
        }

        if (!coursesDonationPrefs.checkIfCourseWasMarkedAsCompletedFor75(courseId)) {
            val isCourseCompletedFor75 = isPercentCompleted(episodes, 75)
            if (isCourseCompletedFor75){
                coursesDonationPrefs.is75Completed = true
                coursesDonationPrefs.markCourseAsCompletedFor75(courseId)
            }
        }

        return episodes
    }

    private fun isPercentCompleted(episodes: List<Episode>, percentageToComplete: Int): Boolean {
        if (episodes.isEmpty()) return false

        val completedCount = episodes.count { it.state == EpisodeState.COMPLETED }
        val percentage = (completedCount.toDouble() / episodes.size) * 100.0

        return percentage.roundToInt() >= percentageToComplete
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
        val cacheKey = "course_$courseId"

        val courseFromApi = fetchWithCache(cacheKey) {
            CoursesApiService.CoursesApi.retrofitService.getCourse(courseId)
        }
        return courseMapper.mapCourseDTOToCourseDataModel(courseFromApi)
    }

    override suspend fun getEpisode(
        courseId: Int,
        episodeId: Int
    ): Episode {
        val cacheKey = "course_${courseId}_episode_$episodeId"

        val episodeFromApi = fetchWithCache(cacheKey) {
            CoursesApiService.CoursesApi.retrofitService.getEpisode(courseId, episodeId)
        }
        val episode = courseMapper.mapEpisodeDTOToEpisodeDataModel(episodeFromApi)
        val updatedEpisode = episode.copy(
            authors = episode.authors.map { author ->
                courseMapper.mapAuthorDetailedDTOToAuthorDomainModel(
                    fetchWithCache("author_${author.id}") {
                        CoursesApiService.CoursesApi.retrofitService.getAuthor(
                            author.id
                        )
                    }
                )
            }
        )
        return updatedEpisode
    }

    override suspend fun markEpisodeAsCompleted(episodeId: Int) {
        val dbDao = ReadingRoomDatabase.getDatabase(application).userCompletedEpisodesDao()
        dbDao.insertUserCompletedEpisode(UserCompletedEpisodesDBModel(episodeId))
    }
}
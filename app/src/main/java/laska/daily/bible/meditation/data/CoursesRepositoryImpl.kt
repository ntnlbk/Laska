package laska.daily.bible.meditation.data

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
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

@Singleton
class CoursesRepositoryImpl @Inject constructor(
    private val languageMapper: LanguageMapper,
    private val courseMapper: CoursesMapper,
    private val homePlacementMapper: HomePlacementMapper,
    private val application: Application,
    private val gson: Gson // Инжектим Gson (обычно он предоставляется через AppModule)
) : CoursesRepository {

    // Вспомогательный метод для реализации логики Cache-First без проверки времени
    private suspend inline fun <reified T> fetchWithCache(
        cacheKey: String,
        crossinline apiCall: suspend () -> T
    ): T {
        val cacheDao = ReadingRoomDatabase.getDatabase(application).apiCacheDao()

        try {
            // 1. Пытаемся сделать запрос в сеть
            val freshDto = apiCall()
            val freshJson = gson.toJson(freshDto)

            // 2. Достаем текущий кеш для сравнения
            val cachedEntity = cacheDao.getCache(cacheKey)

            // 3. Сравниваем сырые JSON-строки
            if (cachedEntity == null || cachedEntity.jsonResponse != freshJson) {
                // Если кеша не было ИЛИ данные на сервере изменились — обновляем кеш
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
            // 4. Если интернета нет (или ошибка сети) — ищем спасения в кеше
            val cachedEntity = cacheDao.getCache(cacheKey)
            if (cachedEntity != null) {
                // Десериализуем старый JSON и отдаем пользователю
                return gson.fromJson(cachedEntity.jsonResponse, T::class.java)
            }

            // Если и кеша нет, и сеть упала — тогда уже пробрасываем ошибку на экран
            throw e
        }
    }

    override suspend fun getCourseForHomeScreen(language: Language): Course {
        // Ключ для списка курсов общий, так как getCourses возвращает те же данные
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

        return coursesFromApi.items.filter {
            it.language == languageMapper.mapLanguageToString(language)
        }.map {
            courseMapper.mapCourseShortDtoToCourseDataModel(it)
        }
    }

    override suspend fun getEpisodesByCourseId(courseId: Int): List<Episode> {
        // Динамический ключ для списка эпизодов конкретного курса
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

        return episodes
    }

    override suspend fun getFeaturedSectionNameByLanguage(language: Language): String {
        // Firestore вы оставляете как есть (это не Retrofit)
        val db = Firebase.firestore
        val featuredSectionNameDocument =
            db.collection("courses_android").document("featured_section_title").get().await()
        return when (language) {
            Language.RU -> featuredSectionNameDocument.getString("value_ru") ?: "Новинка LASKA"
            Language.BY -> featuredSectionNameDocument.getString("value_by") ?: "Навінка LASKA"
        }
    }

    override suspend fun getCourse(courseId: Int): Course {
        // Динамический ключ для деталки курса
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
        // Динамический ключ для конкретного эпизода с двумя path-аргументами
        val cacheKey = "course_${courseId}_episode_$episodeId"

        val episodeFromApi = fetchWithCache(cacheKey) {
            CoursesApiService.CoursesApi.retrofitService.getEpisode(courseId, episodeId)
        }
        return courseMapper.mapEpisodeDTOToEpisodeDataModel(episodeFromApi)
    }

    override suspend fun markEpisodeAsCompleted(episodeId: Int) {
        val dbDao = ReadingRoomDatabase.getDatabase(application).userCompletedEpisodesDao()
        dbDao.insertUserCompletedEpisode(UserCompletedEpisodesDBModel(episodeId))
    }
}
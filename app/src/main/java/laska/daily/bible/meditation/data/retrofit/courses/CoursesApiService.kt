package laska.daily.bible.meditation.data.retrofit.courses

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

class CoursesApiService {

    companion object {
        private const val BASE_URL = "https://alef.laska.mobi/wp-json/laska/v1/"
        private val retrofit =
            Retrofit.Builder()
                .addConverterFactory(
                    Json.asConverterFactory("application/json".toMediaType())
                )
                .baseUrl(BASE_URL).build()
    }

    interface CoursesApiService {
        @GET("courses")
        suspend fun getCourses(
        ): CoursesDTO

        @GET("courses/{id}/episodes")
        suspend fun getEpisodes(
            @Path("id") id: Int,
        ): EpisodesDTO

        @GET("courses/{id}")
        suspend fun getCourse(
            @Path("id") id: Int
        ): CourseDTO

    }

    object CoursesApi {
        val retrofitService: CoursesApiService by lazy {
            retrofit.create(CoursesApiService::class.java)
        }
    }

}

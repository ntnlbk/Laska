package mobi.laska.daily.bible.meditation.data.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class LaskaApiService {

    companion object {
        private const val BASE_URL = "https://new2.laska.mobi/wp-json/daily-readings/v1/"
        private val retrofit =
            Retrofit.Builder()
                .addConverterFactory(
                    Json.asConverterFactory("application/json".toMediaType())
                )
                .baseUrl(BASE_URL).build()
    }

    interface LaskaApiService {

        @GET("reading/{date}")
        suspend fun getReading(
            @Path("date") date: String,
            @Query("lang") lang: String? = null
        ): ReadingDTO

        @GET("readings")
        suspend fun getReadings(
            @Query("from") from: String? = null,
            @Query("to") to: String? = null,
            @Query("lang") lang: String? = null,
            @Query("per_page") perPage: Int? = null,
        ): ReadingsDTO

        @GET("languages")
        suspend fun getLanguages(): List<LanguagesDTO>

    }

    object LaskaApi {
        val retrofitService: LaskaApiService by lazy {
            retrofit.create(LaskaApiService::class.java)
        }
    }

}

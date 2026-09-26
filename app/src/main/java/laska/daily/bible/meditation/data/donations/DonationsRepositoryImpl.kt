package laska.daily.bible.meditation.data.donations

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import laska.daily.bible.meditation.domain.donations.DonationsData
import laska.daily.bible.meditation.domain.donations.DonationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DonationsRepository {

    private val db = Firebase.firestore
    private val gson = Gson()
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun getDonationsData(): DonationsData {
        return try {
            val remoteData = withTimeoutOrNull(4000L) {
                fetchFromRemoteFirestore()
            }

            if (remoteData != null) {
                saveToCache(remoteData)
                remoteData
            } else {
                getFromCache() ?: getFallbackData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getFromCache() ?: getFallbackData()
        }
    }

    private suspend fun fetchFromRemoteFirestore(): DonationsData {
        val querySnapshot = db
            .collection("donations_android")
            .get()
            .await()

        val docsMap = querySnapshot.documents.associateBy { it.id }

        val donationsFlag = docsMap["donate_flag"]?.getBoolean("enable_donations") ?: false

        val donationsUrl = docsMap["donate_url"]?.getString("value") ?: "https://pay.raschet.by/"

        val eripDoc = docsMap["erip"]
        val eripPaths = listOfNotNull(
            eripDoc?.getString("path1"),
            eripDoc?.getString("path2"),
            eripDoc?.getString("path3"),
            eripDoc?.getString("path4"),
            eripDoc?.getString("path5"),
            eripDoc?.getString("path6")
        )

        val eripAccount = docsMap["erip_account"]?.getString("value") ?: "BY94AKBB30340019293080070000"

        return DonationsData(
            donationsFlag = donationsFlag,
            donationsUrl = donationsUrl,
            donationsEripPath = eripPaths.ifEmpty { listOf(
                "Платежи",
                "Банковские финансовые услуги",
                "Банки, НКФО",
                "Беларусбанк",
                "Пополнение счёта",
                "BY94AKBB30340019293080070000"
            ) },
            eripAccount = eripAccount
        )
    }

    private fun saveToCache(data: DonationsData) {
        val jsonString = gson.toJson(data)
        prefs.edit().putString(KEY_CACHED_DONATIONS, jsonString).apply()
    }

    private fun getFromCache(): DonationsData? {
        val jsonString = prefs.getString(KEY_CACHED_DONATIONS, null) ?: return null
        return try {
            gson.fromJson(jsonString, DonationsData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun getFallbackData(): DonationsData {
        return DonationsData(
            donationsFlag = false,
            donationsUrl = "https://pay.raschet.by/",
            donationsEripPath = listOf(
                "Платежи",
                "Банковские финансовые услуги",
                "Банки, НКФО",
                "Беларусбанк",
                "Пополнение счёта",
                "BY94AKBB30340019293080070000"
            ),
            eripAccount = "BY94AKBB30340019293080070000"
        )
    }

    companion object {
        private const val PREFS_NAME = "donations_cache_prefs"
        private const val KEY_CACHED_DONATIONS = "key_donations_data"
    }
}
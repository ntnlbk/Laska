package laska.daily.bible.meditation.presentation.supportfragment

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class PopUpPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pop_ups_prefs", Context.MODE_PRIVATE)

    var lastPopupId: String
        get() = prefs.getString(KEY_POPUP_ID, UUID.randomUUID().toString())
            ?: UUID.randomUUID().toString()
        set(value) = prefs.edit { putString(KEY_POPUP_ID, value) }
    companion object {
        private const val KEY_POPUP_ID = "key_popup_id"
    }
}
package laska.daily.bible.meditation.presentation.supportfragment

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SupportPaymentPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("support_payment_prefs", Context.MODE_PRIVATE)

    var isPaymentPending: Boolean
        get() = prefs.getBoolean(KEY_PAYMENT_PENDING, false)
        set(value) = prefs.edit { putBoolean(KEY_PAYMENT_PENDING, value) }

    fun checkAndClearPaymentPending(): Boolean {
        return if (isPaymentPending) {
            isPaymentPending = false
            true
        } else {
            false
        }
    }

    companion object {
        private const val KEY_PAYMENT_PENDING = "key_payment_pending"
    }
}
package laska.daily.bible.meditation.presentation.supportfragment

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class SupportPaymentPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("support_payment_prefs", Context.MODE_PRIVATE)

    var isPaymentPending: Boolean
        get() = prefs.getBoolean(KEY_PAYMENT_PENDING, false)
        set(value) = prefs.edit { putBoolean(KEY_PAYMENT_PENDING, value) }

    var lastDonationId: String
        get() = prefs.getString(KEY_LAST_DONATION_ID, UUID.randomUUID().toString())
            ?: UUID.randomUUID().toString()
        set(value) = prefs.edit { putString(KEY_LAST_DONATION_ID, value) }

    var lastDonationAmount: Int
        get() = prefs.getInt(KEY_LAST_DONATION_AMOUNT, 0)
        set(value) = prefs.edit { putInt(KEY_LAST_DONATION_AMOUNT, value) }

    fun checkAndClearPaymentPending(): Boolean {
        return if (isPaymentPending) {
            isPaymentPending = false
            true
        } else {
            false
        }
    }

    fun setPendingPayment(donationId: String, amount: Int) {
        prefs.edit {
            putBoolean(KEY_PAYMENT_PENDING, true)
            putString(KEY_LAST_DONATION_ID, donationId)
            putInt(KEY_LAST_DONATION_AMOUNT, amount)
        }
    }

    companion object {
        private const val KEY_PAYMENT_PENDING = "key_payment_pending"
        private const val KEY_LAST_DONATION_ID = "key_last_donation_id"
        private const val KEY_LAST_DONATION_AMOUNT = "key_last_donation_amount"
    }
}
package laska.daily.bible.meditation.presentation.uils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DateUtils @Inject constructor() {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        fun getNextDay(currentDateStr: String): String {
            val currentDate = LocalDate.parse(currentDateStr, formatter)

            val nextDate = currentDate.plusDays(1)

            return nextDate.format(formatter)
        }

        fun getPreviousDay(currentDateStr: String): String {
            val currentDate = LocalDate.parse(currentDateStr, formatter)

            val previousDate = currentDate.minusDays(1)

            return previousDate.format(formatter)
        }
        fun todayFormatted(): String {
            val today = LocalDate.now()
            val limitDate = LocalDate.of(2026, 5, 15) // релиз: 2026-05-15

            val resultDate = if (today.isBefore(limitDate)) {
                limitDate
            } else {
                today
            }

            return resultDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        }

        fun plusSevenDays(date: String): String {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

            return LocalDate
                .parse(date, formatter)
                .plusDays(7)
                .format(formatter)
        }

    }
}
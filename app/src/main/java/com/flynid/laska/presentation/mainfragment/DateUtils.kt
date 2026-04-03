package com.flynid.laska.presentation.mainfragment

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateUtils {
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
    }
}
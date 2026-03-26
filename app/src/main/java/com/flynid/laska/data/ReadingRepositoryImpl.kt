package com.flynid.laska.data

import com.flynid.laska.domain.Language
import com.flynid.laska.domain.ReadingItem
import com.flynid.laska.domain.ReadingRepository

class ReadingRepositoryImpl: ReadingRepository {

    override fun getReading(
        date: String,
        language: Language
    ): ReadingItem {
        TODO("Not yet implemented")
    }

}
package com.flynid.laska.domain

interface ReadingRepository {

    suspend fun getReading(date: String, language: Language): ReadingItem



}
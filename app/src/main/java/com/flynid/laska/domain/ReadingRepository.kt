package com.flynid.laska.domain

interface ReadingRepository {

    fun getReading(date: String, language: Language): ReadingItem

    

}
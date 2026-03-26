package com.flynid.laska.domain

class GetReadingUseCase(private val repository: ReadingRepository) {
    suspend operator fun invoke(date: String, language: Language) = repository.getReading(date, language)
}
package com.flynid.laska.domain


data class ReadingItem(
    val id: Int,
    val date: String,
    val dateFormatted: String,
    val language: Language,
    val bibleReference: String,
    val bibleText: String,
    val bibleTextPlain: String,
    val feastName: String,
    val reflectionTextFirst: String,
    val reflectionTextSecond: String,
    val authorName: String,
    val audioURL: String,
    val imageURL: String,
    val audioLocalPath: String?,
    val imageLocalPath: String?,
    val permalink: String
)

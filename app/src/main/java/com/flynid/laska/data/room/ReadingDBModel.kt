package com.flynid.laska.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "readings_table")
data class ReadingDBModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val dateFormatted: String,
    val language: String,
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

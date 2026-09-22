package com.moetaz.words.data.local.database

import android.content.Context
import com.moetaz.words.R
import com.moetaz.words.data.local.dao.WordDao
import com.moetaz.words.data.local.dto.WordsResponseDto
import kotlinx.serialization.json.Json

object DatabaseInitializer {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun populateIfEmpty(context: Context, dao: WordDao) {
        if (dao.getAllWords().isEmpty()) {
            try {
                val jsonString = context.resources.openRawResource(R.raw.words)
                    .bufferedReader()
                    .use { it.readText() }
                val response = json.decodeFromString<WordsResponseDto>(jsonString)
                val entities = response.words.map { it.toEntity() }
                if (entities.isNotEmpty()) {
                    dao.insertWords(entities)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

package com.moetaz.words.data.local.converter

import androidx.room.TypeConverter
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.WordMasteryStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(value)
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromExampleList(value: List<Example>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toExampleList(value: String): List<Example> {
        return try {
            json.decodeFromString<List<Example>>(value)
        } catch (_: Exception) {
            try {
                json.decodeFromString<List<String>>(value).map { Example(english = it, arabic = "") }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    @TypeConverter
    fun fromMasteryStatus(status: WordMasteryStatus): String = status.name

    @TypeConverter
    fun toMasteryStatus(value: String): WordMasteryStatus {
        return try {
            WordMasteryStatus.valueOf(value)
        } catch (_: Exception) {
            WordMasteryStatus.LEARNING
        }
    }
}

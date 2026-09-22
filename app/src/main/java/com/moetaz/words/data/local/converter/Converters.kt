package com.moetaz.words.data.local.converter

import androidx.room.TypeConverter
import com.moetaz.words.domain.model.Example
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromExampleList(value: List<Example>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toExampleList(value: String): List<Example> {
        return Json.decodeFromString(value)
    }
}

package com.moetaz.words.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moetaz.words.data.local.converter.Converters
import com.moetaz.words.data.local.dao.WordDao
import com.moetaz.words.data.local.entity.WordEntity

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

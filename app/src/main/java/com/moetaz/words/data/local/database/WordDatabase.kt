package com.moetaz.words.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moetaz.words.data.local.converter.Converters
import com.moetaz.words.data.local.dao.WordDao
import com.moetaz.words.data.local.entity.WordEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `words` ADD COLUMN `phonetic` TEXT")
        db.execSQL("ALTER TABLE `words` ADD COLUMN `definition` TEXT")
    }
}

@Database(entities = [WordEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

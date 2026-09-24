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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `words` ADD COLUMN `masteryStatus` TEXT NOT NULL DEFAULT 'LEARNING'")
        db.execSQL("ALTER TABLE `words` ADD COLUMN `isFavorite` INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [WordEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

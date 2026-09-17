package com.moetaz.words.data.local.dao

import androidx.room.*
import com.moetaz.words.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id")
    fun getWordById(id: Long): Flow<WordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)
}

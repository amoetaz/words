package com.moetaz.words.data.local.dao

import androidx.room.*
import com.moetaz.words.data.local.entity.WordEntity
import com.moetaz.words.domain.model.WordMasteryStatus
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("UPDATE words SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE words SET masteryStatus = :status WHERE id = :id")
    suspend fun updateMasteryStatus(id: Long, status: WordMasteryStatus)
}

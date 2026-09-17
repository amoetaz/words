package com.moetaz.words.domain.repository

import com.moetaz.words.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getWords(): Flow<List<Word>>
    fun getWordById(id: Long): Flow<Word?>
    suspend fun insertWord(word: Word)
    suspend fun deleteWord(word: Word)
}

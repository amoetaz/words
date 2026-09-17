package com.moetaz.words.data.repository

import com.moetaz.words.data.local.dao.WordDao
import com.moetaz.words.data.local.entity.WordEntity
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordRepositoryImpl(private val dao: WordDao) : WordRepository {
    override fun getWords(): Flow<List<Word>> {
        return dao.getWords().map { entities ->
            entities.map { it.toWord() }
        }
    }

    override fun getWordById(id: Long): Flow<Word?> {
        return dao.getWordById(id).map { it?.toWord() }
    }

    override suspend fun insertWord(word: Word) {
        dao.insertWord(WordEntity.fromWord(word))
    }

    override suspend fun deleteWord(word: Word) {
        dao.deleteWord(WordEntity.fromWord(word))
    }
}

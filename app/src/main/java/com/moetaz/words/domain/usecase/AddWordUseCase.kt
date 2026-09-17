package com.moetaz.words.domain.usecase

import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.repository.WordRepository

class AddWordUseCase(private val repository: WordRepository) {
    suspend operator fun invoke(word: Word) = repository.insertWord(word)
}

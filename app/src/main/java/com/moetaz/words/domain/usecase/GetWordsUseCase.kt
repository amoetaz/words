package com.moetaz.words.domain.usecase

import com.moetaz.words.domain.repository.WordRepository

class GetWordsUseCase(private val repository: WordRepository) {
    operator fun invoke() = repository.getWords()
}

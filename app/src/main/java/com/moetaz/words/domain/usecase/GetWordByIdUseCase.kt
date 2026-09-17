package com.moetaz.words.domain.usecase

import com.moetaz.words.domain.repository.WordRepository

class GetWordByIdUseCase(private val repository: WordRepository) {
    operator fun invoke(id: Long) = repository.getWordById(id)
}

package com.moetaz.words.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Example(
    val english: String,
    val arabic: String
)

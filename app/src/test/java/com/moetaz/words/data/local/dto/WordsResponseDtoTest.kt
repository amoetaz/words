package com.moetaz.words.data.local.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WordsResponseDtoTest {

    @Test
    fun deserialization_parsesSampleJsonCorrectly() {
        val sampleJson = """
            {
              "words": [
                {
                  "word": "exuberance",
                  "translation": [
                    "امتلاء بالحيوية"
                  ],
                  "examples": [
                    {
                      "english": "Her exuberance was contagious.",
                      "arabic": "كان امتلائها بالحيوية معدياً."
                    },
                    {
                      "english": "The children played with great exuberance.",
                      "arabic": "لعب الأطفال بامتلاء بالحيوية كبير."
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val json = Json { ignoreUnknownKeys = true }
        val response = json.decodeFromString<WordsResponseDto>(sampleJson)

        assertEquals(1, response.words.size)
        val wordDto = response.words[0]
        assertEquals("exuberance", wordDto.word)
        assertEquals(listOf("امتلاء بالحيوية"), wordDto.translations)
        assertEquals(2, wordDto.examples.size)
        assertEquals("Her exuberance was contagious.", wordDto.examples[0].english)
        assertEquals("كان امتلائها بالحيوية معدياً.", wordDto.examples[0].arabic)

        val entity = wordDto.toEntity()
        assertEquals("exuberance", entity.word)
        assertEquals(listOf("امتلاء بالحيوية"), entity.translations)
        assertEquals(2, entity.examples.size)
    }
}

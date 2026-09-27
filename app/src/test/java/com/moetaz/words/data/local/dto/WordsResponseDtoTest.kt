package com.moetaz.words.data.local.dto

import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordsResponseDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        explicitNulls = false
        encodeDefaults = true
    }

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

    @Test
    fun deserialization_parsesDatabaseStructureJsonCorrectly() {
        val dbJson = """
            {
              "words": [
                {
                  "id": 42,
                  "word": "Eloquent",
                  "translations": ["فصيح", "بليغ"],
                  "examples": [
                    {
                      "english": "An eloquent speech.",
                      "arabic": "خطاب بليغ."
                    }
                  ],
                  "phonetic": "/ˈɛl.ə.kwənt/",
                  "definition": "Fluent or persuasive in speaking or writing.",
                  "definitionTranslation": "طلاقة أو إقناع في التحدث أو الكتابة.",
                  "masteryStatus": "MASTERED",
                  "isFavorite": true
                }
              ]
            }
        """.trimIndent()

        val response = json.decodeFromString<WordsResponseDto>(dbJson)

        assertEquals(1, response.words.size)
        val dto = response.words[0]
        assertEquals(42L, dto.id)
        assertEquals("Eloquent", dto.word)
        assertEquals(listOf("فصيح", "بليغ"), dto.translations)
        assertEquals("/ˈɛl.ə.kwənt/", dto.phonetic)
        assertEquals("Fluent or persuasive in speaking or writing.", dto.definition)
        assertEquals("طلاقة أو إقناع في التحدث أو الكتابة.", dto.definitionTranslation)
        assertEquals(WordMasteryStatus.MASTERED, dto.masteryStatus)
        assertEquals(true, dto.isFavorite)

        val entity = dto.toEntity()
        assertEquals(42L, entity.id)
        assertEquals("Eloquent", entity.word)
        assertEquals(WordMasteryStatus.MASTERED, entity.masteryStatus)
        assertTrue(entity.isFavorite)
    }

    @Test
    fun deserialization_parsesRootJsonArrayCorrectly() {
        val arrayJson = """
            [
              {
                "id": 1,
                "word": "Meticulous",
                "translations": ["دقيق"],
                "masteryStatus": "REVIEWING",
                "isFavorite": false
              }
            ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<WordDto>>(arrayJson)

        assertEquals(1, dtos.size)
        val entity = dtos[0].toEntity()
        assertEquals(1L, entity.id)
        assertEquals("Meticulous", entity.word)
        assertEquals(listOf("دقيق"), entity.translations)
        assertEquals(WordMasteryStatus.REVIEWING, entity.masteryStatus)
    }

    @Test
    fun serialization_formatsWordToDatabaseStructureJson() {
        val word = Word(
            id = 10L,
            word = "Resilient",
            translations = listOf("مرن", "صامد"),
            examples = listOf(Example("Resilient nature.", "طبيعة صامدة.")),
            phonetic = "/rɪˈzɪl.jənt/",
            definition = "Able to withstand or recover quickly.",
            definitionTranslation = "قادر على الصمود.",
            masteryStatus = WordMasteryStatus.LEARNING,
            isFavorite = true
        )

        val dto = WordDto.fromWord(word)
        val response = WordsResponseDto(words = listOf(dto))
        val encodedJson = json.encodeToString(response)

        assertTrue(encodedJson.contains("\"id\": 10"))
        assertTrue(encodedJson.contains("\"word\": \"Resilient\""))
        assertTrue(encodedJson.contains("\"translations\":"))
        assertTrue(encodedJson.contains("\"masteryStatus\": \"LEARNING\""))
        assertTrue(encodedJson.contains("\"isFavorite\": true"))

        // Round-trip verification
        val decodedResponse = json.decodeFromString<WordsResponseDto>(encodedJson)
        assertEquals(1, decodedResponse.words.size)
        val decodedWord = decodedResponse.words[0].toEntity().toWord()
        assertEquals(word, decodedWord)
    }
}


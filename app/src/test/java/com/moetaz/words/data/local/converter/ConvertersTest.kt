package com.moetaz.words.data.local.converter

import com.moetaz.words.domain.model.Example
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    @Test
    fun fromStringList_emptyList_returnsEmptyJsonArray() {
        val list = emptyList<String>()
        val result = converters.fromStringList(list)
        assertEquals("[]", result)
    }

    @Test
    fun toStringList_emptyJsonArray_returnsEmptyList() {
        val json = "[]"
        val result = converters.toStringList(json)
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun fromStringList_listOfStrings_returnsJsonArrayString() {
        val list = listOf("book", "novel", "reading material")
        val result = converters.fromStringList(list)
        assertEquals("""["book","novel","reading material"]""", result)
    }

    @Test
    fun toStringList_jsonArrayString_returnsListOfStrings() {
        val json = """["book","novel","reading material"]"""
        val result = converters.toStringList(json)
        assertEquals(listOf("book", "novel", "reading material"), result)
    }

    @Test
    fun fromExampleList_listOfExamples_returnsJsonArrayString() {
        val list = listOf(Example("Her exuberance was contagious.", "كان امتلائها بالحيوية معدياً."))
        val result = converters.fromExampleList(list)
        val expected = """[{"english":"Her exuberance was contagious.","arabic":"كان امتلائها بالحيوية معدياً."}]"""
        assertEquals(expected, result)
    }

    @Test
    fun toExampleList_jsonArrayString_returnsListOfExamples() {
        val json = """[{"english":"Her exuberance was contagious.","arabic":"كان امتلائها بالحيوية معدياً."}]"""
        val result = converters.toExampleList(json)
        val expected = listOf(Example("Her exuberance was contagious.", "كان امتلائها بالحيوية معدياً."))
        assertEquals(expected, result)
    }

    @Test
    fun converters_roundTrip_preservesSpecialAndUnicodeCharacters() {
        val original = listOf("مرحبا", "Bonjour! Ça va?", "test \"quotes\" & symbols: %$#@!")
        val json = converters.fromStringList(original)
        val deserialized = converters.toStringList(json)
        assertEquals(original, deserialized)
    }

    @Test
    fun toExampleList_stringArrayJson_convertsToStringExamples() {
        val json = """["man in the jungle "]"""
        val result = converters.toExampleList(json)
        val expected = listOf(Example(english = "man in the jungle ", arabic = ""))
        assertEquals(expected, result)
    }

    @Test
    fun toExampleList_invalidJson_returnsEmptyList() {
        val json = "invalid json"
        val result = converters.toExampleList(json)
        assertEquals(emptyList<Example>(), result)
    }

    @Test
    fun toStringList_invalidJson_returnsEmptyList() {
        val json = "invalid json"
        val result = converters.toStringList(json)
        assertEquals(emptyList<String>(), result)
    }
}

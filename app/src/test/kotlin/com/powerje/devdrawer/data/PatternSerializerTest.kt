package com.powerje.devdrawer.data

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PatternSerializerTest {

    @Test
    fun `serialize and deserialize pattern`() {
        val pattern = Pattern(label = "Work Apps", regex = "com\\.mycompany\\..*")
        val json = Json.encodeToString(Pattern.serializer(), pattern)
        val decoded = Json.decodeFromString(Pattern.serializer(), json)
        assertEquals(pattern, decoded)
    }

    @Test
    fun `serialize and deserialize pattern list`() {
        val patterns = listOf(
            Pattern(label = "Work", regex = "com\\.work\\..*"),
            Pattern(label = "Personal", regex = "com\\.personal\\..*")
        )
        val json = Json.encodeToString(ListSerializer(Pattern.serializer()), patterns)
        val decoded: List<Pattern> = Json.decodeFromString(ListSerializer(Pattern.serializer()), json)
        assertEquals(patterns, decoded)
    }
}

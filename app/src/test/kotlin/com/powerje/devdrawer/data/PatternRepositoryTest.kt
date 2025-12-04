package com.powerje.devdrawer.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PatternRepositoryTest {

    private lateinit var repository: PatternRepository

    @Before
    fun setup() {
        repository = PatternRepository(FakePatternStorage())
    }

    @Test
    fun `empty repository returns empty list`() = runTest {
        val patterns = repository.getPatterns().first()
        assertEquals(emptyList<Pattern>(), patterns)
    }

    @Test
    fun `save and retrieve patterns`() = runTest {
        val patterns = listOf(
            Pattern(label = "Test", regex = "com\\.test\\..*")
        )
        repository.savePatterns(patterns)
        val retrieved = repository.getPatterns().first()
        assertEquals(patterns, retrieved)
    }

    @Test
    fun `save overwrites previous patterns`() = runTest {
        repository.savePatterns(listOf(Pattern("Old", "old\\..*")))
        val newPatterns = listOf(Pattern("New", "new\\..*"))
        repository.savePatterns(newPatterns)
        val retrieved = repository.getPatterns().first()
        assertEquals(newPatterns, retrieved)
    }
}

class FakePatternStorage : PatternStorage {
    private var patterns: List<Pattern> = emptyList()

    override suspend fun load(): List<Pattern> = patterns

    override suspend fun save(patterns: List<Pattern>) {
        this.patterns = patterns
    }
}

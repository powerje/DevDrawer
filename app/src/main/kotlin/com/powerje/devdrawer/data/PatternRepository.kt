package com.powerje.devdrawer.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PatternStorage {
    suspend fun load(): List<Pattern>
    suspend fun save(patterns: List<Pattern>)
}

class PatternRepository(private val storage: PatternStorage) {

    private val _patterns = MutableStateFlow<List<Pattern>>(emptyList())

    suspend fun initialize() {
        _patterns.value = storage.load()
    }

    fun getPatterns(): Flow<List<Pattern>> = _patterns.asStateFlow()

    suspend fun savePatterns(patterns: List<Pattern>) {
        storage.save(patterns)
        _patterns.value = patterns
    }
}

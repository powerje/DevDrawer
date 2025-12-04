package com.powerje.devdrawer.config

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.powerje.devdrawer.data.DataStorePatternStorage
import com.powerje.devdrawer.data.Pattern
import com.powerje.devdrawer.data.PatternRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfigViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PatternRepository(DataStorePatternStorage(application))

    private val _patterns = MutableStateFlow<List<Pattern>>(emptyList())
    val patterns: StateFlow<List<Pattern>> = _patterns.asStateFlow()

    private val _editingPattern = MutableStateFlow<EditingPattern?>(null)
    val editingPattern: StateFlow<EditingPattern?> = _editingPattern.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initialize()
            repository.getPatterns().collect { _patterns.value = it }
        }
    }

    fun addPattern() {
        _editingPattern.value = EditingPattern(index = null, label = "", regex = "")
    }

    fun editPattern(index: Int) {
        val pattern = _patterns.value.getOrNull(index) ?: return
        _editingPattern.value = EditingPattern(index = index, label = pattern.label, regex = pattern.regex)
    }

    fun dismissDialog() {
        _editingPattern.value = null
    }

    fun savePattern(label: String, regex: String): Boolean {
        // Validate regex
        try {
            regex.toRegex()
        } catch (e: Exception) {
            return false
        }

        val editing = _editingPattern.value ?: return false
        val current = _patterns.value.toMutableList()
        val newPattern = Pattern(label = label.trim(), regex = regex.trim())

        if (editing.index != null) {
            current[editing.index] = newPattern
        } else {
            current.add(newPattern)
        }

        viewModelScope.launch {
            repository.savePatterns(current)
        }

        _editingPattern.value = null
        return true
    }

    fun deletePattern(index: Int) {
        val current = _patterns.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            viewModelScope.launch {
                repository.savePatterns(current)
            }
        }
    }
}

data class EditingPattern(
    val index: Int?,
    val label: String,
    val regex: String
)

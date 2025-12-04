package com.powerje.devdrawer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "patterns")

class DataStorePatternStorage(private val context: Context) : PatternStorage {

    private val patternsKey = stringPreferencesKey("patterns_json")

    override suspend fun load(): List<Pattern> {
        val json = context.dataStore.data.map { preferences ->
            preferences[patternsKey] ?: "[]"
        }.first()
        return Json.decodeFromString(ListSerializer(Pattern.serializer()), json)
    }

    override suspend fun save(patterns: List<Pattern>) {
        val json = Json.encodeToString(ListSerializer(Pattern.serializer()), patterns)
        context.dataStore.edit { preferences ->
            preferences[patternsKey] = json
        }
    }
}

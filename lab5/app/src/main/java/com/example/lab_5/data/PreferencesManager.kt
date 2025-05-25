package com.example.lab_5.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
    }

    val searchQueryFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SEARCH_QUERY_KEY]
    }

    suspend fun saveSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_QUERY_KEY] = query
        }
    }
} 
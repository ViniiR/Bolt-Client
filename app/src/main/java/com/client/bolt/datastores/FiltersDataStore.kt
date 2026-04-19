package com.client.bolt.datastores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class FiltersDataStore(
    private val context: Context
) {
    val reverse = context.filtersDataStore.data.map { preferences ->
        preferences[reverseKey]
    }
    val hiatus = context.filtersDataStore.data.map { preferences ->
        preferences[hiatusKey]
    }
    val fininshed = context.filtersDataStore.data.map { preferences ->
        preferences[finishedKey]
    }
    val books = context.filtersDataStore.data.map { preferences ->
        preferences[booksKey]
    }
    val manga = context.filtersDataStore.data.map { preferences ->
        preferences[mangaKey]
    }
    val manhwa = context.filtersDataStore.data.map { preferences ->
        preferences[manhwaKey]
    }
    val manhua = context.filtersDataStore.data.map { preferences ->
        preferences[manhuaKey]
    }

    suspend fun updateReverse(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[reverseKey] = value
        }
    }
    suspend fun updateHiatus(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[hiatusKey] = value
        }
    }
    suspend fun updateFinished(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[finishedKey] = value
        }
    }
    suspend fun updateBooks(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[booksKey] = value
        }
    }
    suspend fun updateManga(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[mangaKey] = value
        }
    }
    suspend fun updateManhwa(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[manhwaKey] = value
        }
    }
    suspend fun updateManhua(value: Boolean) {
        context.filtersDataStore.edit { settings ->
            settings[manhuaKey] = value
        }
    }

    companion object {
        val Context.filtersDataStore by preferencesDataStore("filters")

        val reverseKey = booleanPreferencesKey("reverse_key")
        val hiatusKey = booleanPreferencesKey("hiatus_key")
        val finishedKey = booleanPreferencesKey("finished_key")
        val booksKey = booleanPreferencesKey("books_key")
        val mangaKey = booleanPreferencesKey("manga_key")
        val manhwaKey = booleanPreferencesKey("manhwa_key")
        val manhuaKey = booleanPreferencesKey("manhua_key")
    }
}
package com.client.bolt.datastores

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class ApiSettingsDataStore(
    private val context: Context
) {
    val url = context.apiSettingsDataStore.data.map { preferences ->
        preferences[urlKey]
    }
    val username = context.apiSettingsDataStore.data.map { preferences ->
        preferences[usernameKey]
    }
    val password = context.apiSettingsDataStore.data.map { preferences ->
        preferences[passwordKey]
    }

    suspend fun updateUrl(url: String) {
        context.apiSettingsDataStore.edit { settings ->
            settings[urlKey] = url
        }
    }

    suspend fun updateUsername(username: String) {
        context.apiSettingsDataStore.edit { settings ->
            settings[usernameKey] = username
        }
    }

    suspend fun updatePassword(password: String) {
        context.apiSettingsDataStore.edit { settings ->
            settings[passwordKey] = password
        }
    }

    companion object {
        val Context.apiSettingsDataStore by preferencesDataStore("api_settings")

        val urlKey = stringPreferencesKey("url_key")
        val usernameKey = stringPreferencesKey("username_key")
        val passwordKey = stringPreferencesKey("password_key")
    }
}
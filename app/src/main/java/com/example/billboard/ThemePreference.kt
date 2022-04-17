package com.example.billboard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class ThemePreference(private val context: Context, deviceTheme: Boolean) {
    companion object {
        private val Context.dataStoreTheme: DataStore<Preferences> by preferencesDataStore("appTheme")
        val theme_boolean = booleanPreferencesKey("dark_theme")
        val device_uuid = stringPreferencesKey("uuid")
    }

    val getTheme: Flow<Boolean> = context.dataStoreTheme.data
        .map { preferences ->
            preferences[theme_boolean] ?: deviceTheme
        }

    suspend fun saveTheme( userPreference: Boolean ) {
        context.dataStoreTheme.edit { preferences ->
            preferences[theme_boolean] = userPreference
        }
    }

}
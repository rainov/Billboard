package com.example.billboard

/*===================================================/
|| The ThemePreference class is used to remember
|| the last selected theme by the user on the app
|| or the device one.
/====================================================*/

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreference(private val context: Context, deviceTheme: Boolean) {
    companion object {
        private val Context.dataStoreTheme: DataStore<Preferences> by preferencesDataStore("appTheme")
        val theme_boolean = booleanPreferencesKey("dark_theme")
    }

    //Fetch the last theme chosen
    val getTheme: Flow<Boolean> = context.dataStoreTheme.data
        .map { preferences ->
            preferences[theme_boolean] ?: deviceTheme
        }

    //Store the theme chosen by the user
    suspend fun saveTheme( userPreference: Boolean ) {
        context.dataStoreTheme.edit { preferences ->
            preferences[theme_boolean] = userPreference
        }
    }

}
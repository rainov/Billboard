package com.example.billboard
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.billboard.ui.theme.BillBoardTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@Composable
fun BillBoardApp() {

//    val context = LocalContext.current
//
//    val darkFlow: Flow<Boolean> = context.dataStore.data.map {  preferences ->
//        preferences[dark_mode] ?: true
//    }

//    val darkMode = remember { mutableStateOf(true) }
    val darkBoolean = isSystemInDarkTheme()
    val darkMode = remember { mutableStateOf(darkBoolean) }

    BillBoardTheme (
        darkTheme = darkMode.value
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            ViewContainer(darkMode)
        }
    }
}
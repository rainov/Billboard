package com.example.billboard
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
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

    val darkBoolean = isSystemInDarkTheme()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val themeStore = ThemePreference(context, darkBoolean)
    val themeSetting = themeStore.getTheme.collectAsState(initial = darkBoolean)

    BillBoardTheme (
        darkTheme = themeSetting.value
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            ViewContainer( scope, themeStore, themeSetting.value )
        }
    }
}
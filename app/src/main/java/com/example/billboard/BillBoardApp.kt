package com.example.billboard

/*===================================================/
|| This is the starting point of our application. Here
|| the device theme is checked and passed to our app
|| as default theme. If the user wants different theme
|| setting, it is saved in dataStore and used as default
/====================================================*/

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

    /////////////////////////
    // Check system theme //
    ///////////////////////
    val darkBoolean = isSystemInDarkTheme()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    ////////////////////////////////////////////////////////////////////////////////////////////
    // Creates the dataStore for the theme preference and passes the system theme as default //
    //////////////////////////////////////////////////////////////////////////////////////////
    val themeStore = ThemePreference(context, darkBoolean)

    ///////////////////////////////////////
    // Reads the saved theme preference //
    /////////////////////////////////////
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
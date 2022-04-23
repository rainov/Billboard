package com.example.billboard

/*===================================================/
|| App drawer container. The content is changing
|| according to the current view of the app.
/====================================================*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

////////////////////////////////////////////////////////////////////////
// Main scaffold structure, the content is passed from the app views //
//////////////////////////////////////////////////////////////////////
@Composable
fun DrawerMainScreen(
    scState: ScaffoldState,
    scope: CoroutineScope,
    contentComp: Unit
) {
    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(false, scState, true, scope) },
        content = { contentComp }
    )
}

////////////////////////////////////////////////////
// Drawer content for the main screen of the app //
//////////////////////////////////////////////////
@Composable
fun DrawerContent( navControl: NavController, scState: ScaffoldState, scope: CoroutineScope) {

    ///////////////////////
    // Container column //
    /////////////////////
    Column (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(modifier = Modifier.height(15.dp))

        ///////////////////////////////////////////
        // Affiliate partners navigation button //
        /////////////////////////////////////////
        OutlinedButton(
            onClick = {
                navControl.navigate("Affiliate")
                scope.launch { scState.drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth(.85f)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
            elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
        ) {
            Text( text = stringResource(R.string.promotions))
        }

        Spacer(modifier = Modifier.height(15.dp))

        /////////////////////////////////
        // About us navigation button //
        ///////////////////////////////
        OutlinedButton(
            onClick = {
                navControl.navigate("About")
                scope.launch { scState.drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth(.85f)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
            elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
        ) {
            Text( text = stringResource(R.string.about))
        }

        Spacer(modifier = Modifier.height(15.dp))


        /////////////////////////////////
        // Settings navigation button //
        ///////////////////////////////
        OutlinedButton(
            onClick = {
                navControl.navigate("Settings")
                scope.launch { scState.drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth(.85f)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
            elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
        ) {
            Text( text = stringResource(R.string.settings))
        }

        Spacer(modifier = Modifier.height(15.dp))

    }
}
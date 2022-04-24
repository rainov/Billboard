package com.example.billboard

/*===================================================/
|| Composable for the content of the app drawer outside
|| of group view
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

////////////////////////////////////////////////////////////////////////
// Main scaffold structure, the content is passed from the app views //
//////////////////////////////////////////////////////////////////////
@Composable
fun DrawerMainScreen(
    navControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope
) {
    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(false, scState, true, scope) },
        content = { DrawerContent(navControl , scState, scope ) }
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
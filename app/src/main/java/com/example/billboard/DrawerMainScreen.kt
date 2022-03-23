package com.example.billboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerMainScreen( scState: ScaffoldState, navControl: NavController, scope: CoroutineScope) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(false, scState, true, scope ) },
        content = { DrawerContent( navControl, scState, scope ) },
    )
}

@Composable
fun DrawerContent( navControl: NavController, scState: ScaffoldState, scope: CoroutineScope) {

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
            ){

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = {

            },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
        ) {
            Text( text = stringResource(R.string.promotions))
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = {

            },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
        ) {
            Text( text = stringResource(R.string.about))
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = {
                navControl.navigate("Settings")
                scope.launch { scState.drawerState.close() }
            },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
        ) {
            Text( text = stringResource(R.string.settings))
        }

        Spacer(modifier = Modifier.height(15.dp))

    }
}
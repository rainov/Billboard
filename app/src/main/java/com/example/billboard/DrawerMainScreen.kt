package com.example.billboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.billboard.ui.theme.Bilboard_green

@Composable
fun DrawerMainScreen( scState: ScaffoldState) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(false, scState) },
        content = { DrawerContent() },
    )
}

@Composable
fun DrawerContent() {

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

        OutlinedButton(
            onClick = {

            },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
        ) {
            Text( text = stringResource(R.string.sign_out))
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}
package com.example.billboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import com.example.billboard.ui.theme.Billboard_lightGreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun SettingsView (
    scState: ScaffoldState,
    navControl: NavController,
    userVM: UserViewModel,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope ) },
        content = { SettingsContent( navControl, userVM, scState, scope ) },
        drawerContent = { DrawerMainScreen( scState, navControl, scope ) }
    )

}

@Composable
fun SettingsContent( navControl: NavController, userVM: UserViewModel, scState: ScaffoldState, scope: CoroutineScope) {

    val checkedState = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text( text = userVM.userEmail.value, fontSize = 25.sp )

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .width(280.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
            ) {
                Text( text = stringResource(R.string.reset_password))
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedButton(
                onClick = { userVM.signOut() },
                modifier = Modifier
                    .width(280.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
            ) {
                Text( text = stringResource( R.string.sign_out ))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text( text = stringResource(R.string.dark_mode))
                Switch(
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Bilboard_green,
                        uncheckedThumbColor = Color.DarkGray,
                        checkedTrackColor = Billboard_lightGreen,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedButton(
                onClick = {
                    navControl.navigate("MainScreen")
                          },
                modifier = Modifier
                    .width(280.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
            ) {
                Text( text = stringResource( R.string.exit_settings ))
            }

        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .width(280.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Color.Red )
            ) {
                Text( text = stringResource( R.string.delete_account ))
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
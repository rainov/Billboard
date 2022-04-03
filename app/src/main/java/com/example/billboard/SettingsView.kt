package com.example.billboard

import android.util.Log
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
import com.example.billboard.ui.theme.Billboard_Red
import com.example.billboard.ui.theme.Billboard_lightGreen
import com.google.firebase.auth.FirebaseAuth
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
        drawerContent = { DrawerMainScreen (
                scState,
                scope,
                DrawerContent(navControl , scState, scope )
            )
        }
    )

}

@Composable
fun SettingsContent( navControl: NavController, userVM: UserViewModel, scState: ScaffoldState, scope: CoroutineScope) {

    val checkedState = remember { mutableStateOf(true) }
    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {

        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.passwd_reset))
            },
            text = {
                Text(text = stringResource(R.string.passwd_reset_mess) + " -> " + userVM.userEmail.value)
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        resetPassword(userVM)
                        openDialog.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(stringResource(R.string.send))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        openDialog.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

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

            Text( text = userVM.userName.value, fontSize = 20.sp )

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedButton(
                onClick = {
                            openDialog.value = true
                          },
                modifier = Modifier
                    .fillMaxWidth(.75f)
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
                    .fillMaxWidth(.75f)
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
                    .fillMaxWidth(.75f)
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
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(.75f)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Billboard_Red )
            ) {
                Text( text = stringResource( R.string.delete_account ))
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

fun resetPassword(userVM: UserViewModel, email : String = ""){
    var useremail : String
    if(email.isEmpty()) useremail = userVM.userEmail.value
    else useremail = email
    FirebaseAuth.getInstance().sendPasswordResetEmail(useremail).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("Email sent to ", useremail)
        }
    }
}
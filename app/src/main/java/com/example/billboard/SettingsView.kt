package com.example.billboard

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.example.billboard.ui.theme.Billboard_Red
import com.example.billboard.ui.theme.Billboard_lightGreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingsView (
    scState: ScaffoldState,
    navControl: NavController,
    userVM: UserViewModel,
    scope: CoroutineScope,
    auth: FirebaseAuth,
    themeStore: ThemePreference,
    themeSetting: Boolean
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope ) },
        content = { SettingsContent( navControl, userVM, scState, scope, auth, themeStore, themeSetting ) },
        drawerContent = { DrawerMainScreen (
                scState,
                scope,
                DrawerContent(navControl , scState, scope )
            )
        }
    )

}


@Composable
fun SettingsContent( navControl: NavController, userVM: UserViewModel, scState: ScaffoldState, scope: CoroutineScope, auth: FirebaseAuth, themeStore: ThemePreference, themeSetting: Boolean ) {

    val ddd = themeStore.getTheme.collectAsState(initial = Boolean)
    val checkedState = remember { mutableStateOf(ddd.value) }
//    val checkedState = remember { mutableStateOf(darkMode.value) }
    val openDialog = remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(userVM.userName.value) }
    var editUserName by remember { mutableStateOf(false)}

    fun saveUserName( newUserName: String ) {
        Firebase.firestore
            .collection("users")
            .document(userVM.user.value?.email.toString())
            .update("username", newUserName)
            .addOnSuccessListener {
                userVM.setUsername(newUserName)
                editUserName = false
            }
    }

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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
//                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
//                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if ( !editUserName ) {

            Spacer( modifier = Modifier.height(70.dp))

            Text( text = userVM.userName.value, fontSize = 20.sp )

            Column(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedButton(
                    onClick = {
                        editUserName = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text( text = stringResource(R.string.edit_username))
                    }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        openDialog.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text( text = stringResource(R.string.reset_password))
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = { userVM.signOut(auth) },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text( text = stringResource( R.string.sign_out ))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text( text = stringResource(R.string.dark_mode))
                    Switch(
                        checked = themeSetting,
                        onCheckedChange = {
                            checkedState.value = it
                            //darkMode.value = it
                            scope.launch {
                                themeStore.saveTheme(it)
                            }
                            },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Billboard_green,
                            uncheckedThumbColor = Color.DarkGray,
                            checkedTrackColor = Billboard_lightGreen,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        navControl.navigate("MainScreen")
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text( text = stringResource( R.string.exit_settings ))
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = Billboard_Red),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text( text = stringResource( R.string.delete_account ))
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text(text = stringResource(R.string.username)) },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Billboard_green,
                        cursorColor = MaterialTheme.colors.onPrimary,
//                        cursorColor = Color.White,
                        textColor = MaterialTheme.colors.onPrimary,
//                        textColor = Color.White,
                        focusedLabelColor = MaterialTheme.colors.onPrimary
//                        focusedLabelColor = Color.White
                    ),
                    modifier = Modifier
                        .height(64.dp)
                        .clickable { Log.d("MESSAGE", "CLICKED") },
                    shape = MaterialTheme.shapes.large
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        saveUserName(userName)
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
//                    colors = ButtonDefaults.outlinedButtonColors( contentColor = Billboard_green )
                ) {
                    Text( text = stringResource(R.string.save))
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        userName = userVM.userName.value
                        editUserName = false
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
//                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
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
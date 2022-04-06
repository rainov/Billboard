package com.example.billboard


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.billboard.ui.theme.Bilboard_green
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope



@Composable
fun LogRegView( userVM: UserViewModel, groupsVM: GroupsViewModel, scState: ScaffoldState, scope: CoroutineScope, auth: FirebaseAuth) {

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var registerSwitch by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    val openDialog = remember { mutableStateOf(false) }
    val dialogForgotPw = remember { mutableStateOf(false) }
    var notVerified by remember { mutableStateOf(false) }
    var emailInUse by remember { mutableStateOf(false) }
    var registerSuccess by remember { mutableStateOf( false ) }

    var emailinput by remember { mutableStateOf("")}

    if (dialogForgotPw.value) {

        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.passwd_reset))
            },
            text = {
                Column(){
                    Text(text = stringResource(R.string.email_reset))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = emailinput,
                        onValueChange = { emailinput = it },
                        label = { Text(text = stringResource(R.string.email)) },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.mail_icon),
                                contentDescription = "mail icon",
                                Modifier.padding(15.dp)
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Bilboard_green,
                            cursorColor = Color.White,
                            textColor = Color.White,
                            focusedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .height(64.dp),
                        shape = MaterialTheme.shapes.large
                    )
                }

            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        if(emailinput.isNotEmpty()){
                            dialogForgotPw.value = false
                            resetPassword(userVM, emailinput)
                        }
                        Log.d("Email value", emailinput)
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.send))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        dialogForgotPw.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (openDialog.value) {

        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.input_err))
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
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
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    if (notVerified) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.not_verifdied))
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        notVerified = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    if (emailInUse) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.register_fail))
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        emailInUse = false
                        registerSwitch = !registerSwitch
                        email = ""
                        password = ""
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    if (registerSuccess) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.register_success))
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        registerSuccess = false
                        registerSwitch = !registerSwitch
                        email = ""
                        password = ""
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if(task.isSuccessful){
                        if (auth.currentUser?.isEmailVerified == true) {
                            userVM.setEmail(email)
                            auth.currentUser?.let { user -> userVM.signIn( user )}
                            groupsVM.setEmail( email )
                            Firebase.firestore.collection("users")
                                .document(email)
                                .get()
                                .addOnSuccessListener {
                                    userVM.setUsername(it.get("username").toString())
                                    groupsVM.getGroups()
                                }
                        } else {
                            errorMessage = context.getString(R.string.verify_email)
                            notVerified = true
                            auth.signOut()
                        }

                    } else {
                        errorMessage = context.getString(R.string.err_signin)
                        openDialog.value = true
                    }
                }
        } else {
            errorMessage = context.getString(R.string.all_inputs_required)
            openDialog.value = true
        }
    }

    fun register(email: String, password: String, repeatPass: String, username: String) {
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && repeatPass.isNotEmpty()) {
            if (password == repeatPass) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val user = hashMapOf<String, Any>(
                            "username" to username
                        )
                        Firebase.firestore.collection("users")
                            .document(email)
                            .set(user)
                            .addOnSuccessListener {
                                auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                                    if ( it.isSuccessful ) {
                                        auth.currentUser?.sendEmailVerification()
                                        errorMessage = context.getString(R.string.verification_email)
                                        registerSuccess = true
                                        auth.signOut()
                                    }
                                }
                            }
                    }
                    .addOnFailureListener {
                        errorMessage = it.message.toString()
                        emailInUse = true
                    }
            } else {
                errorMessage = context.getString(R.string.passwords_not_match)
                openDialog.value = true
            }
        } else {
            errorMessage = context.getString(R.string.all_inputs_required)
            openDialog.value = true
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //Logo start
                Spacer(modifier = Modifier.height(20.dp))
                TopBar(false, scState, false, scope )
                Spacer(modifier = Modifier.height(20.dp))
                //Logo end

                //Username start
                if (registerSwitch) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(text = stringResource(R.string.username)) },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Bilboard_green,
                            cursorColor = Color.White,
                            textColor = Color.White,
                            focusedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .height(64.dp)
                            .padding(0.dp),
                        shape = MaterialTheme.shapes.large
                    )
                }
                //Username end

                //Email start
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = stringResource(R.string.email)) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.mail_icon),
                            contentDescription = "mail icon",
                            Modifier.padding(15.dp)
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Bilboard_green,
                        cursorColor = Color.White,
                        textColor = Color.White,
                        focusedLabelColor = Color.White
                    ),
                    modifier = Modifier
                        .height(64.dp),
                    shape = MaterialTheme.shapes.large
                )
                //Email end

            var showPassWd by remember { mutableStateOf(false) }
            var showPassWd2 by remember { mutableStateOf(false) }

            //Password start
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(R.string.password)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.password_eye),
                        contentDescription = "eye password",
                        Modifier
                            .padding(15.dp)
                            .clickable { showPassWd = !showPassWd }
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Bilboard_green,
                    cursorColor = Color.White,
                    textColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                visualTransformation = if (showPassWd) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.height(64.dp),
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            //Password end

            //Repeat password start
            if (registerSwitch) {
                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    label = { Text(text = stringResource(R.string.repeat_password)) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.password_eye),
                            contentDescription = "eye password",
                            Modifier
                                .padding(15.dp)
                                .clickable { showPassWd2 = !showPassWd2 }
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Bilboard_green,
                        cursorColor = Color.White,
                        textColor = Color.White,
                        focusedLabelColor = Color.White
                    ),
                    visualTransformation = if (showPassWd2) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.height(64.dp),
                    shape = MaterialTheme.shapes.large,
                    //textStyle = TextStyle(color = Bilboard_green)
                )
            }
            //Repeat password end

            Spacer(modifier = Modifier.height(5.dp))

            //SignIn button start
            if (!registerSwitch) {
                OutlinedButton(
                    onClick = {
                        login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.sign_in_text))
                }
                //SignIn button end

                Spacer(modifier = Modifier.height(5.dp))

                //Change to register view start
                OutlinedButton(
                    onClick = {
                        registerSwitch = !registerSwitch
                        email = ""
                        password = ""
                        repeatPassword = ""
                        username = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(text = stringResource(R.string.new_user_text))
                }
                //Change to register view end

                Spacer(modifier = Modifier.height(5.dp))

                //Reset password start
                OutlinedButton(
                    onClick = {
                        dialogForgotPw.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                )
                {
                    Text(text = stringResource(R.string.forgot_pwd))
                }
                //reset password end
            }

            //Register Button start
            if (registerSwitch) {
                OutlinedButton(
                    onClick = {
                        register(email, password, repeatPassword, username)
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.register_text))
                }
                //Register button end


                Spacer(modifier = Modifier.height(5.dp))

                //Change to signIn start
                OutlinedButton(
                    onClick = {
                        registerSwitch = !registerSwitch
                        email = ""
                        password = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                )
                {
                    Text(text = stringResource(R.string.registered_user_text))
                }
                //Change to signIn end
            }
        }
    }
}


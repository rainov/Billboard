package com.example.billboard.ui.theme


import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.billboard.R
import com.example.billboard.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogRegView( userVM: UserViewModel){

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var registerSwitch by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var fieldError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    fun login( email: String, password: String) {
        if ( email.isNotEmpty() && password.isNotEmpty() ) {
            fieldError = false
            auth.signInWithEmailAndPassword( email, password)
                .addOnSuccessListener(){
                    userVM.signIn()
                }
        } else {
            errorMessage = context.getString(R.string.all_inputs_required)
            fieldError = true
        }
    }

    fun register( email: String, password: String, repeatPass: String) {
        if( username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && repeatPass.isNotEmpty() ) {
            if ( password == repeatPass ) {
                fieldError = false
                auth.createUserWithEmailAndPassword( email, password)
                    .addOnSuccessListener(){
                        userVM.signIn()
                    }
            } else {
                errorMessage = context.getString(R.string.passwords_not_match)
                fieldError = true
            }
        } else {
            errorMessage = context.getString(R.string.all_inputs_required)
            fieldError = true
        }
    }

    Column() {
        if( registerSwitch ) {
            OutlinedTextField(value = username, onValueChange = { username = it}, label = { Text( text = "Username")})
        }
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text( text = "Email")})
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text( text = "Password")})
        if ( registerSwitch ) {
            OutlinedTextField(value = repeatPassword, onValueChange = { repeatPassword = it }, label = { Text( text = "Repeat password")})
        }
        if( fieldError ) {
            Text( text = errorMessage )
        }
        if ( !registerSwitch ) {
            OutlinedButton(onClick = { login( email, password) }) {
                Text( text = stringResource(R.string.sign_in_text))
            }
            OutlinedButton(
                onClick = {
                    registerSwitch = !registerSwitch
                    email = ""
                    password = ""
                    repeatPassword = ""
                    username = ""
                }
            ) {
                Text( text = stringResource(R.string.new_user_text))
            }
        }
        if ( registerSwitch ) {
            OutlinedButton(onClick = { register( email, password, repeatPassword) }) {
                Text( text = stringResource(R.string.register_text))
            }
            OutlinedButton(
                onClick = {
                    registerSwitch = !registerSwitch
                    email = ""
                    password = ""
                }
            )
            {
                Text( text = stringResource(R.string.registered_user_text))
            }
        }

    }
}
package com.example.billboard.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.example.billboard.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogRegView( userVM: UserViewModel){

    var username by remember { mutableStateOf("") }
    var registerSwitch by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    fun login( email: String, password: String) {
        auth.signInWithEmailAndPassword( email, password)
            .addOnSuccessListener(){
                Log.d("tag", "success")
                userVM.signIn()
            }
    }

    fun register( email: String, password: String, repeatPass: String) {
        if ( password == repeatPass ) {
            auth.createUserWithEmailAndPassword( email, password)
                .addOnSuccessListener(){
                    Log.d("tag", "success")
                }
        } else {
            repeatPassword = "passwords do not match"
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
        if ( !registerSwitch ) {
            OutlinedButton(onClick = { login( email, password) }) {
                Text( text = "Sign in")
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
                Text( text = "New user")
            }
        }
        if ( registerSwitch ) {
            OutlinedButton(onClick = { register( email, password, repeatPassword) }) {
                Text( text = "Register")
            }
            OutlinedButton(
                onClick = {
                    registerSwitch = !registerSwitch
                    email = ""
                    password = ""
                }
            )
            {
                Text( text = "Registered? Sign in")
            }
        }
    }
}
package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class UserViewModel: ViewModel() {
    var signedIn = mutableStateOf(false)
    var userEmail = mutableStateOf("")

    fun setEmail( email: String ){
        userEmail.value = email
    }

    fun signIn() {
        signedIn.value = true
        Log.d("message", "SignedIn")
    }
    fun signOut() {
        signedIn.value = false
    }
}
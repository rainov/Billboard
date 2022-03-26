package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserViewModel: ViewModel() {

    var signedIn = mutableStateOf(false)
    var userEmail = mutableStateOf("")
    var userName = mutableStateOf("")

    fun setEmail( email: String ){
        userEmail.value = email
    }

    fun setUsername( username: String ){
        userName.value = username
    }

    fun signIn() {
        signedIn.value = true
        Log.d("message", "SignedIn")
    }
    fun signOut() {
        signedIn.value = false
    }
}
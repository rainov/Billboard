package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class UserViewModel: ViewModel() {

    var userEmail = mutableStateOf("")
    var userName = mutableStateOf("")

    var user = mutableStateOf<FirebaseUser?>(null)

    fun setUser( userAuth: FirebaseUser? ) {
        user.value = userAuth
    }

    fun setEmail( email: String ){
        userEmail.value = email
    }

    fun setUsername( username: String ){
        userName.value = username
    }

    fun signIn( userInfo: FirebaseUser ) {
        user.value = userInfo
        Log.d("message", "SignedIn")
    }

    fun signOut( auth: FirebaseAuth) {
        auth.signOut()
        user.value = null
    }
}
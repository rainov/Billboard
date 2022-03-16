package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {
    var signedIn = mutableStateOf(false)

    fun signIn() {
        signedIn.value = true
        Log.d("message", "ok")
    }
    fun signOut() {
        signedIn.value = false
    }
}
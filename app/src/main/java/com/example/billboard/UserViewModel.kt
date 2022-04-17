package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.util.*


class UserViewModel: ViewModel() {

    var userEmail = mutableStateOf("")
    var userName = mutableStateOf("")
    var uniqueID = mutableStateOf("")

    var user = mutableStateOf<FirebaseUser?>(null)

    fun setUser( userAuth: FirebaseUser? ) {
        user.value = userAuth
    }

    fun setUniqueId( id: String ) {
        uniqueID.value = id
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

    fun logAction( actionType: String ) {
        val time = Calendar.getInstance().time.toString()
        val newLog = LogAction( uniqueID.value, time, actionType )
        Firebase.firestore
            .collection("users")
            .document( userEmail.value)
            .update("logs", FieldValue.arrayUnion(newLog))
    }

}
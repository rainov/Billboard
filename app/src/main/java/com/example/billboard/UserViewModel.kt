package com.example.billboard

/*===================================================/
|| The UserViewModel stores the current logged in user
|| data (email, username and id for the logs)
/====================================================*/

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class UserViewModel: ViewModel() {

    var userEmail = mutableStateOf("")
    var userName = mutableStateOf("")
    private var uniqueID = mutableStateOf("")

    var user = mutableStateOf<FirebaseUser?>(null)

    //Setter functions for the different parameters

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

    //Functions to store/remove the user value depends if the user sign in/out
    fun signIn( userInfo: FirebaseUser ) {
        user.value = userInfo
        Log.d("message", "SignedIn")
    }

    fun signOut( auth: FirebaseAuth) {
        auth.signOut()
        user.value = null
    }

    //Function to store the user's logs from the actionType passed as parameter in the database
    fun logAction( actionType: String ) {
        val time = Calendar.getInstance().time.toString()
        val newLog = LogAction( uniqueID.value, time, actionType )
        Firebase.firestore
            .collection("users")
            .document( userEmail.value)
            .update("logs", FieldValue.arrayUnion(newLog))
    }

}
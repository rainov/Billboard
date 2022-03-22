package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GroupsViewModel: ViewModel() {

    var groups = mutableStateOf( listOf<DocumentSnapshot>() )
    private var userEmail = mutableStateOf("")

    fun setEmail( email: String ) {
        userEmail.value = email
        Log.d("msg", userEmail.value)
    }

    fun getGroups() {
        Firebase.firestore
            .collection("groups")
            .whereArrayContains("members", userEmail.value)
            .addSnapshotListener { value, error ->
                if ( error != null ) {
                    error.message?.let { Log.d("err message: ", it) }
                } else if ( value != null && !value.isEmpty ) {
                    val tempGroups = mutableListOf<DocumentSnapshot>()
                    value.documents.forEach { group ->
                        tempGroups.add( group )
                        Log.d("groups log:" , "not good!")
                    }
                    groups.value = tempGroups
                }
            }
    }

    fun createGroup( name: String) {
        val adminsList: List<String> = listOf(userEmail.value)
        val expensesList: List<String> = listOf()
        val membersList: List<String> = listOf(userEmail.value)
        val groupName: String = name
        val newGroup = GroupClass( adminsList, expensesList, membersList, groupName)
        Log.d("group: ", newGroup.toString())
        Firebase.firestore
            .collection("groups")
            .add(newGroup)
    }

}
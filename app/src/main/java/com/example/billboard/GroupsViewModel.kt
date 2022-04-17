package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GroupsViewModel: ViewModel() {

//    var userEmail = Firebase.auth.currentUser?.email.toString()
    var userEmail = mutableStateOf("")
    fun setEmail( email: String ) {
        userEmail.value = email
    }

    var groups = mutableStateOf( listOf<GroupClass>())

    fun getGroups() {
        Log.d("EMAIL", userEmail.value)
        Log.d("getGroups called ", "TRUE")
        Firebase.firestore
            .collection("groups")
            .whereArrayContains("members", userEmail.value )
            .addSnapshotListener { value, error ->
                if ( error != null ) {
                    error.message?.let { Log.d("err message: ", it) }
                } else if ( value != null && !value.isEmpty ) {
                    val tempGroupClasses = mutableListOf<GroupClass>()
                    value.documents.forEach { group ->
                        val tempSingleGroup = GroupClass(
                            group.get("admins") as List<String> ,
                            group.get("expenses") as List<String> ,
                            group.get("members") as List<String> ,
                            group.get("name").toString(),
                            group.get("balance") as MutableMap<String, MutableMap<String, Double>>,
                            group.id
                        )
                        tempGroupClasses.add(tempSingleGroup)
                    }
                    groups.value = tempGroupClasses
                } else if ( value != null && value.isEmpty  ){
                    groups.value = emptyList()
                }
            }
    }

    fun createGroup( name: String, navControl: NavController, userVM: UserViewModel) {
        val adminsList: List<String> = listOf(userEmail.value)
        val expensesList: List<String> = listOf()
        val membersList: List<String> = listOf(userEmail.value)
        val groupName: String = name
        val balance = mutableMapOf(membersList[0] to mutableMapOf<String, Double>())
        val newGroup = GroupClass( adminsList, expensesList, membersList, groupName, balance, "" )
        Log.d("Current user", userEmail.value)
        Firebase.firestore
            .collection("groups")
            .add(newGroup)
            .addOnSuccessListener { group ->
                Firebase.firestore
                    .collection("groups")
                    .document(group.id)
                    .update("id", group.id)
                userVM.logAction("Create group")
                navControl.navigate(group.id)
            }
    }

    fun editGroup( group: GroupClass, userVM: UserViewModel, actionType: String ) {
        Firebase.firestore
            .collection("groups")
            .document(group.id)
            .set( group )
            .addOnSuccessListener {
                getGroups()
                userVM.logAction(actionType)
            }
    }

    fun deleteGroup(group : GroupClass, userVM: UserViewModel) {
        //First delete all expenses related to the group
        val fexp = Firebase.firestore.collection("expenses")
        val fgrp = Firebase.firestore.collection("groups")

        group.expenses.forEach { exp ->
            fexp.document(exp).delete()
        }

        //Then delete the group
        fgrp.document(group.id).delete().addOnSuccessListener {
            userVM.logAction("Deleted group")
        }

        getGroups()
    }
}
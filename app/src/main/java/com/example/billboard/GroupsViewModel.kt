package com.example.billboard

/*===================================================/
|| The view model controlling all actions for the
|| groups.
/====================================================*/

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class GroupsViewModel: ViewModel() {

    //////////////////////////////////////////////////////////////////////////////////
    // The app uses the email of the users to filter the groups from the database. //
    // We did that so when people who are not yet registered to the app, but are  //
    // added to some group by registered user, will have all the groups visible  //
    // when they register.                                                      //
    /////////////////////////////////////////////////////////////////////////////
    private var userEmail = mutableStateOf("")
    fun setEmail( email: String ) {
        userEmail.value = email
    }

    ////////////////////////////////////////////////////////////////////
    // All fetched groups are stored here as a list of Group classes //
    //////////////////////////////////////////////////////////////////
    var groups = mutableStateOf( listOf<GroupClass>())

    ////////////////////////////////////////////////////////////////
    // Get groups function that fetches all corresponding to the //
    // user email groups and stores them in the groups list     //
    /////////////////////////////////////////////////////////////
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

    ////////////////////////////////
    // Create new group function //
    //////////////////////////////
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

    //////////////////////////
    // Edit group function //
    ////////////////////////
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

    ////////////////////////////
    // Delete group function //
    //////////////////////////
    fun deleteGroup(group : GroupClass, userVM: UserViewModel) {

        val fexp = Firebase.firestore.collection("expenses")
        val fgrp = Firebase.firestore.collection("groups")

        /////////////////////////////////////////////////////
        // First delete all expenses related to the group //
        ///////////////////////////////////////////////////
        group.expenses.forEach { exp ->
            fexp.document(exp).delete()
        }

        ////////////////////////////
        // Then delete the group //
        //////////////////////////
        fgrp.document(group.id).delete().addOnSuccessListener {
            userVM.logAction("Deleted group")
        }
        getGroups()
    }
}
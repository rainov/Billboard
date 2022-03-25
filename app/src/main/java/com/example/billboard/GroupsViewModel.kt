package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GroupsViewModel: ViewModel() {

    var groups = mutableStateOf( listOf<DocumentSnapshot>() )

    var userEmail = FirebaseAuth.getInstance().currentUser.toString()

    var groupClasses = mutableStateOf( listOf<GroupClass>())



    fun getGroups() {
        Log.d("user0email:", userEmail)
        Firebase.firestore
            .collection("groups")
            .whereArrayContains("members", userEmail)
            .addSnapshotListener { value, error ->
                if ( error != null ) {
                    error.message?.let { Log.d("err message: ", it) }
                } else if ( value != null && !value.isEmpty ) {
                    val tempGroups = mutableListOf<DocumentSnapshot>()
                    val tempGroupClasses = mutableListOf<GroupClass>()
                    value.documents.forEach { group ->
                        val tempSingleGroup = GroupClass(
                            group.get("admins") as List<String> ,
                            group.get("expenses") as List<String> ,
                            group.get("members") as List<String> ,
                            group.get("name").toString(),
                            group.get("balance") as Map<String, Map<String, Map<String, Double>>>,
                            group.id
                        )
                        tempGroups.add(group)
                        tempGroupClasses.add(tempSingleGroup)
                        Log.d("groupClass: ", tempSingleGroup.toString())
                        Log.d("Group balance: " , tempSingleGroup.balance.toString())
                        Log.d("User1 collect balance", tempSingleGroup.balance["collectingmoney"]?.get("user1").toString())
                    }
                    groups.value = tempGroups
                    groupClasses.value = tempGroupClasses
                }
            }
    }

    fun createGroup( name: String) {
        val adminsList: List<String> = listOf(userEmail)
        val expensesList: List<String> = listOf()
        val membersList: List<String> = listOf(userEmail)
        val groupName: String = name
        val newGroup = GroupClass( adminsList, expensesList, membersList, groupName, id = "" )
        Log.d("group: ", newGroup.toString())
        Firebase.firestore
            .collection("groups")
            .add(newGroup)
            .addOnSuccessListener { group ->
                Firebase.firestore
                    .collection("groups")
                    .document(group.id)
                    .update("id", group.id)
            }
    }

}
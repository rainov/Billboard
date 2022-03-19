package com.example.billboard

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.billboard.ui.theme.LogRegView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ViewContainer(){

    val firestore = Firebase.firestore

    var groups by remember { mutableStateOf(mutableListOf<QueryDocumentSnapshot>()) }
    firestore
        .collection("groups")
        .get()
        .addOnSuccessListener { groupList ->
            val tempGroup = mutableListOf<QueryDocumentSnapshot>()
            for( group in groupList) {
                tempGroup.add(group)
            }
            groups = tempGroup
            Log.d("mss", groups.toString())
        }

    val userVM: UserViewModel = viewModel()

    if (!userVM.signedIn.value) {
        LogRegView( userVM )
    } else {
        val navControl = rememberNavController()
        NavHost(navController = navControl, startDestination = "MainScreen") {
            composable(route = "MainScreen") {
                MainScreen(navControl, groups)
            }
            groups.forEach { groupInfo ->
                composable(route = groupInfo.get("name").toString()) {
                    GroupViewNavigationContainer(navControl, groupInfo)
                }
            }
        }
    }
}
package com.example.billboard

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ViewContainer( scope: CoroutineScope, themeStore: ThemePreference, themeSetting: Boolean ){

    val affiliateVM: AffiliatePartnersViewModel = viewModel()

    val userVM: UserViewModel = viewModel()

    val groupsVM: GroupsViewModel = viewModel()

    val groups = groupsVM.groups.value

    val auth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser
    if (user != null) {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userVM.setUniqueId(task.result)
            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }
        userVM.setUser( user )
        groupsVM.setEmail(user.email.toString())
        userVM.setEmail(user.email.toString())
        groupsVM.getGroups()
        affiliateVM.getPartners()
        Firebase.firestore
            .collection("users")
            .document(user.email.toString())
            .get()
            .addOnSuccessListener { userName ->
                userVM.setUsername( userName.get("username").toString())
            }
    }

    val scState = rememberScaffoldState(
        rememberDrawerState(initialValue = DrawerValue.Closed)
    )

    if (userVM.user.value == null) {
        LogRegView( userVM, groupsVM, scState, scope, auth )
    } else {
        val navControl = rememberNavController()
        NavHost(navController = navControl, startDestination = "MainScreen") {
            composable(route = "MainScreen") {
                MainScreen(navControl, groups, groupsVM, scState, scope )
            }
            composable( route = "CreateGroup") {
                CreateGroupView( groupsVM, navControl, scState, scope, userVM )
            }
            composable( route = "Affiliate") {
                AffiliateNavContainer( navControl, scState, scope, affiliateVM, userVM )
            }
            groups.forEach { groupInfo ->
                composable(route = groupInfo.id) {
                    GroupViewNavigationContainer( navControl, groupInfo, scState, groupsVM, scope, userVM )
                }

            }
            composable( route = "Settings") {
                SettingsView( scState, navControl, userVM, scope, auth, themeStore, themeSetting )
            }
            composable( route = "About") {
                AboutUs( scState, navControl, scope )
            }
        }
    }
}
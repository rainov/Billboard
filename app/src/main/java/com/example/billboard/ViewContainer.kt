package com.example.billboard

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ViewContainer(){

    val userVM: UserViewModel = viewModel()

    val groupsVM: GroupsViewModel = viewModel()

    val groups = groupsVM.groups.value

    if (!userVM.signedIn.value) {
        LogRegView( userVM, groupsVM )
    } else {
        val navControl = rememberNavController()
        NavHost(navController = navControl, startDestination = "MainScreen") {
            composable(route = "MainScreen") {
                MainScreen(navControl, groups)
            }
            groups.forEach { groupInfo ->
                //changed the navigation parameter to group ID so there are no conflicts if we have groups with the same name
                composable(route = groupInfo.id) {
                    GroupViewNavigationContainer( navControl, groupInfo )
                }
            }
        }
    }
}
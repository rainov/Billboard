package com.example.billboard

import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ViewContainer(){

    val scope = rememberCoroutineScope()

    val userVM: UserViewModel = viewModel()

    val groupsVM: GroupsViewModel = viewModel()

    val groups = groupsVM.groups.value

    val scState = rememberScaffoldState(
        rememberDrawerState(initialValue = DrawerValue.Closed)
    )

    if (!userVM.signedIn.value) {
        LogRegView( userVM, groupsVM, scState, scope )
    } else {
        val navControl = rememberNavController()
        NavHost(navController = navControl, startDestination = "MainScreen") {
            composable(route = "MainScreen") {
                MainScreen(navControl, groups, groupsVM, scState, scope )
            }
            composable( route = "CreateGroup") {
                CreateGroupView( groupsVM, navControl, scState, scope )
            }
            groups.forEach { groupInfo ->
                //changed the navigation parameter to group ID so there are no conflicts if we have groups with the same name
                composable(route = groupInfo.id) {

                    GroupViewNavigationContainer( navControl, groupInfo, scState, groupsVM, scope )
                }
            }
            composable( route = "Settings") {
                SettingsView( scState, navControl, userVM, scope )
            }
        }
    }
}
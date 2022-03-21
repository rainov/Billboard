package com.example.billboard

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ViewContainer(){

<<<<<<< HEAD
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


=======
>>>>>>> 94210a1a64dca181564c42fd804cf79676c897cf
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
<<<<<<< HEAD

=======
>>>>>>> 94210a1a64dca181564c42fd804cf79676c897cf
}

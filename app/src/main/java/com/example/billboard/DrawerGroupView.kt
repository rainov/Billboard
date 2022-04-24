package com.example.billboard

/*===================================================/
|| Composable for the content of the app drawer in
|| Group view
/====================================================*/

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



////////////////////////////////////////////////////////////////////////
// Main scaffold structure, the content is passed from the app views //
//////////////////////////////////////////////////////////////////////
@Composable
fun DrawerGroupView(
    navControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    groupInfo: GroupClass,
    expenseNavControl: NavController,
    userVM: UserViewModel,
    groupsVM: GroupsViewModel
) {
    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(false, scState, true, scope) },
        content = {
            DrawerGroupContent(
                navControl,
                scState,
                scope,
                groupInfo,
                expenseNavControl,
                userVM,
                groupsVM
            )
        }
    )
}

///////////////////////////////////////////////////
// Drawer content for the Group view of the app //
/////////////////////////////////////////////////
@SuppressLint("MutableCollectionMutableState")
@Composable
fun DrawerGroupContent( navControl: NavController,
                        scState: ScaffoldState,
                        scope: CoroutineScope,
                        groupInfo: GroupClass,
                        expenseNavControl: NavController,
                        userVM : UserViewModel,
                        groupsVM: GroupsViewModel
) {

    var membersList by remember { mutableStateOf(groupInfo.members) }
    var adminsList by remember { mutableStateOf(groupInfo.admins) }
    val newBalance by remember { mutableStateOf(groupInfo.balance) }

    val alertDialogAdmin = remember { mutableStateOf(false)}
    val alertDialogBalance = remember { mutableStateOf(false)}

    fun leaveGroup() {
        val member = userVM.userEmail.value

        /* Delete members line from group balance */
        membersList.forEach { m ->
            if (m == member) {
                newBalance.remove(m)
            } else {
                //In each member balance, remove member balance
                newBalance[m]?.remove(member)
            }
        }

        /* Delete from admins */
        if (groupInfo.admins.contains(member)) {
            val tempAdmins = mutableListOf<String>()
            adminsList.forEach { admin -> if (admin != member) tempAdmins.add(admin) }
            adminsList = tempAdmins
        }

        /* Delete from members */
        val tempMembers = mutableListOf<String>()
        membersList.forEach { m -> if (m != member) tempMembers.add(m) }
        membersList = tempMembers


        /////////////////////////////////////////////////////////////////
        // New group info for updating firebase data with information //
        ///////////////////////////////////////////////////////////////
        val newGroup = GroupClass(
            adminsList,
            groupInfo.expenses,
            membersList,
            groupInfo.name,
            newBalance,
            groupInfo.id
        )

        groupsVM.editGroup(newGroup, userVM, "User left a group" )
    }

    fun isBalanceClear() : Boolean {
        groupInfo.balance[userVM.userEmail.value]?.forEach { other ->
            if (other.value != 0.0) return false
        }
        return true
    }

    fun isOneAdminLeft() : Boolean {
        return groupInfo.admins.contains(userVM.userEmail.value) && groupInfo.admins.size >= 2 || !groupInfo.admins.contains(userVM.userEmail.value)
    }

    ///////////////////////
    // Container column //
    /////////////////////
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween,
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /////////////////////////////
        // Top part of the screen //
        ///////////////////////////
        Column(
            modifier = Modifier.weight(.45f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(15.dp))

            ////////////////////////////
            // Headline - Group name //
            //////////////////////////
            Text(text = groupInfo.name, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(15.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.85f),
                color = Billboard_green
            )
        }

        ////////////////////////////////
        // Middle part of the screen //
        //////////////////////////////
        Column(
            modifier = Modifier
                .weight(2.5f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .fillMaxHeight(.65f)
                    .verticalScroll(rememberScrollState())
            ) {
                ///////////////////////////////////////////////////////////////////////
                // Email and admin indicator ( if the user is admin ) for each user //
                /////////////////////////////////////////////////////////////////////
                groupInfo.members.forEach { member ->
                    Row {
                        // Email
                        Text(text = member, fontSize = 16.sp)

                        Spacer(modifier = Modifier.width(5.dp))

                        // Admin indicator
                        if (groupInfo.admins.contains(member)) {
                            Text(text = "Admin", fontSize = 11.sp, color = Billboard_green)
                        }
                    }

                    Row{
                        // Username if registered user
                        val uname = remember { mutableStateOf("default")}
                        getUsername(member, uname)
                        if(uname.value == "null") uname.value = member.substringBefore("@")
                        Text(text = "(" + uname.value + ")")
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }

            }

            ////////////////////////////////////////////////////
            // Container column for the group action buttons //
            //////////////////////////////////////////////////
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (groupInfo.admins.contains(userVM.userEmail.value)) {
                    /////////////////////////////////////////////////////////
                    // Edit members button, visible only for group admins //
                    ///////////////////////////////////////////////////////
                    OutlinedButton(
                        onClick = {
                            expenseNavControl.navigate("addMembers")
                            scope.launch { scState.drawerState.close() }
                        },
                        modifier = Modifier
                            .fillMaxWidth(.85f)
                            .height(60.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                    ) {
                        Text(text = stringResource(R.string.edit_members))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                /////////////////////////
                // Leave group button //
                ///////////////////////
                OutlinedButton(
                    onClick = {
                        if(isBalanceClear()){
                            if(isOneAdminLeft()){
                            leaveGroup()
                            scope.launch { scState.drawerState.close() }
                        } else {
                            alertDialogAdmin.value = true
                        }} else {
                            alertDialogBalance.value = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                        .height(60.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(text = stringResource(R.string.leave_grp))
                }

                Spacer(modifier = Modifier.height(10.dp))

            }
        }

        ////////////////////////////////
        // Bottom part of the screen //
        //////////////////////////////
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {

            Divider(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(1.dp),
                color = Billboard_green
            )

            Spacer(modifier = Modifier.height(15.dp))

            //////////////////////
            // Settings button //
            ////////////////////
            OutlinedButton(
                onClick = {
                    navControl.navigate("Settings")
                    scope.launch { scState.drawerState.close() }
                },
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(60.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Text(text = stringResource(R.string.settings))
            }

            Spacer(modifier = Modifier.height(10.dp))

            //////////////////////
            // About us button //
            ////////////////////
            OutlinedButton(
                onClick = {
                    navControl.navigate("About")
                    scope.launch { scState.drawerState.close() }
                },
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(60.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Text(text = stringResource(R.string.about))
            }

            Spacer(modifier = Modifier.height(15.dp))

            Divider(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(1.dp),
                color = Billboard_green
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Error dialog pop up for when trying top leave a group and the balance is dirty //
    ///////////////////////////////////////////////////////////////////////////////////
    if(alertDialogBalance.value){
        AlertDialog(
            onDismissRequest = {
                alertDialogBalance.value = false
            },
            title = {
                Text(text = stringResource(R.string.error))
            },
            text = {
                Text(text = stringResource(R.string.err_balance_clear))
            },
            confirmButton = {
                ////////////////////
                // Cancel button //
                //////////////////
                OutlinedButton(
                    onClick = {
                        alertDialogBalance.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Error dialog pop up for when trying top leave a group and you are the only admin in the group //
    //////////////////////////////////////////////////////////////////////////////////////////////////
    if(alertDialogAdmin.value){
        AlertDialog(
            onDismissRequest = {
                alertDialogAdmin.value = false
            },
            title = {
                Text(text = stringResource(R.string.error))
            },
            text = {
                Text(text = stringResource(R.string.err_admin_left))
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        alertDialogAdmin.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}
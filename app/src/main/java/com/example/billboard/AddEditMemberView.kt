package com.example.billboard

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import kotlinx.coroutines.CoroutineScope

@Composable
fun AddEditMemberView(groupsVM: GroupsViewModel, expenseNavControl: NavController, scState: ScaffoldState, scope: CoroutineScope, group: GroupClass ) {
    Scaffold(
        topBar = { TopBar(showMenu = true, scState, false, scope ) },
        content = { AddEditMemberContent( groupsVM, expenseNavControl, group ) }
    )

}

@Composable
fun AddEditMemberContent( groupsVM: GroupsViewModel, expenseNavControl: NavController, group: GroupClass ) {

    var memberEmail by remember { mutableStateOf("") }
    var membersList by remember { mutableStateOf(group.members) }
    var adminsList by remember { mutableStateOf(group.admins) }
    var adminCheck by remember { mutableStateOf(false) }
    var editGroup by remember { mutableStateOf(group) }
    var newBalance by remember { mutableStateOf( group.balance ) }

    fun addMember() {
        val newMemberBalanceMap = mutableMapOf<String, Double>()
        membersList.forEach { member ->
            val oldMemberBalance = editGroup.balance[member]!!
            val newMemberBalance = mutableMapOf(memberEmail to 0.0)
            newMemberBalanceMap[member] = 0.0
            oldMemberBalance[memberEmail] = 0.0
            newBalance[member] = oldMemberBalance
            Log.d("oldMemberBalance", oldMemberBalance.toString())
            Log.d("newMemberBalance", newMemberBalance.toString())
        }
        newBalance[memberEmail] = newMemberBalanceMap
        val tempMembers = mutableListOf<String>()
        membersList.forEach { member -> tempMembers.add(member) }
        tempMembers.add(memberEmail)
        membersList = tempMembers
        Log.d("Members ====> ", membersList.toString())
        if (adminCheck) {
            val tempAdmins = mutableListOf<String>()
            adminsList.forEach { admin -> tempAdmins.add(admin) }
            tempAdmins.add(memberEmail)
            adminsList = tempAdmins
            Log.d("Admins ====> ", adminsList.toString())
        }
        val newGroup = GroupClass(adminsList, group.expenses, membersList, group.name, newBalance, group.id)
        Log.d("NewGroup: ", newGroup.toString())
        editGroup = newGroup
        newBalance = newGroup.balance
        groupsVM.editGroup(newGroup)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(text = group.name, fontSize = 30.sp)

                Spacer(modifier = Modifier.height(20.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(1.dp),
                    color = Billboard_green
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = memberEmail,
                    onValueChange = { memberEmail = it },
                    label = { Text(text = stringResource(R.string.member_email)) },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Billboard_green,
                        cursorColor = Color.White,
                        textColor = Color.White,
                        focusedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(64.dp),
                    shape = MaterialTheme.shapes.large,
                    textStyle = TextStyle(color = Billboard_green)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = stringResource(R.string.add_as_admin))

                    Checkbox(
                        checked = adminCheck,
                        onCheckedChange = { adminCheck = it },
                        colors = CheckboxDefaults.colors(Billboard_green)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = { addMember() },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                ) {
                    Text(text = stringResource(R.string.add_member))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(1.dp),
                    color = Billboard_green
                )

                Spacer(modifier = Modifier.height(20.dp))

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(.8f)
                    .verticalScroll(enabled = true, state = ScrollState(1))
            ) {
                membersList.forEach { member ->
                    Text(text = member, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .width(80.dp)
                                .height(35.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                        ) {
                            Text(text = stringResource(R.string.edit))
                        }
                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .width(150.dp)
                                .height(35.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                        ) {
                            Text(text = stringResource(R.string.make_admin))
                        }
                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .width(80.dp)
                                .height(35.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                        ) {
                            Text(text = stringResource(R.string.delete))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(.75f)
                            .height(1.dp),
                        color = Billboard_green
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    expenseNavControl.navigate("group")
                },
                modifier = Modifier
                    .fillMaxWidth(.75f)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
            ) {
                Text(text = stringResource(R.string.cancel))
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

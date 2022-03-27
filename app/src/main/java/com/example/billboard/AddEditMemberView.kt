package com.example.billboard

import android.util.Log
import androidx.compose.foundation.layout.*
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
import com.example.billboard.ui.theme.Bilboard_green
import kotlinx.coroutines.CoroutineScope

@Composable
fun AddEditMemberView(groupsVM: GroupsViewModel, expenseNavControl: NavController, scState: ScaffoldState, scope: CoroutineScope, group: GroupClass) {
    Scaffold(
        topBar = { TopBar(showMenu = true, scState, false, scope ) },
        content = { AddEditMemberContent( groupsVM, expenseNavControl, group ) }
    )

}

@Composable
fun AddEditMemberContent( groupsVM: GroupsViewModel, expenseNavControl: NavController, group: GroupClass) {

    var memberEmail by remember { mutableStateOf("") }
    var membersList by remember { mutableStateOf(group.members) }
    var adminsList by remember { mutableStateOf(group.admins) }
    var adminCheck by remember { mutableStateOf(false) }
    val newBalance = mutableMapOf<String, MutableList<MutableMap<String, Double>>>()
    val newMemberBalanceList = mutableListOf<MutableMap<String, Double>>()

    fun addMember() {
        val members = membersList
        members.forEach { member ->
            val oldMemberBalance = group.balance[member]!!.toMutableList()
            val newMemberBalance = mutableMapOf( memberEmail to 0.0)
            newMemberBalanceList.add(mutableMapOf(member to 0.0))
            oldMemberBalance.add(newMemberBalance)
            newBalance[member] = oldMemberBalance
            Log.d("oldMemberBalance", oldMemberBalance.toString())
            Log.d("newMemberBalance", newMemberBalance.toString())
        }
        newBalance[memberEmail] = newMemberBalanceList
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
        Log.d("TESTTTTT:", newBalance.toString())
        val newGroup = GroupClass(adminsList, group.expenses, membersList, group.name, newBalance, group.id)
        Log.d("NewGroup: ", newGroup.toString())
        groupsVM.editGroup(newGroup)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column (
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(20.dp))

            Text( text = group.name, fontSize = 30.sp )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = memberEmail,
                onValueChange = { memberEmail = it },
                label = { Text(text = stringResource(R.string.member_email)) },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Bilboard_green,
                    cursorColor = Color.White,
                    textColor = Color.White,
                    focusedLabelColor = Color.White
                ),
                modifier = Modifier.height(64.dp),
                shape = MaterialTheme.shapes.large,
                textStyle = TextStyle(color = Bilboard_green)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text( text = stringResource(R.string.add_as_admin))

                Checkbox(
                    checked = adminCheck,
                    onCheckedChange = { adminCheck = it },
                    colors = CheckboxDefaults.colors(Bilboard_green)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = ::addMember,
                modifier = Modifier
                    .width(280.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
            ) {
                Text(text = stringResource(R.string.add_member))
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = {
                    expenseNavControl.navigate("group")
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            membersList.forEach { member ->
                Text(text = member.toString())
            }
        }
    }
}

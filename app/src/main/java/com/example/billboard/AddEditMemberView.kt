package com.example.billboard

import android.util.Log
import androidx.compose.foundation.clickable
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope

@Composable
fun AddEditMemberView(groupsVM: GroupsViewModel, expenseNavControl: NavController, scState: ScaffoldState, scope: CoroutineScope, group: GroupClass, userVM: UserViewModel, expensesVM : ExpensesViewModel ) {
    Scaffold(
        topBar = { TopBar(showMenu = true, scState, false, scope ) },
        content = { AddEditMemberContent( groupsVM, expenseNavControl, group, userVM, expensesVM ) }
    )

}

@Composable
fun AddEditMemberContent( groupsVM: GroupsViewModel, expenseNavControl: NavController, group: GroupClass, userVM: UserViewModel, expensesVM : ExpensesViewModel) {

    var memberEmail by remember { mutableStateOf("") }
    var membersList by remember { mutableStateOf(group.members) }
    var adminsList by remember { mutableStateOf(group.admins) }
    var adminCheck by remember { mutableStateOf(false) }
    var editGroup by remember { mutableStateOf(group) }
    var newBalance by remember { mutableStateOf(group.balance) }

    var bool_edit by remember { mutableStateOf(false)}
    var alert_existing_m = remember { mutableStateOf(false)}

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
        val newGroup =
            GroupClass(adminsList, group.expenses, membersList, group.name, newBalance, group.id)
        Log.d("NewGroup: ", newGroup.toString())
        editGroup = newGroup
        newBalance = newGroup.balance
        groupsVM.editGroup(newGroup)
    }

    fun edit_member_name(groupsVM: GroupsViewModel, group: GroupClass, member: String, newemail : String){
        // Edit in group collection//

        // Apply changes in group balance //
        membersList.forEach { m ->
            if(m == member){
                //Adding new key in map//
                val oldMemberBalance = editGroup.balance[m]!!
                newBalance[newemail] = oldMemberBalance
                //Deleting old key in map//
                newBalance.remove(m)
            } else {
                //In each member balance, adding new key with old value
                var oldBalance = editGroup.balance[m]!!.getValue(member)
                newBalance[m]?.set(newemail, oldBalance)

                //Deleting old key balance//
                newBalance[m]?.remove(member)
            }
        }

        // Apply changes in members list //

        val tempMembers = mutableListOf<String>()
        membersList.forEach { m -> if(m != member) tempMembers.add(m) }
        tempMembers.add(newemail)
        membersList = tempMembers


        // Apply changes in admins list //

        if(adminsList.contains(member)){
            val tempAdmins = mutableListOf<String>()
            adminsList.forEach { admin -> if(admin != member) tempAdmins.add(admin) }
            tempAdmins.add(newemail)
            adminsList = tempAdmins
        }

        val newGroup = GroupClass(
            adminsList,
            group.expenses,
            membersList,
            group.name,
            newBalance,
            group.id
        )



        // Edit in expense collection //
        // In each expense, edit member email in rest list or payer and/or paidvalues map //

        group.expenses.forEach { exp ->
            val fexp = Firebase.firestore.collection("expenses").document(exp)

            fexp.get()
                .addOnSuccessListener { expenseSnapshot ->
                    val oldExpense = ExpenseClass(
                        expenseSnapshot.get("name").toString(),
                        expenseSnapshot.get("amount") as Double,
                        expenseSnapshot.get("payer").toString(),
                        expenseSnapshot.get("date").toString(),
                        expenseSnapshot.get("groupid").toString(),
                        expenseSnapshot.get("rest") as MutableList<String>,
                        expenseSnapshot.get("expid").toString(),
                        expenseSnapshot.get("paidvalues") as MutableMap<String, Boolean>
                    )

                    //Edit payer//
                    if(member == oldExpense.payer){
                        fexp.update("payer",newemail)
                    }

                    //Edit rest//
                    if(oldExpense.rest.contains(member)) {
                        fexp.update("rest", FieldValue.arrayRemove(member))
                        fexp.update("rest", FieldValue.arrayUnion(newemail))
                    }

                    //Edit paid values//
                    if(oldExpense.paidvalues.containsKey(member)){
                        var newPaidValuesMap = mutableMapOf<String,Boolean>()
                        oldExpense.paidvalues.forEach { other ->
                            newPaidValuesMap[other.key] = other.value
                        }
                        newPaidValuesMap[newemail] = newPaidValuesMap[member]!!
                        newPaidValuesMap.remove(member)

                        fexp.update("paidvalues", newPaidValuesMap)
                        bool_edit = false
                    }
                }
        }

        editGroup = newGroup
        groupsVM.editGroup(newGroup)
        memberEmail = ""
        /* TODO refresh expenses */
    }

    fun makeAdmin(groupsVM: GroupsViewModel, group: GroupClass, member: String) {
        val tempAdmins = mutableListOf<String>()
        adminsList.forEach { admin -> tempAdmins.add(admin) }
        tempAdmins.add(member)
        adminsList = tempAdmins
        Log.d("Admins ====> ", adminsList.toString())
        val newGroup = GroupClass(
            adminsList,
            group.expenses,
            group.members,
            group.name,
            group.balance,
            group.id
        )
        Log.d("Add admin : ", newGroup.toString())
        editGroup = newGroup
        groupsVM.editGroup(newGroup)
    }

    fun deleteAdmin(groupsVM: GroupsViewModel, group: GroupClass, member: String) {

        val tempAdmins = mutableListOf<String>()
        adminsList.forEach { admin -> tempAdmins.add(admin) }
        tempAdmins.remove(member)
        adminsList = tempAdmins

        val newGroup = GroupClass(
            adminsList,
            group.expenses,
            group.members,
            group.name,
            group.balance,
            group.id
        )

        editGroup = newGroup
        groupsVM.editGroup(newGroup)
    }

    fun isMemberBalanceClear(
        groupsVM: GroupsViewModel,
        group: GroupClass,
        member: String
    ): Boolean {
        group.balance[member]?.forEach { other ->
            if (other.value != 0.0) return false
        }
        return true
    }


    fun deleteMember(groupsVM: GroupsViewModel, group: GroupClass, member: String) {

        /* Delete members line from group balance */
        membersList.forEach { m ->
            if(m == member){
                newBalance.remove(m)
            } else {
                //In each member balance, remove member balance
                newBalance[m]?.remove(member)
            }
        }

        /* Delete from admins */
        if(group.admins.contains(member)){
            val tempAdmins = mutableListOf<String>()
            adminsList.forEach { admin -> if(admin != member) tempAdmins.add(admin) }
            adminsList = tempAdmins
        }

        /* Delete from members */
        val tempMembers = mutableListOf<String>()
        membersList.forEach { m -> if(m != member) tempMembers.add(m) }
        membersList = tempMembers


        val newGroup = GroupClass(
            adminsList,
            group.expenses,
            membersList,
            group.name,
            newBalance,
            group.id
        )

        editGroup = newGroup
        groupsVM.editGroup(newGroup)

    }

    if(!bool_edit) {
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
                        focusedBorderColor = MaterialTheme.colors.onPrimary,
                        cursorColor = MaterialTheme.colors.onPrimary,
                        textColor = MaterialTheme.colors.onPrimary,
                        focusedLabelColor = MaterialTheme.colors.onPrimary
                    ),
                    modifier = Modifier.height(64.dp),
                    shape = MaterialTheme.shapes.large,
                    textStyle = TextStyle(color = MaterialTheme.colors.onPrimary)
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
                        colors = CheckboxDefaults.colors(MaterialTheme.colors.onPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = { if(group.members.contains(memberEmail)) {
                        alert_existing_m.value = true
                    } else {
                        addMember() }},
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
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

                        if (!userVM.userEmail.value.equals(member)) {
                                OutlinedButton(
                                    onClick = {
                                        memberEmail = member
                                        bool_edit = true },
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(35.dp),
                                    shape = MaterialTheme.shapes.large,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary ),
                                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                                ) {
                                    Text(text = stringResource(R.string.edit))
                                }
                        }

                        if (!group.admins.contains(member)) {
                            OutlinedButton(
                                onClick = {
                                    makeAdmin(groupsVM, group, member)
                                },
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(35.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary ),
                                elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                            ) {
                                Text(text = stringResource(R.string.make_admin))

                            }
                        }

                        if (group.admins.contains(member) && !userVM.userEmail.value.equals(member)) {
                            OutlinedButton(
                                onClick = {
                                    deleteAdmin(groupsVM, group, member)
                                },
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(35.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary ),
                                elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                            ) {
                                Text(text = stringResource(R.string.rem_admin))

                            }
                        }

                        if (isMemberBalanceClear(
                                groupsVM,
                                group,
                                member
                            ) && !userVM.userEmail.value.equals(
                                member
                            )
                        ) {
                            OutlinedButton(
                                onClick = { deleteMember(groupsVM, group, member) },
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(35.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                            ) {
                                Text(text = stringResource(R.string.delete))
                            }
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary ),
                elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
            ) {
                Text(text = stringResource(R.string.cancel))
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
        }
        }
        if(alert_existing_m.value){
            AlertDialog(
                onDismissRequest = {
                    alert_existing_m.value = false
                },
                title = {
                    Text(text = stringResource(R.string.error))
                },
                text = {
                    Text(text = stringResource(R.string.err_add_member))
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            alert_existing_m.value = false
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
    } else {
        val oldMember by remember { mutableStateOf(memberEmail) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            OutlinedTextField(
                value = memberEmail,
                onValueChange = { memberEmail = it },
                label = { Text(text = stringResource(R.string.member_email)) },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Billboard_green,
                    cursorColor = MaterialTheme.colors.onPrimary,
//                        cursorColor = Color.White,
                    textColor = MaterialTheme.colors.onPrimary,
//                        textColor = Color.White,
                    focusedLabelColor = MaterialTheme.colors.onPrimary
//                        focusedLabelColor = Color.White
                ),
                modifier = Modifier
                    .height(64.dp)
                    .clickable { Log.d("MESSAGE", "CLICKED") },
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    edit_member_name(
                        groupsVM,
                        group,
                        oldMember,
                        memberEmail)
                    bool_edit = false
                },
                modifier = Modifier
                    .fillMaxWidth(.75f)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary ),
                elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
//                    colors = ButtonDefaults.outlinedButtonColors( contentColor = Billboard_green )
            ) {
                Text( text = stringResource(R.string.save))
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    bool_edit = false
                },
                modifier = Modifier
                    .fillMaxWidth(.75f)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
//                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    }
}
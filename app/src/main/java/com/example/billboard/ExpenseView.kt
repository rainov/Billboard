package com.example.billboard

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope

@Composable
fun ExpenseView(
    expense: ExpenseClass,
    expenseNavControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    expensesViewModel: ExpensesViewModel,
    groupsViewModel: GroupsViewModel,
    groupInfo : GroupClass,
    navControl : NavController
) {
    val groupAdmins = remember { mutableStateOf(groupInfo.admins) }
    val isUserAdmin = remember { mutableStateOf(false) }
    getUserStatus(isUserAdmin, groupAdmins)

    Scaffold(
        topBar = { TopBar(showMenu = false, scState, false, scope ) },
        bottomBar = { BottomBarExpenseView(
            navControl,
            expenseNavControl,
            expense,
            isUserAdmin.value,
            expensesViewModel,
            groupsViewModel,
            groupInfo
        )},
        content = {
            ExpenseViewContent(
                expense,
                expenseNavControl,
                expensesViewModel,
                groupsViewModel,
                groupInfo,
                navControl,
                isUserAdmin
            )
        }
    )
}

@Composable
fun ExpenseViewContent(
    expense: ExpenseClass,
    expenseNavControl: NavController,
    expensesViewModel: ExpensesViewModel,
    groupsViewModel: GroupsViewModel,
    groupInfo: GroupClass,
    navControl : NavController,
    isUserAdmin: MutableState<Boolean>
) {

    val expenseName = expense.name
    val expenseAmount = expense.amount.toString()
    val expensePayer = expense.payer

    val expenseRest = expense.rest

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = expenseName, textAlign = TextAlign.Center, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(20.dp))

            //TODO need to discuss about default currency, can the user choose one or the group
            Text(
                text = stringResource(R.string.amount_paid)
                        + expenseAmount + "€"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = stringResource(R.string.payer_member) + ": " + expensePayer)
            if(!groupInfo.members.contains(expensePayer)){
                Text( text = "Deleted", fontSize = 12.sp, color = Billboard_green)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = stringResource(R.string.rest))

            Spacer(modifier = Modifier.height(20.dp))

            expenseRest.forEach { member ->
                Row() {
                    Text(text = member, modifier = Modifier.padding(15.dp))
                    if (!groupInfo.members.contains(member)) {
                        Text(text = "Deleted", fontSize = 12.sp, color = Billboard_green)
                    } else {

                        if (isUserAdmin.value) {
                            if (expense.paidvalues[member] == false) {
                                OutlinedButton(
                                    onClick = {
                                        expensesViewModel.eraseDebt(
                                            groupInfo,
                                            member,
                                            expense,
                                            expenseNavControl,
                                            groupsViewModel,
                                            navControl
                                        )
                                    },
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(40.dp),
                                    shape = MaterialTheme.shapes.large,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                                ) {
                                    Text(text = stringResource(R.string.erase_debt))
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        expensesViewModel.cancelEraseDebt(
                                            groupInfo,
                                            member,
                                            expense,
                                            expenseNavControl,
                                            groupsViewModel,
                                            navControl
                                        )
                                    },
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(40.dp),
                                    shape = MaterialTheme.shapes.large,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                                ) {
                                    Text(text = stringResource(R.string.cancel_erase_debt))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getUserStatus(userstatus: MutableState<Boolean>, adminlist: MutableState<List<String>>){
    if(adminlist.value.contains(Firebase.auth.currentUser?.email.toString()))
        userstatus.value = true
}

fun statusEraseDebts(expense : ExpenseClass) : Boolean{
    var test = false
    expense.paidvalues.forEach { key ->
        if(key.value) test = true
    }
    return test
}

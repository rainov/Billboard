package com.example.billboard

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import getGroupAdmins
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

    Scaffold(
        topBar = { TopBar(showMenu = true, scState, false, scope ) },
        content = { ExpenseViewContent(expense, expenseNavControl, expensesViewModel, groupsViewModel, groupInfo, navControl) }
    )

}

@Composable
fun ExpenseViewContent(expense: ExpenseClass, expenseNavControl: NavController, expensesViewModel: ExpensesViewModel, groupsViewModel: GroupsViewModel, groupInfo: GroupClass, navControl : NavController) {

    val expenseName = expense.name
    val expenseAmount = expense.amount.toString()
    val expensePayer = expense.payer

    val expenseRest = expense.rest

    val openDialogDeleteWithDebts = remember { mutableStateOf(false) }
    val openDialogDelete = remember { mutableStateOf(false) }
    var openDialogClear = remember { mutableStateOf(false) }

    val groupAdmins = remember { mutableStateOf(groupInfo.admins) }
//    val groupAdmins = remember { mutableStateOf(listOf<String>()) }
//    getGroupAdmins(expense.groupid, groupAdmins)

    val isUserAdmin = remember { mutableStateOf(false) }
    getUserStatus(isUserAdmin, groupAdmins)

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

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = stringResource(R.string.rest))

            Spacer(modifier = Modifier.height(20.dp))

            expenseRest.forEach { member ->
                Row() {
                    Text(text = member, modifier = Modifier.padding(15.dp))

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
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
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
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(text = stringResource(R.string.cancel_erase_debt))
                                //Spacer(modifier = Modifier.height(5.dp))
                            }

                        }
                    }
                }
            }
        }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
                    .padding(5.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back icon",
                    modifier = Modifier.clickable { expenseNavControl.navigate("group") })

                if (isUserAdmin.value) {
                    OutlinedButton(
                        onClick = {
                            if (statusEraseDebts(expense)) {
                                openDialogDeleteWithDebts.value = true
                            } else {
                                openDialogDelete.value = true
                            }
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                    ) {
                        Text(text = stringResource(R.string.delete))
                    }
                }

                if (isUserAdmin.value) {
                    OutlinedButton(
                        onClick = {
                            openDialogClear.value = true
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                    ) {
                        Text(text = stringResource(R.string.clear_exp))
                    }
                }


                if (isUserAdmin.value) {
                    OutlinedButton(
                        onClick = {
                            expenseNavControl.navigate("${expense.expid}_edit")
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                    ) {
                        Text(text = stringResource(R.string.edit))
                    }
                }

                if (openDialogDelete.value) {

                    AlertDialog(
                        onDismissRequest = {
                            openDialogDelete.value = false
                        },
                        title = {
                            Text(text = stringResource(R.string.delete_conf))
                        },
                        text = {
                            Text(text = stringResource(R.string.delete_conf_mess))
                        },
                        confirmButton = {
                            OutlinedButton(
                                onClick = {
                                    openDialogDelete.value = false
                                    expensesViewModel.deleteExpenseLine(
                                        expense,
                                        expenseNavControl,
                                        groupsViewModel,
                                        groupInfo,
                                        navControl
                                    )
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    openDialogDelete.value = false
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                if (openDialogDeleteWithDebts.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialogDeleteWithDebts.value = false
                        },
                        title = {
                            Text(text = stringResource(R.string.delete_conf))
                        },
                        text = {
                            Text(text = stringResource(R.string.delete_conf_mess_debts))
                        },
                        confirmButton = {
                            OutlinedButton(
                                onClick = {
                                    openDialogDeleteWithDebts.value = false
                                    expensesViewModel.deleteExpenseLineCancelEraseDebts(
                                        expense,
                                        groupsViewModel,
                                        groupInfo,
                                        navControl
                                    )
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(stringResource(R.string.delete))
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    openDialogDeleteWithDebts.value = false
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                if (openDialogClear.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialogClear.value = false
                        },
                        title = {
                            Text(text = stringResource(R.string.clear_exp))
                        },
                        text = {
                            Text(text = stringResource(R.string.clear_conf))
                        },
                        confirmButton = {
                            OutlinedButton(
                                onClick = {
                                    openDialogClear.value = false
                                    expensesViewModel.eraseAllDebts(
                                        groupInfo,
                                        expense,
                                        expenseNavControl,
                                        groupsViewModel,
                                        navControl
                                    )
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(stringResource(R.string.confirm))
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    openDialogClear.value = false
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                            ) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
        }
    }

fun getUserStatus(userstatus: MutableState<Boolean>, adminlist: MutableState<List<String>>){
    if(adminlist.value.contains(Firebase.auth.currentUser?.email.toString()))
        userstatus.value = true
}

fun statusEraseDebts(expense : ExpenseClass) : Boolean{
    var test : Boolean = false
    expense.paidvalues.forEach { key ->
        if(key.value) test = true
    }
    return test
}

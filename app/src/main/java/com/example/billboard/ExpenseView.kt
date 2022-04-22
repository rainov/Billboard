package com.example.billboard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt

@Composable
fun ExpenseView(
    expense: ExpenseClass,
    expenseNavControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    expensesViewModel: ExpensesViewModel,
    groupsViewModel: GroupsViewModel,
    groupInfo : GroupClass,
    navControl : NavController,
    userVM: UserViewModel
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
            groupInfo,
            userVM
        )},
        content = {
            ExpenseViewContent(
                expense,
                expenseNavControl,
                expensesViewModel,
                groupsViewModel,
                groupInfo,
                navControl,
                isUserAdmin,
                userVM
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
    isUserAdmin: MutableState<Boolean>,
    userVM: UserViewModel
) {

    val expenseName = expense.name
    val expenseAmount = expense.amount.toString()
    val expensePayer = expense.payer
    val expenseRest = expense.rest

    val amountForEach = ((expense.amount / ( expense.rest.size + 1 )) * 100.0 ).roundToInt() / 100.0

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = expenseName, textAlign = TextAlign.Center, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(.85f),
                elevation = 7.dp,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.amount_paid),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = expenseAmount + "€", fontSize = 21.sp)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(.9f),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(text = stringResource(R.string.payer_member), fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(.83f),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = expensePayer, fontSize = 20.sp)
                    if(!groupInfo.members.contains(expensePayer)){
                        Text( text = "Deleted", fontSize = 12.sp, color = Billboard_green)
                    }
                }
            }

            //TODO need to discuss about default currency, can the user choose one or the group

            Spacer(modifier = Modifier.height(10.dp))

            if ( expense.receiptURL == "" ) {
                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate("${expense.expid}_addReceipt")
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                )
                {
                    Text(text = stringResource(R.string.add_receipt))
                }
            } else {
                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate("${expense.expid}_showReceipt")
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                )
                {
                    Text(text = stringResource(R.string.show_receipt))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = stringResource(R.string.rest), fontSize = 18.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.83f),
                color = MaterialTheme.colors.onPrimary
            )

            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .fillMaxHeight(.79f)
                    .verticalScroll(enabled = true, state = ScrollState(1)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                expenseRest.forEach { member ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 7.dp,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = member, modifier = Modifier.padding(10.dp), fontWeight = if(expense.paidvalues[member] == false) FontWeight.Bold else FontWeight.Light )
                            if( expense.paidvalues[member] == false ) {
                                Text( text = stringResource(R.string.owes) + " " + amountForEach + stringResource(R.string.euro_sign) )
                            } else {
                                Text( text = stringResource(R.string.has_paid) + " " + amountForEach + stringResource(R.string.euro_sign) )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            if (!groupInfo.members.contains(member)) {
                                Text(text = "Deleted", fontSize = 12.sp, color = Billboard_green)
                            } else if(groupInfo.members.contains(expensePayer)){
                                if (isUserAdmin.value || userVM.userEmail.value == expensePayer) {
                                    if (expense.paidvalues[member] == false) {
                                        OutlinedButton(
                                            onClick = {
                                                expensesViewModel.eraseDebt(
                                                    groupInfo,
                                                    member,
                                                    expense,
                                                    expenseNavControl,
                                                    groupsViewModel,
                                                    navControl,
                                                    userVM
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(.8f)
                                                .height(37.dp),
                                            shape = MaterialTheme.shapes.large,
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                                            elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
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
                                                    navControl,
                                                    userVM
                                                )
                                            },
                                            modifier = Modifier
                                                .width(150.dp)
                                                .height(37.dp),
                                            shape = MaterialTheme.shapes.large,
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                                            elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                                        ) {
                                            Text(text = stringResource(R.string.cancel_erase_debt))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.85f),
                color = MaterialTheme.colors.onPrimary
            )
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

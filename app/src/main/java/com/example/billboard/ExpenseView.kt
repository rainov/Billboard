package com.example.billboard

/*===================================================/
|| This view is showing the information for a group
|| expense. From here, the expense can be deleted,
|| edited or cleared.
/====================================================*/

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
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

///////////////////////////////////
// Main scaffold view container //
/////////////////////////////////
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

//////////////////////////////////
// Content of the Expense view //
////////////////////////////////
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
    expensesViewModel.getExpenses(groupInfo.id)

    val expenseName = expense.name
    val expenseAmount = expense.amount.toString()
    val expensePayer = expense.payer
    val expenseRest = expense.rest
    val receiptUrl = expense.receiptURL

    val amountForEach = ((expense.amount / ( expense.rest.size + 1 )) * 100.0 ).roundToInt() / 100.0

    ///////////////////////
    // Container column //
    /////////////////////
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        //////////////////////////////////////////////////////////////////
        // Top column with card displaying information for the expense //
        ////////////////////////////////////////////////////////////////
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ///////////////////////////////
            // Headline - Expense name  //
            /////////////////////////////
            Text(text = expenseName, textAlign = TextAlign.Center, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(10.dp))

            ////////////////
            // Info card //
            //////////////
            Card(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                        shape = MaterialTheme.shapes.large,
                    ),
                elevation = 7.dp,
                shape = MaterialTheme.shapes.large
            ) {
                /////////////////////////
                // Inside card column //
                ///////////////////////
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //////////////////
                        // Amount paid //
                        ////////////////
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

                    //////////////
                    // Paid by //
                    ////////////
                    Text(text = stringResource(R.string.payer_member), fontSize = 17.sp)

                    Spacer(modifier = Modifier.height(5.dp))

                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(.83f),
                        color = MaterialTheme.colors.onPrimary
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    ///////////////////////////////////////////////////////////////////////////////////////////////
                    // The user who paid the bill... If the user is registered, his/her username is displayed.  //
                    // If the payer is not registered, the part before @ in his email is shown.                //
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    var uname = remember { mutableStateOf("default")}
                    getUsername(expensePayer, uname)
                    if(uname.value == "null") uname.value = expensePayer
                    Text(text = uname.value, fontSize = 20.sp)
                    if(!groupInfo.members.contains(expensePayer)){
                        Text( text = "Deleted", fontSize = 12.sp, color = MaterialTheme.colors.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            /////////////////////////////////////////////////
            // Button to upload/show receipt for the bill //
            ///////////////////////////////////////////////
            if ( receiptUrl.isEmpty() ) {
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

            ////////////////////////
            // Participants text //
            //////////////////////
            Text(text = stringResource(R.string.rest), fontSize = 18.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.83f),
                color = Billboard_green
            )

            Spacer(modifier = Modifier.height(15.dp))

            //////////////////////////////////////////////////////////////////
            // Vertical scroll column, containing all expense participants //
            ////////////////////////////////////////////////////////////////
            Column(
                modifier = Modifier
                    .fillMaxWidth(.83f)
                    .fillMaxHeight(.77f)
                    .verticalScroll(enabled = true, state = ScrollState(1)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                expenseRest.forEach { member ->
                    //////////////////////////////////////////
                    // Card with info for each participant //
                    ////////////////////////////////////////
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
                            /////////////////////////////////////////////////////////////////////
                            // Username/email of the user based on if he/she is registered or not //
                            ///////////////////////////////////////////////////////////////////
                            var uname = remember { mutableStateOf("default")}
                            getUsername(member, uname)
                            if(uname.value == "null") uname.value = member
                            Text(text = uname.value, modifier = Modifier.padding(10.dp), fontWeight = if(expense.paidvalues[member] == false) FontWeight.Bold else FontWeight.Light )

                            /////////////////////////////////////////////
                            // Text describing the status of the debt //
                            ///////////////////////////////////////////
                            if( expense.paidvalues[member] == false ) {
                                Text( text = stringResource(R.string.owes) + " " + amountForEach + stringResource(R.string.euro_sign) )
                            } else {
                                Text( text = stringResource(R.string.has_paid) + " " + amountForEach + stringResource(R.string.euro_sign) )
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            ////////////////////////////////////////////////////////////////////////
                            // Erase debt and refund debt buttons, visible only for group admins //
                            //////////////////////////////////////////////////////////////////////
                            if (!groupInfo.members.contains(member)) {
                                Text(text = "Deleted", color = MaterialTheme.colors.onPrimary)
                            } else if(groupInfo.members.contains(expensePayer)){
                                if (isUserAdmin.value || userVM.userEmail.value == expensePayer) {
                                    if (expense.paidvalues[member] == false) {
                                        ////////////////////////
                                        // Erase debt button //
                                        //////////////////////
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
                                        /////////////////////////
                                        // Refund debt button //
                                        ///////////////////////
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
                                                .fillMaxWidth(.8f)
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

package com.example.billboard

/*===================================================/
|| Here are stored all composes that are used as
|| bottom bars in different app views
/====================================================*/

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green

/////////////////////////////
// MAIN SCREEN BOTTOM BAR //
///////////////////////////
@Composable
fun BottomBarMainScreen(
    navControl: NavController
) {
    ////////////////
    // Container //
    //////////////
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(.83f),
            color = Billboard_green
        )

        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {

            ///////////////////////////////////////
            // Circle Add button for new groups //
            /////////////////////////////////////
            FloatingActionButton(
                onClick = { navControl.navigate( "CreateGroup" ) },
                backgroundColor = Billboard_green,
                modifier = Modifier
                    .padding(30.dp, 20.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_add),
                    contentDescription = "add group",
                )
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////
// GROUP VIEW BOTTOM BAR //
//////////////////////////
@Composable
fun BottomBarGroupScreen(
    navControl: NavController,
    expenseNavControl: NavController,
    groupInfo: GroupClass,
    groupsVM: GroupsViewModel,
    userVM: UserViewModel
) {

    val openDialog = remember { mutableStateOf(false) }
    val openErrors = remember { mutableStateOf(false) }

    ////////////////
    // Container //
    //////////////
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(.83f),
            color = Billboard_green
        )

        ////////////////////////////////////
        // Container row for the buttons //
        //////////////////////////////////
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 0.dp, start = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            ////////////////////////
            // Back arrow button //
            //////////////////////
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back icon",
                modifier = Modifier
                    .clickable { navControl.navigate("MainScreen") }
                    .padding(35.dp, 30.dp)
            )

            //////////////////////////////////////////////////
            // Delete button visible only for group admins //
            ////////////////////////////////////////////////
            if(groupInfo.admins.contains(userVM.userEmail.value)) {
                OutlinedButton(
                    onClick = {
                        if (groupBalanceClear(groupInfo)) {
                            openDialog.value = true
                        } else {
                            openErrors.value = true
                        }
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////
            // Add expense button, visible only if there is at least 1 member except the group admin //
            //////////////////////////////////////////////////////////////////////////////////////////
            if (groupInfo.members.size > 1 ) {
                FloatingActionButton(onClick = { expenseNavControl.navigate("addExpense")},
                    backgroundColor = Billboard_green,
                    modifier = Modifier.padding(20.dp, 20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add),
                        contentDescription = "add expense",
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(95.dp))
            }
        }
    }


    /////////////////////////////////////////////////////////
    // Delete confirmation pop up dialog for empty groups //
    ///////////////////////////////////////////////////////
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.delete_conf))
            },
            text = {
                Text(text = stringResource(R.string.delete_grp))
            },
            confirmButton = {
                ////////////////////
                // Delete button //
                //////////////////
                OutlinedButton(
                    onClick = {
                        openDialog.value = false
                        groupsVM.deleteGroup(
                            groupInfo,
                            userVM
                        )
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                ////////////////////
                // Cancel button //
                //////////////////
                OutlinedButton(
                    onClick = {
                        openDialog.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    /////////////////////////////////////////////////////////
    // Delete warning for groups with dirty balance sheet //
    ///////////////////////////////////////////////////////
    if (openErrors.value) {
        AlertDialog(
            onDismissRequest = {
                openErrors.value = false
            },
            title = {
                Text(text = stringResource(R.string.delete_conf))
            },
            text = {
                Text(text = stringResource(R.string.delete_grp_err))
            },
            confirmButton = {
                ////////////////////
                // Delete button //
                //////////////////
                OutlinedButton(
                    onClick = {
                        openErrors.value = false
                        groupsVM.deleteGroup(
                            groupInfo,
                            userVM
                        )
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                ////////////////////
                // Cancel button //
                //////////////////
                OutlinedButton(
                    onClick = {
                        openErrors.value = false
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }

        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////
// BALANCE VIEW BOTTOM BAR //
////////////////////////////
@Composable
fun BottomBarBack(
    expenseNavControl: NavController
) {
    ///////////////////////
    // Container column //
    /////////////////////
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(.83f),
            color = Billboard_green
        )
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ////////////////////////
            // Back arrow button //
            //////////////////////
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back icon",
                modifier = Modifier
                    .clickable { expenseNavControl.navigate("group") }
                    .padding(35.dp, 20.dp)
            )
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////
// EXPENSE VIEW BOTTOM BAR //
////////////////////////////
@Composable
fun BottomBarExpenseView(
    navControl: NavController,
    expenseNavControl: NavController,
    expense: ExpenseClass,
    isUserAdmin: Boolean,
    expensesViewModel: ExpensesViewModel,
    groupsViewModel: GroupsViewModel,
    groupInfo: GroupClass,
    userVM: UserViewModel
) {
    ///////////////////////
    // Container column //
    /////////////////////
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(.83f),
            color = Billboard_green
        )

        ///////////////////////////////////////////
        // Row container for the action buttons //
        /////////////////////////////////////////
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            val openDialogDeleteWithDebts = remember { mutableStateOf(false) }
            val openDialogDelete = remember { mutableStateOf(false) }
            val openDialogClear = remember { mutableStateOf(false) }

            ////////////////////////
            // Back arrow button //
            //////////////////////
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back icon",
                modifier = Modifier.clickable { expenseNavControl.navigate("group") })

            //////////////////////////////////////////////////
            // Delete button visible only for group admins //
            ////////////////////////////////////////////////
            if (isUserAdmin) {
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            }

            //////////////////////////////////////////////////
            // Clear button visible only for group admins //
            ////////////////////////////////////////////////
            if (isUserAdmin) {
                OutlinedButton(
                    onClick = {
                        openDialogClear.value = true
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text(text = stringResource(R.string.clear_exp))
                }
            }

            //////////////////////////////////////////////////
            // Edit button visible only for group admins //
            ////////////////////////////////////////////////
            if (isUserAdmin) {
                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate("${expense.expid}_edit")
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text(text = stringResource(R.string.edit))
                }
            }

            ////////////////////////////////////////////////////////////
            // Delete confirmation pop up for clear balance expenses //
            //////////////////////////////////////////////////////////
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
                        ////////////////////
                        // Delete button //
                        //////////////////
                        OutlinedButton(
                            onClick = {
                                openDialogDelete.value = false
                                expensesViewModel.deleteExpenseLine(
                                    expense,
                                    groupsViewModel,
                                    groupInfo,
                                    navControl,
                                    userVM
                                )
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(40.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        ////////////////////
                        // Cancel button //
                        //////////////////
                        OutlinedButton(
                            onClick = {
                                openDialogDelete.value = false
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

            ////////////////////////////////////////////////////////////
            // Delete confirmation pop up for dirty balance expenses //
            //////////////////////////////////////////////////////////
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
                        ////////////////////
                        // Delete button //
                        //////////////////
                        OutlinedButton(
                            onClick = {
                                openDialogDeleteWithDebts.value = false
                                expensesViewModel.deleteExpenseLineCancelEraseDebts(
                                    expense,
                                    groupsViewModel,
                                    groupInfo,
                                    navControl,
                                    userVM
                                )
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(40.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        ////////////////////
                        // Cancel button //
                        //////////////////
                        OutlinedButton(
                            onClick = {
                                openDialogDeleteWithDebts.value = false
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

            ////////////////////////////////////////
            // Clear expense confirmation pop up //
            //////////////////////////////////////
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
                        ////////////////////
                        // Confirm button //
                        //////////////////
                        OutlinedButton(
                            onClick = {
                                openDialogClear.value = false
                                expensesViewModel.eraseAllDebts(
                                    groupInfo,
                                    expense,
                                    groupsViewModel,
                                    navControl,
                                    userVM
                                )
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(40.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        ////////////////////
                        // Cancel button //
                        //////////////////
                        OutlinedButton(
                            onClick = {
                                openDialogClear.value = false
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
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////
// ABOUT US BOTTOM BAR //
////////////////////////
@Composable
fun BottomBarAboutUs(
    navControl: NavController
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        ////////////////////////
        // Back arrow button //
        //////////////////////
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { navControl.navigate("MainScreen") }
                .padding(35.dp, 30.dp)
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////
// AFFILIATE PARTNERS CATEGORIES VIEW BOTTOM BAR //
//////////////////////////////////////////////////
@Composable
fun BottomBarAffiliate(
    navControl: NavController,
    categoryName: MutableState<String>
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        ////////////////////////
        // Back arrow button //
        //////////////////////
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable {
                    if( categoryName.value == "" ){
                        navControl.navigate("MainScreen")
                    } else {
                        categoryName.value = ""
                    }
                }
                .padding(35.dp, 30.dp)
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// AFFILIATE PARTNER VIEW BOTTOM BAR //
//////////////////////////////////////
@Composable
fun BottomBarPartner(
    affiliateNavControl: NavController
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        ////////////////////////
        // Back arrow button //
        //////////////////////
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { affiliateNavControl.navigate("affiliate_categories")}
                .padding(35.dp, 30.dp)
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////
// ADD RECEIPT VIEW BOTTOM BAR //
////////////////////////////////
@Composable
fun BottomBarAddReceipt(
    expenseNavControl: NavController,
    expId: String
){
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        ////////////////////////
        // Back arrow button //
        //////////////////////
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { expenseNavControl.navigate(expId)}
                .padding(35.dp, 30.dp)
        )
    }
}
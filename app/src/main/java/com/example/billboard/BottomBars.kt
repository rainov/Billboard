package com.example.billboard

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

//MAIN SCREEN BOTTOM
@Composable
fun BottomBarMainScreen(
    navControl: NavController
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//GROUP VIEW BOTTOM
@Composable
fun BottomBarGroupScreen(
    navControl: NavController,
    expenseNavControl: NavController,
    groupInfo: GroupClass,
    groupsVM: GroupsViewModel,
    userVM : UserViewModel
) {

    val openDialog = remember { mutableStateOf(false) }
    val openErrors = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 0.dp, start = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { navControl.navigate("MainScreen") }
                .padding(35.dp, 30.dp)
        )

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
                OutlinedButton(
                    onClick = {
                        openDialog.value = false
                        groupsVM.deleteGroup(
                            groupInfo
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
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//BALANCE VIEW + ADD EDIT EXPENSE VIEW
@Composable
fun BottomBarBack(
    expenseNavControl: NavController
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { expenseNavControl.navigate("group") }
                .padding(35.dp, 30.dp)
        )
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//EXPENSE VIEW BOTTOM
@Composable
fun BottomBarExpenseView(
    navControl: NavController,
    expenseNavControl: NavController,
    expense: ExpenseClass,
    isUserAdmin: Boolean,
    expensesViewModel: ExpensesViewModel,
    groupsViewModel: GroupsViewModel,
    groupInfo: GroupClass
) {
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

        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier.clickable { expenseNavControl.navigate("group") })

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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Text(text = stringResource(R.string.delete))
            }
        }

        if (isUserAdmin) {
            OutlinedButton(
                onClick = {
                    openDialogClear.value = true
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Text(text = stringResource(R.string.clear_exp))
            }
        }

        if (isUserAdmin) {
            OutlinedButton(
                onClick = {
                    expenseNavControl.navigate("${expense.expid}_edit")
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { navControl.navigate("MainScreen") }
                .padding(35.dp, 30.dp)
        )
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            modifier = Modifier
                .clickable { affiliateNavControl.navigate("affiliate_categories")}
                .padding(35.dp, 30.dp)
        )
    }
}
package com.example.billboard


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.example.billboard.ui.theme.BillBoard_Grey
import kotlinx.coroutines.CoroutineScope
import java.util.*

@Composable
fun GroupView(
    groupInfo: GroupClass,
    expenses: List<ExpenseClass>,
    expenseNavControl: NavController,
    navControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    groupsViewModel: GroupsViewModel,
    userVM : UserViewModel
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(showMenu = true, scState, false, scope) },
        bottomBar = { BottomBarGroupScreen(navControl, expenseNavControl, groupInfo, groupsViewModel, userVM )},
        drawerContent = {
            DrawerMainScreen (
                scState,
                scope,
                DrawerGroupContent(navControl, scState, scope, groupInfo, expenseNavControl, userVM, groupsViewModel )
            )
        },
        content = { GroupViewContent( groupInfo, expenses, expenseNavControl, navControl ) },
    )
}

@Composable
fun GroupViewContent( groupInfo: GroupClass, expenses: List<ExpenseClass>, expenseNavControl: NavController, navControl: NavController ) {

    var totalSpent = 0.0
    expenses.forEach { expense ->
        totalSpent += expense.amount
    }

    if (groupInfo.members.size == 1 && groupInfo.expenses.isEmpty()) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.weight((1f))
            ) {

                Text(
                    text = groupInfo.name,
                    modifier = Modifier.clickable { navControl.navigate("MainScreen") },
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

            }
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.lonely_message),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate("addMembers")
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(text = stringResource(R.string.add_members))
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = groupInfo.name,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

//                var adminlist = ""
//                groupInfo.admins.forEach { admin ->
//                    adminlist = if (adminlist.isEmpty()) admin.substringBefore("@")
//                    else adminlist + ", " + admin.substringBefore("@")
//                }
//
//                var memberlist = ""
//                groupInfo.members.forEach { member ->
//                    memberlist = if (memberlist.isEmpty()) member.substringBefore("@")
//                    else memberlist + ", " + member.substringBefore("@")
//                }

                OutlinedButton(
                    onClick = { expenseNavControl.navigate("groupBalance") },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.balance_details))
                        Icon(
                            painter = painterResource(R.drawable.forward),
                            contentDescription = "forward arrow",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Group expenses", fontSize = 20.sp, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(10.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(1.dp),
                    color = Billboard_green
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxSize(.79f)
                        .verticalScroll(enabled = true, state = ScrollState(1)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    expenses.forEach { expense ->
                        var color = BillBoard_Grey
                        expense.paidvalues.forEach { key ->
                            if (!key.value) color = MaterialTheme.colors.surface
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        OutlinedButton(
                            onClick = { expenseNavControl.navigate( expense.expid ) },
                            modifier = Modifier
                                .fillMaxWidth(.75f)
                                .height(50.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colors.onPrimary,
                                backgroundColor = color
                            ),
                            elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                        ) {
                                Text(text = expense.name )
                        }
                    }
                }
            }
        }
    }
}

fun groupBalanceClear(group: GroupClass): Boolean {
    group.balance.forEach { key ->
        key.value.forEach { member ->
            if (member.value != 0.0) {
                return false
            }
        }
    }
    return true
}
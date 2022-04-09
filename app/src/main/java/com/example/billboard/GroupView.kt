package com.example.billboard


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

@Composable
fun GroupView(
    groupInfo: GroupClass,
    expenses: List<ExpenseClass>,
    expenseNavControl: NavController,
    navControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    groupsViewModel: GroupsViewModel
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(showMenu = true, scState, false, scope) },
        bottomBar = { BottomBarGroupScreen(navControl, expenseNavControl, groupInfo, groupsViewModel )},
        drawerContent = {
            DrawerMainScreen (
                scState,
                scope,
                DrawerGroupContent(navControl, scState, scope, groupInfo, expenseNavControl )
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

    if (groupInfo.members.size == 1) {

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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
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
                    modifier = Modifier.clickable { navControl.navigate("MainScreen") },
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                var adminlist = ""
                groupInfo.admins.forEach { admin ->
                    adminlist = if (adminlist.isEmpty()) admin.substringBefore("@")
                    else adminlist + ", " + admin.substringBefore("@")
                }

                var memberlist = ""
                groupInfo.members.forEach { member ->
                    memberlist = if (memberlist.isEmpty()) member.substringBefore("@")
                    else memberlist + ", " + member.substringBefore("@")
                }

                OutlinedButton(
                    onClick = { expenseNavControl.navigate("groupBalance") },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Billboard_green)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(R.string.balance_details))
                        Icon(
                            painter = painterResource(R.drawable.forward),
                            contentDescription = "forward arrow",
                            tint = Billboard_green
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.admins) + " " + adminlist,
                    modifier = Modifier.clickable { navControl.navigate("MainScreen") })
                Text(
                    text = stringResource(R.string.members) + " " + memberlist,
                    modifier = Modifier.clickable { navControl.navigate("MainScreen") })


                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxSize(.8f)
                        .verticalScroll(enabled = true, state = ScrollState(1)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    expenses.forEach { expense ->
                        var color = BillBoard_Grey
                        expense.paidvalues.forEach { key ->
                            if (!key.value) color = Color.Transparent
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        Card(modifier = Modifier
                            .padding(5.dp)
                            .clickable { expenseNavControl.navigate(expense.expid) }
                            .fillMaxWidth(fraction = 0.75f),
                            elevation = 10.dp,
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(1.dp, Billboard_green),
                            backgroundColor = color
                        ) {
                            Text(
                                text = expense.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(15.dp)
                            )
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
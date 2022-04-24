package com.example.billboard

/*===================================================/
|| This view is showing information for a single group.
|| From here can be added expenses, members, navigate
|| to group balance and expense view
/====================================================*/

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.example.billboard.ui.theme.BillBoard_Grey
import kotlinx.coroutines.CoroutineScope

//////////////////////////////
// Main scaffold container //
////////////////////////////
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
            DrawerGroupView ( navControl, scState, scope, groupInfo, expenseNavControl, userVM, groupsViewModel )
        },
        content = { GroupViewContent( groupInfo, expenses, expenseNavControl, navControl ) },
    )
}

//////////////////////////////
// Content of the scaffold //
////////////////////////////
@Composable
fun GroupViewContent( groupInfo: GroupClass, expenses: List<ExpenseClass>, expenseNavControl: NavController, navControl: NavController ) {

    /////////////////////////////////////////////////////////////////////////////////////
    // If the group is just created and there are not any members except the creator, //
    // here is displayed an invitation to add members to the group                   //
    //////////////////////////////////////////////////////////////////////////////////
    if (groupInfo.members.size == 1 && groupInfo.expenses.isEmpty()) {

        ///////////////////////
        // Container column //
        /////////////////////
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier.weight((1f))
            ) {
                ///////////////////////////////
                // Group name as a headline //
                /////////////////////////////
                Text(
                    text = groupInfo.name,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            }

            ////////////////////////////////////////////////////////////////////
            // Inner container column for the message and Add members button //
            //////////////////////////////////////////////////////////////////
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                //////////////////////////////////////////////////////////
                // Text message saying that you are alone in the group //
                ////////////////////////////////////////////////////////
                Text(
                    text = stringResource(R.string.lonely_message),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                //////////////////////////////////////////////////////////////
                // Add members button, navigating to Add/Edit members view //
                ////////////////////////////////////////////////////////////
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
        ///////////////////////////////////////////////////////////////////////////
        // If there are already group members added, the following is displayed //
        /////////////////////////////////////////////////////////////////////////
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            /////////////////////////////
            // Inner container column //
            ///////////////////////////
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                ////////////////////////////
                // Headline - Group name //
                //////////////////////////
                Text(
                    text = groupInfo.name,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                //////////////////////////////////////////////////
                // Button navigating to the group balance view //
                ////////////////////////////////////////////////
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

                ////////////////////////////
                // Text - Group expenses //
                //////////////////////////
                Text(text = stringResource(R.string.group_expenses), fontSize = 20.sp, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(10.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(1.dp),
                    color = Billboard_green
                )

                Spacer(modifier = Modifier.height(10.dp))

                ///////////////////////////////////////////////////////////////////////////
                // Vertical scroll column containing all group expenses, sorted by date //
                /////////////////////////////////////////////////////////////////////////
                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxSize(.79f)
                        .verticalScroll(enabled = true, state = ScrollState(1)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    expenses.sortedByDescending { exp -> exp.date }.forEach { expense ->
                        var color = BillBoard_Grey
                        expense.paidvalues.forEach { key ->
                            if (!key.value) color = MaterialTheme.colors.surface
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        /////////////////////////////////////////////////////////////////////////////////
                        // Button for each expense, navigating to the corresponding expense view page //
                        ///////////////////////////////////////////////////////////////////////////////
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
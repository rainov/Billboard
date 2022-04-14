package com.example.billboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.example.billboard.ui.theme.Billboard_Red
import kotlinx.coroutines.CoroutineScope

@Composable
fun GroupBalanceView (
    scState: ScaffoldState,
    expenseNavControl: NavController,
    groupInfo: GroupClass,
    expenses: List<ExpenseClass>,
    scope: CoroutineScope,
    navControl: NavController,
    userVM: UserViewModel
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope ) },
        bottomBar = { BottomBarBack(expenseNavControl) },
        content = { GroupBalanceContent( groupInfo, expenses ) },
        drawerContent = {
            DrawerMainScreen (
                scState,
                scope,
                DrawerGroupContent(
                    navControl,
                    scState,
                    scope,
                    groupInfo,
                    expenseNavControl,
                    userVM
                )
            )
        }
    )
}

@Composable
fun GroupBalanceContent(
    groupInfo: GroupClass,
    expenses: List<ExpenseClass>
) {

    var totalSpent = 0.0
    expenses.forEach { expense ->
        totalSpent += expense.amount
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        //.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = groupInfo.name, textAlign = TextAlign.Center, fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row() {
                Text( text = stringResource( R.string.total_spent ), fontSize = 20.sp)
                Spacer(modifier = Modifier.width( 15.dp))
                Text( text = totalSpent.toString(), fontSize = 20.sp, color = Billboard_green )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(.88f)
                    .verticalScroll(enabled = true, state = ScrollState(1))
            ) {
                groupInfo.members.forEach { member ->
                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .border(
                                BorderStroke(1.dp, Billboard_green),
                                shape = MaterialTheme.shapes.large,
                            )
                            .padding(15.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text( text = member, textAlign = TextAlign.Center, fontSize = 19.sp )

                            Spacer(modifier = Modifier.height(5.dp))

                            Divider(
                                modifier = Modifier.height(1.dp),
                                color = Billboard_green
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            groupInfo.balance[member]?.forEach { other ->
                                var color: Color = MaterialTheme.colors.onPrimary
                                var amount: Double = 0.0


                                if ( other.value < 0 ) {
                                    color = Billboard_Red
                                    amount = other.value * -1
                                } else if (other.value > 0){
                                    color = Billboard_green
                                    amount = other.value
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text( text = other.key + ": " )
                                    Text( text = amount.toString(), color = color)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}
package com.example.billboard

import android.telephony.ClosedSubscriberGroupInfo
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerGroupView(scState: ScaffoldState, navControl: NavController, scope: CoroutineScope, groupInfo: GroupClass, expenseNavControl: NavController) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(false, scState, true, scope ) },
        content = { DrawerGroupContent( navControl, scState, scope, groupInfo, expenseNavControl ) },
    )
}

@Composable
fun DrawerGroupContent(navControl: NavController, scState: ScaffoldState, scope: CoroutineScope, groupInfo: GroupClass, expenseNavControl: NavController) {

    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text( text = groupInfo.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(15.dp))
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .width(280.dp),
                color = Bilboard_green
            )
        }

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                groupInfo.members.forEach { member ->
                    Text( text = member )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = { expenseNavControl.navigate("addMembers")},
                    modifier = Modifier
                        .width(280.dp)
                        .height(60.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
                ) {
                    Text(text = stringResource(R.string.add_member))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .width(280.dp),
                color = Bilboard_green
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = {
                    navControl.navigate("Settings")
                    scope.launch { scState.drawerState.close() }
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(60.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
            ) {
                Text( text = stringResource(R.string.settings))
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {

                },
                modifier = Modifier
                    .width(280.dp)
                    .height(60.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
            ) {
                Text( text = stringResource(R.string.about))
            }

            Spacer(modifier = Modifier.height(15.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .width(280.dp),
                color = Bilboard_green
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
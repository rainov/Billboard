package com.example.billboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerGroupContent(navControl: NavController, scState: ScaffoldState, scope: CoroutineScope, groupInfo: GroupClass, expenseNavControl: NavController) {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text( text = groupInfo.name, fontSize = 30.sp )
            Spacer(modifier = Modifier.height(15.dp))
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.85f),
                color = Billboard_green
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
                    Row() {
                        Text( text = member, fontSize = 20.sp )
                        Spacer(modifier = Modifier.width(5.dp))
                        if ( groupInfo.admins.contains(member)) {
                            Text( text = "Admin", fontSize = 12.sp, color = Billboard_green)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate("addMembers")
                        scope.launch { scState.drawerState.close() }
                    },
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                        .height(60.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary )
                ) {
                    Text(text = stringResource(R.string.edit_members))
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
                    .fillMaxWidth(.85f)
                    .height(1.dp),
                color = Billboard_green
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = {
                    navControl.navigate("Settings")
                    scope.launch { scState.drawerState.close() }
                },
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(60.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary )
            ) {
                Text( text = stringResource(R.string.settings))
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    navControl.navigate("About")
                    scope.launch { scState.drawerState.close() }
                },
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(60.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors( contentColor = MaterialTheme.colors.onPrimary )
            ) {
                Text( text = stringResource(R.string.about))
            }

            Spacer(modifier = Modifier.height(15.dp))

            Divider(
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .height(1.dp),
                color = Billboard_green
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
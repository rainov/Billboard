package com.example.billboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainScreen(
    navControl: NavController,
    groups: List<GroupClass>,
    groupsVM: GroupsViewModel,
    scState: ScaffoldState,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope) },
        bottomBar = { BottomBarMainScreen( navControl ) },
        content = { MainScreenContent( navControl, groups, groupsVM) },
        drawerContent = { DrawerMainScreen (
                scState,
                scope,
                DrawerContent( navControl, scState, scope )
            )
        }
    )
}

@Composable
fun MainScreenContent( navControl: NavController, groups: List<GroupClass>, groupsVM: GroupsViewModel) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        //Group cards
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(enabled = true, state = ScrollState(1)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            groups.forEach { group ->
                Spacer(modifier = Modifier.height(5.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.75f)
                        .padding(5.dp)
                        .clickable {
                            navControl.navigate(group.id)
                        },
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, Bilboard_green),
                    backgroundColor = Color.Transparent
                ) {
                    Column(
                    ) {
                        Text(
                            text = group.name,
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


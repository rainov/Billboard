package com.example.billboard

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import java.sql.Timestamp

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
                .fillMaxSize(.88f)
                .verticalScroll(enabled = true, state = ScrollState(1)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text( text = stringResource( R.string.your_groups))
            groups.forEach { group ->
                Spacer(modifier = Modifier.height(5.dp))

                OutlinedButton(
                    onClick = { navControl.navigate( group.id ) },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.onPrimary,
                    ),
                    elevation = ButtonDefaults.elevation(7.dp, 5.dp, 0.dp)
                ) {
                    Text(text = group.name )
                }
            }
        }
    }
}


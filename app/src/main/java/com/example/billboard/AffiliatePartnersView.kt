package com.example.billboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun AffiliatePartnersView(
    navControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope) },
        bottomBar = { BottomBarAboutUs( navControl ) },
        content = { AffiliatePartnersContent( navControl ) },
        drawerContent = { DrawerMainScreen (
            scState,
            scope,
            DrawerContent( navControl, scState, scope )
        )
        }
    )
}

@Composable
fun AffiliatePartnersContent(navControl: NavController) {

    val categories = listOf("Travel", "Shopping", "Group activities")

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(20.dp))
        categories.forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .height(140.dp),
                shape = MaterialTheme.shapes.large,
                elevation = 7.dp
            ) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text( text = category, textAlign = TextAlign.Center, fontSize = 30.sp, color = MaterialTheme.colors.onPrimary )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
package com.example.billboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutUs (
    scState: ScaffoldState,
    navControl: NavController,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope ) },
        bottomBar = { BottomBarAboutUs(navControl) },
        content = { AboutUsContent() },
        drawerContent = { DrawerMainScreen (
                scState,
                scope,
                DrawerContent(navControl , scState, scope )
            )
        }
    )

}

@Composable
fun AboutUsContent() {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Spacer(modifier = Modifier.height(80.dp))
        Box(
            modifier = Modifier
                .fillMaxSize(.8f)
                .border(
                    BorderStroke(.8.dp, Billboard_green),
                    shape = MaterialTheme.shapes.large,
                )
                .padding(15.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Bill", fontSize = 25.sp, color = Billboard_green)
                    Text(text = "Board", fontSize = 25.sp)
                }
                Text(text = "~Pay less together~", fontSize = 20.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = " is a student project for the University of applied sciences Oulu - Finland.", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(50.dp))
                Text(text = "The idea of the app is to serve as an expense tracker for groups, where they " +
                        "can share their bills and keep track of the money flow between the members.", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "The business plan for the project is to serve as a real Billboard, " +
                        "and advertise our affiliate partners and their products/services to our end users.", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Authors:", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Clémence Cunin", fontStyle = FontStyle.Italic, color = Billboard_green, fontSize = 18.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Aleksandar Raynov", fontStyle = FontStyle.Italic, color = Billboard_green, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
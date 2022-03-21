package com.example.billboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun MainScreen( navControl: NavController, groups: List<DocumentSnapshot>) {

    Scaffold(
        topBar = { TopBar(true ) },
        content = { MainScreenContent( navControl, groups) }
    )

}

@Composable
fun MainScreenContent( navControl: NavController, groups: List<DocumentSnapshot>) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        //Group cards
        Column(
            Modifier.fillMaxWidth(),
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
                    elevation = 10.dp,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(2.dp, Bilboard_green),
                    backgroundColor = Color.Transparent
                ) {
                    Column(
                    ) {
                        Text(
                            text = group.get("name").toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(15.dp)
                        )
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .padding(5.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ){
            FloatingActionButton(onClick = { navControl.navigate("createGroup")},
                backgroundColor = Bilboard_green,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_add),
                    contentDescription = "add group",
                    modifier = Modifier.clickable {  navControl.navigate("createGroup")  })
            }
        }

    }
}


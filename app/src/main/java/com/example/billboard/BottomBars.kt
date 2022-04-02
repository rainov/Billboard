package com.example.billboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green

@Composable
fun BottomBarMainScreen(
    navControl: NavController
) {
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        FloatingActionButton(
            onClick = { navControl.navigate( "CreateGroup" ) },
            backgroundColor = Bilboard_green,
            modifier = Modifier
                .padding(30.dp, 30.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_add),
                contentDescription = "add group",
            )
        }
    }
}

@Composable
fun BottomBarGroupScreen(
    navControl: NavController,
    expenseNavControl: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 0.dp, start = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",

            modifier = Modifier
                .clickable { navControl.navigate("MainScreen") }
                .padding(35.dp, 30.dp)
        )
        OutlinedButton(
            onClick = {
                /* TODO */
            },
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
        ) {
            Text(text = stringResource(R.string.delete))
        }

        FloatingActionButton(onClick = { expenseNavControl.navigate("addExpense")},
            backgroundColor = Bilboard_green,
            modifier = Modifier.padding(20.dp, 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_add),
                contentDescription = "add expense",
            )
        }
    }
}
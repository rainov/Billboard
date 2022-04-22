package com.example.billboard


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope

@Composable
fun ReceiptView(
    expenseID: String,
    receiptURL: String,
    expenseNavControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(showMenu = false, scState, false, scope) },
        bottomBar = { BottomBarAddReceipt(expenseNavControl, expenseID ) },
        content = { ReceiptViewContent( expenseNavControl, expenseID, receiptURL ) }
    )
}

@Composable
fun ReceiptViewContent(expenseNavControl: NavController, expenseID: String, receiptURL: String ) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier.weight((2f))
        ) {
            AsyncImage(model = receiptURL, contentDescription = "receipt")
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate("${expenseID}_addReceipt")
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(text = stringResource(R.string.change_image))
                }
        }
    }
}
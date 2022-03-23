package com.example.billboard

import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import kotlinx.coroutines.CoroutineScope

@Composable
fun CreateGroupView( groupsVM: GroupsViewModel, navControl: NavController, scState: ScaffoldState, scope: CoroutineScope) {
    Scaffold(
        topBar = { TopBar(showMenu = true, scState, false, scope ) },
        content = { CreateGroupContent( groupsVM, navControl ) }
    )

}

@Composable
fun CreateGroupContent( groupsVM: GroupsViewModel, navControl: NavController ) {

    var groupName by remember { mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(150.dp))

        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text(text = stringResource(R.string.group_name)) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Bilboard_green,
                cursorColor = Color.White,
                textColor = Color.White,
                focusedLabelColor = Color.White
            ),
            modifier = Modifier.height(64.dp),
            shape = MaterialTheme.shapes.large,
            textStyle = TextStyle(color = Bilboard_green)
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = {
                groupsVM.createGroup( groupName )
                navControl.navigate("MainScreen")
            },
            modifier = Modifier
                .width(280.dp)
                .height(40.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
        ) {
            Text(text = stringResource(R.string.create_group))
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = {
                navControl.navigate("MainScreen")
            },
            modifier = Modifier
                .width(280.dp)
                .height(40.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Bilboard_green)
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}
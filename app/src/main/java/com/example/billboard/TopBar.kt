package com.example.billboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
    fun TopBar( showMenu: Boolean) {
    Row(
        modifier = Modifier
            .padding(0.dp, 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if ( showMenu ) {
            Icon(painter = painterResource(R.drawable.menu_icon), contentDescription = "hamburger menu")
        } else {
            Spacer( modifier = Modifier.width(45.dp))
        }
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Text( text = "Bill", color = Color(0xFFadd27d), style = MaterialTheme.typography.h1 )
            Text( text = "Board", style = MaterialTheme.typography.h1 )
        }
        Spacer(modifier = Modifier.width(45.dp))
    }
}
package com.example.billboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.QueryDocumentSnapshot

@Composable
fun MainScreen( navControl: NavController, groups: MutableList<QueryDocumentSnapshot>) {

    groups.forEach { group ->
        Spacer(modifier = Modifier.height(5.dp))
        Card(
            modifier = Modifier
                .width(240.dp)
                .height(36.dp)
                .clickable {
                    navControl.navigate(
                        group
                            .get("name")
                            .toString()
                    )
                }
        ) {
            Text(text = group.get("name").toString())
        }
    }
}
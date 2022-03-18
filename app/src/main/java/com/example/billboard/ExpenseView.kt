package com.example.billboard

import AddExpenseView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

@Composable
fun ExpenseView(id : String, expenseNavControl: NavController) {
    //TODO fetch expense info in database
    val expenseName : String = "Grocery"
    val expenseAmount : String = "25"
    val payer : String = "John"
    val membersWhoPay : List<String> = listOf("Maeve","Victor","Anna")

    Column(){
        Text(text = "Expense details : $expenseName")
        //TODO need to discuss about default currency, can the user choose one or the group
        Text(text = "$expenseAmount €")
        Text(text = "Payer member : $payer")
        Text(text = "Members who have to pay :")
        membersWhoPay.forEach { member ->
            Text(text = member)
        }

        Row(
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back icon",
                modifier = Modifier.clickable {  expenseNavControl.navigate("group")  })
            OutlinedButton(onClick = { /*TODO Delete function if USER is an admin*/ }) {
                Text("Delete this expense")
            }
            OutlinedButton(onClick = { /*TODO Edit function if USER is an admin -> AddExpenseView(id) */}) {
                Text("Edit this expense")
            }
        }

    }
}
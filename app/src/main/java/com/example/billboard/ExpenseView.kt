package com.example.billboard

import AddExpenseView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
<<<<<<< HEAD
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
=======
import androidx.navigation.NavController
>>>>>>> 770032df31dd613a54248c61b6856d145fbd7941

@Composable
fun ExpenseView(id : String, expenseNavControl: NavController) {
    //TODO fetch expense info in database

    val expenseName = remember { mutableStateOf("") }
    val expenseAmount = remember { mutableStateOf("") }
    val expensePayer = remember { mutableStateOf("") }
    val expenseRest = remember { mutableStateOf(listOf<String>()) }

    getExpenseLine(expenseName, expenseAmount, expensePayer, expenseRest, id)

    Column(){
        Text(text = "Expense details : ${expenseName.value}")
        //TODO need to discuss about default currency, can the user choose one or the group
        Text(text = "${expenseAmount.value} €")
        Text(text = "Payer member : ${expensePayer.value}")
        Text(text = "Members who have to pay :")
        expenseRest.value.forEach { member ->
            Row(){
                Text(text = member)
                Button(onClick = { /*TODO if the user is an admin I can erase the member debt*/ }) {Text(text = "Erase debt")}
            }
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

fun getExpenseLine(expenseName : MutableState<String>,
                   expenseAmount : MutableState<String>,
                   expensePayer : MutableState<String>,
                   expenseRest : MutableState<List<String>>,
                   id : String){

    Firebase.firestore.collection("expenses")
        .document(id)
        .get()
        .addOnSuccessListener {

            var eName = it.get("name").toString()
            var eAmount =  it.get("amount").toString()
            var ePayer = it.get("payer").toString()
            var eRest = mutableListOf<String>()
            val list = it.get("rest") as? List<String>
            list!!.forEach { element ->
                eRest.add(element)
            }

            expenseName.value = eName
            expenseAmount.value = eAmount
            expensePayer.value = ePayer
            expenseRest.value = eRest

        }
}


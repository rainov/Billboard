package com.example.billboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun ExpenseView( expense: DocumentSnapshot, expenseNavControl: NavController, scState: ScaffoldState) {

    Scaffold(
        topBar = { TopBar(showMenu = true, scState) },
        content = { ExpenseViewContent(expense, expenseNavControl) }
    )

}

@Composable
fun ExpenseViewContent( expense: DocumentSnapshot, expenseNavControl: NavController) {

    val expenseName = expense.get("name").toString()
    val expenseAmount = expense.get("amount").toString()
    val expensePayer = expense.get("payer").toString()

    val expenseRest = expense.get("rest") as List<String>

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = expenseName, textAlign = TextAlign.Center, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(20.dp))

            //TODO need to discuss about default currency, can the user choose one or the group
            Text(text = "Amount paid $expenseAmount €")

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Payer member : $expensePayer")

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Members who have to pay :")

            Spacer(modifier = Modifier.height(20.dp))

            expenseRest.forEach { member ->
                Row() {
                    Text(text = member, modifier = Modifier.padding(15.dp))
                    OutlinedButton(
                        onClick = {
                            /*TODO if the user is an admin he can erase the member debt*/
                        },
                        modifier = Modifier
                            .width(150.dp)
                            .height(40.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
                    ) {
                        Text( text = "Erase debt")
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                }
            }
        }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
                    .padding(5.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back icon",
                    modifier = Modifier.clickable { expenseNavControl.navigate("group") })
                OutlinedButton(
                    onClick = {
                        /* TODO Delete function */
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
                ) {
                    Text( text = "Delete")
                }
                OutlinedButton(
                    onClick = {
                        expenseNavControl.navigate(
                            "editExpense/${expense.id}/${expenseName}/${expenseAmount}/${expensePayer}/${
                                expenseRest.joinToString(
                                    ","
                                )
                            }"
                        )
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors( contentColor = Bilboard_green )
                ) {
                    Text( text = "Edit")
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

//fun getExpenseLine(expenseName : MutableState<String>,
//                   expenseAmount : MutableState<String>,
//                   expensePayer : MutableState<String>,
//                   expenseRest : MutableState<List<String>>,
//                   id : String){
//
//    Firebase.firestore.collection("expenses")
//        .document(id)
//        .get()
//        .addOnSuccessListener {
//
//            var eName = it.get("name").toString()
//            var eAmount =  it.get("amount").toString()
//            var ePayer = it.get("payer").toString()
//            var eRest = mutableListOf<String>()
//            val list = it.get("rest") as? List<String>
//            list!!.forEach { element ->
//                eRest.add(element)
//            }
//
//            expenseName.value = eName
//            expenseAmount.value = eAmount
//            expensePayer.value = ePayer
//            expenseRest.value = eRest
//
//        }
//}


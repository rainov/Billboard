package com.example.billboard

import AddExpenseView
import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun GroupViewNavigationContainer( navControl: NavController, groupInfo: QueryDocumentSnapshot) {
    val firestore = Firebase.firestore

    var expenses by remember { mutableStateOf(mutableListOf<DocumentSnapshot>()) }
    val expenseIds: List<String> = groupInfo.get("expenses") as List<String>
    val tempExpenses by remember {mutableStateOf(mutableListOf<DocumentSnapshot>())}
    expenseIds.forEach { id ->
        firestore
            .collection("expenses")
            .document(id)
            .get()
            .addOnSuccessListener { expense ->
                if( !tempExpenses.contains(expense) ) {
                    tempExpenses.add(expense)
                    Log.d("mss", expense.toString())
                }
            }
        Log.d("temp", tempExpenses.toString())
    }
    expenses = tempExpenses

    Log.d("exp", expenses.toString())

    val expenseNavControl = rememberNavController()

    //NavHost(navController = expenseNavControl, startDestination = groupInfo.get("name").toString() ) {
    NavHost(navController = expenseNavControl, startDestination = "group" ) {
        //composable( route = groupInfo.get("name").toString() ) {
        composable( route = "group" ) {
            GroupView( groupInfo, expenses, expenseNavControl, navControl )
        }
        expenses.forEach { expense ->
            composable( route = expense.get("name").toString()) {
                //Here you can pass the expense as an argument to the ExpenseView screen, and you have all the information about it
                //so no need to fetch it there :D
                ExpenseView( expense, expenseNavControl )
            }
        }
        composable(route = "addExpense"){
            AddExpenseView(groupid = groupInfo.id, expenseNavControl = expenseNavControl)
        }
        composable(route = "editExpense/{expenseid}/{expensename}/{expenseamount}/{expensepayer}/{expenserest}",
        arguments = listOf(
            navArgument("expenseid"){
                type = NavType.StringType
            },
            navArgument("expensename"){
                type = NavType.StringType
            },
            navArgument("expenseamount"){
                type = NavType.StringType
            },
            navArgument("expensepayer"){
                type = NavType.StringType
            },
            navArgument("expenserest"){
                type = NavType.StringType
            }

        )){
            AddExpenseView(
                id = it.arguments?.getString("expenseid")!!,
                name = it.arguments?.getString("expensename")!!,
                amount = it.arguments?.getString("expenseamount")!!,
                payer = it.arguments?.getString("expensepayer")!!,
                rest = it.arguments?.getString("expenserest")!!,
                groupid = groupInfo.id,
                expenseNavControl = expenseNavControl)
        }
    }
}

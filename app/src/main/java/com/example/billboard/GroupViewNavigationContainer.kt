package com.example.billboard

import AddExpenseView

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun GroupViewNavigationContainer( navControl: NavController, groupInfo: DocumentSnapshot) {

    val expensesVM: ExpensesViewModel = viewModel()

    expensesVM.getExpenses( groupInfo.id )

    val expenses = expensesVM.expenses.value

    val expenseNavControl = rememberNavController()

    NavHost(navController = expenseNavControl, startDestination = "group" ) {
        composable( route = "group" ) {
            GroupView( groupInfo, expenses, expenseNavControl, navControl )
        }
        composable( route = "addExpense") {
            AddExpenseView( groupInfo, expenseNavControl )
        }
        expenses.forEach { expense ->
            composable( route = expense.get("name").toString()) {
                //Here you can pass the expense as an argument to the ExpenseView screen, and you have all the information about it
                //so no need to fetch it there :D
                ExpenseView( expense, expenseNavControl )
            }
        }
    }
}

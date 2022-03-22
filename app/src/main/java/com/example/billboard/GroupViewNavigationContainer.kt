package com.example.billboard

import AddEditExpenseView
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun GroupViewNavigationContainer( navControl: NavController, groupInfo: DocumentSnapshot) {

    val expensesVM: ExpensesViewModel = viewModel()

    expensesVM.getExpenses( groupInfo.id )

    val expenses = expensesVM.expenses

    val expenseNavControl = rememberNavController()

    NavHost(navController = expenseNavControl, startDestination = "group" ) {
        composable( route = "group" ) {
            GroupView( groupInfo, expenses, expenseNavControl, navControl )
        }
        composable( route = "addExpense") {
            val expense = expensesVM.createExpense(groupid = groupInfo.id)
            AddEditExpenseView(groupInfo, expenseNavControl, expensesVM, expense)
        }
        expenses.forEach { expense ->
            composable( route = expense.expid) {
                //Here you can pass the expense as an argument to the ExpenseView screen, and you have all the information about it
                //so no need to fetch it there :D
                ExpenseView( expense, expenseNavControl )
            }
            composable( route = "${expense.expid}_edit"){
                AddEditExpenseView(groupInfo, expenseNavControl, expensesVM, expense)
            }
        }

        //TODO Add new group navigation
        composable(route = "createGroup"){
            createGroup()
        }
    }
}

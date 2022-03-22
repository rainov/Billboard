package com.example.billboard


import androidx.compose.material.ScaffoldState
import AddEditExpenseView
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

@Composable
fun GroupViewNavigationContainer(
    navControl: NavController,
    groupInfo: DocumentSnapshot,
    scState: ScaffoldState,
    groupsVM: GroupsViewModel
) {

    val expensesVM: ExpensesViewModel = viewModel()

    expensesVM.getExpenses( groupInfo.id )

    val expenses = expensesVM.expenses

    val expenseNavControl = rememberNavController()

    NavHost(navController = expenseNavControl, startDestination = "group" ) {
        composable( route = "group" ) {
            GroupView( groupInfo, expenses, expenseNavControl, navControl, scState )
        }
        composable( route = "addExpense") {
            val name = ""
            val amount = 0.0
            val payer = ""
            val expid = ""
            //val expense = expensesVM.createExpense(groupid = groupInfo.id)
            val date = Calendar.getInstance().time.toString()
            val rest = mutableListOf<String>()
            val expense = ExpenseClass( name, amount, payer, date, groupInfo.id, rest, expid)
            AddEditExpenseView(groupInfo, expenseNavControl, expensesVM, expense, groupsVM)
        }
        expenses.forEach { expense ->
            composable( route = expense.expid) {
                ExpenseView( expense, expenseNavControl, scState )
            }
            composable( route = "${expense.expid}_edit"){
                AddEditExpenseView(groupInfo, expenseNavControl, expensesVM, expense, groupsVM )
            }
        }
    }
}

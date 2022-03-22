package com.example.billboard

import AddExpenseView
import androidx.compose.material.ScaffoldState
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
fun GroupViewNavigationContainer(
    navControl: NavController,
    groupInfo: DocumentSnapshot,
    groupsVM: GroupsViewModel,
    scState: ScaffoldState
) {

    val expensesVM: ExpensesViewModel = viewModel()

    expensesVM.getExpenses( groupInfo.id )

    val expenses = expensesVM.expenses.value

    val expenseNavControl = rememberNavController()

    NavHost(navController = expenseNavControl, startDestination = "group" ) {
        composable( route = "group" ) {
            GroupView( groupInfo, expenses, expenseNavControl, navControl, groupsVM, scState )
        }
        composable( route = "addExpense") {
            AddExpenseView(groupInfo = groupInfo, expenseNavControl = expenseNavControl )
        }
        expenses.forEach { expense ->
            composable( route = expense.get("name").toString()) {
                //Here you can pass the expense as an argument to the ExpenseView screen, and you have all the information about it
                //so no need to fetch it there :D
                ExpenseView( expense, expenseNavControl, scState )
            }
        }
        composable(route = "addExpense"){
            AddExpenseView(groupInfo = groupInfo, expenseNavControl = expenseNavControl)
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
                groupInfo = groupInfo,
                expenseNavControl = expenseNavControl)
        }

        //TODO Add new group navigation
        composable(route = "createGroup"){
            createGroup()
        }
    }
}

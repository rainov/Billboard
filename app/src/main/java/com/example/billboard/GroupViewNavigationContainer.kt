@file:Suppress("SpellCheckingInspection")

package com.example.billboard

/*===================================================/
|| Entry point for the groups navigation stack
/====================================================*/

import android.annotation.SuppressLint
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Composable
fun GroupViewNavigationContainer(
    navControl: NavController,
    groupInfo: GroupClass,
    scState: ScaffoldState,
    groupsVM: GroupsViewModel,
    scope: CoroutineScope,
    userVM : UserViewModel
) {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Uses the expense view model to fetch all group expenses and pass them to the corresponding views //
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    val expensesVM: ExpensesViewModel = viewModel()
    expensesVM.getExpenses( groupInfo.id )
    val expenses = expensesVM.expenses

    val expenseNavControl = rememberNavController()

    ///////////////////////////////////////////
    // Navigation host for the groups stack //
    /////////////////////////////////////////
    NavHost(navController = expenseNavControl, startDestination = "group" ) {

        ///////////////////////////////////////////////////
        // Start destination for the stack - Group view //
        /////////////////////////////////////////////////
        composable( route = "group" ) {
            GroupView( groupInfo, expenses, expenseNavControl, navControl, scState, scope, groupsVM, userVM)
        }

        //////////////////////////////////
        // Add/Edit members composable //
        ////////////////////////////////
        composable( route = "addMembers") {
            AddEditMemberView(
                groupsVM,
                userVM,
                scState,
                scope,
                groupInfo,
                expenseNavControl,
                navControl
            )
        }

        /////////////////////////
        // Group balance view //
        ///////////////////////
        composable( route = "groupBalance" ) {
            GroupBalanceView(scState, expenseNavControl, groupInfo, expenses , scope, navControl, userVM, groupsVM)
        }

        ///////////////////////////
        // Add new expense view //
        /////////////////////////
        composable( route = "addExpense") {
            val name = ""
            val amount = 0.0
            val payer = ""
            val expid = ""
            val date = SimpleDateFormat("yyyy/MM/dd_HH/mm/ss").format(Date()).toString()
            val rest = mutableListOf<String>()
            val paidvalues = mutableMapOf<String,Boolean>()
            val receiptURL = ""
            val expense = ExpenseClass( name, amount, payer, date, groupInfo.id, rest, expid, paidvalues, receiptURL)
            AddEditExpenseView(groupInfo, expenseNavControl, expensesVM, expense, scState, groupsVM, scope, userVM)

        }

        expenses.forEach { expense ->

            /////////////////////////////////////////////////
            // Expense view for each expense in the group //
            ///////////////////////////////////////////////
            composable( route = expense.expid) {
                ExpenseView( expense, expenseNavControl, scState, scope, expensesVM, groupsVM, groupInfo, navControl, userVM)
            }

            /////////////////////////////////////////////////////
            // Add receipt view for each expense in the group //
            ///////////////////////////////////////////////////
            composable( route = "${expense.expid}_addReceipt") {
                AddReceipt( expense, expenseNavControl, scState, scope, expensesVM )
            }

            ///////////////////////////////////
            // View receipt for each expense //
            /////////////////////////////////
            composable( route = "${expense.expid}_showReceipt"){
                ReceiptView(
                    expenseID = expense.expid,
                    receiptURL = expense.receiptURL,
                    expenseNavControl,
                    scState,
                    scope
                )
            }

            /////////////////////////////////////////
            // Edit expense view for each expense //
            ///////////////////////////////////////
            composable( route = "${expense.expid}_edit"){
                AddEditExpenseView(groupInfo, expenseNavControl, expensesVM, expense, scState, groupsVM, scope, userVM)
            }
        }
    }
}


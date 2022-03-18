package com.example.billboard

import AddExpenseView
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.billboard.ui.theme.LogRegView

@Composable
fun ViewContainer(){
    AddExpenseView(groupid = "LmApyzhdAiRlPkvTDoHC")
    /*
    val userVM: UserViewModel = viewModel()
    if (!userVM.signedIn.value) {
        LogRegView( userVM )
    } else {
        //AddExpenseView()
        ExpenseView("gg91lhqquxXWURdaxUop")
    }
     */
}
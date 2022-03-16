package com.example.billboard

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.billboard.ui.theme.LogRegView

@Composable
fun ViewContainer(){
    val userVM: UserViewModel = viewModel()
    if (!userVM.signedIn.value) {
        LogRegView( userVM )
    } else {
        Text( text = "You are signed in")
    }
}
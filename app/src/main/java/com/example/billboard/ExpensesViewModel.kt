package com.example.billboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ExpensesViewModel: ViewModel() {

    var expenses = mutableStateOf( listOf<DocumentSnapshot>() )

    fun getExpenses(groupId: String) {

        Firebase.firestore
            .collection("expenses")
            .whereEqualTo("group", groupId)
            .addSnapshotListener { expensesList, error ->
                if (error != null) {
                    error.message?.let { Log.d("err msg: ", it) }
                } else if (expensesList != null && !expensesList.isEmpty) {
                    val tempExpenses = mutableListOf<DocumentSnapshot>()
                    expensesList.documents.forEach { expense ->
                        tempExpenses.add(expense)
                        Log.d("expense log:", "not good!")
                    }
                    expenses.value = tempExpenses
                }
            }
    }
}
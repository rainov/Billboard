package com.example.billboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ExpensesViewModel: ViewModel() {

    var expenses = mutableListOf<ExpenseClass>()

    fun addExpenseLine(newExpense : ExpenseClass, expenseNavControl: NavController){

        val fstore = Firebase.firestore.collection("expenses")

            fstore.add(newExpense)
            .addOnSuccessListener {

                Log.d("Add new expense", it.id)
                fstore.document(it.id).update("expid",it.id)
            }
    }

    fun getExpenses(groupId: String) {

        Firebase.firestore
            .collection("expenses")
            .whereEqualTo("groupid", groupId)
            .addSnapshotListener { expensesList, error ->
                if (error != null) {
                    error.message?.let { Log.d("err msg: ", it) }
                } else if (expensesList != null && !expensesList.isEmpty) {
                    val tempExpenses = mutableListOf<ExpenseClass>()
                    expensesList.documents.forEach { expense ->
                        val newExpense = ExpenseClass(expense.get("name").toString(),
                            expense.get("amount").toString().toDouble(),
                            expense.get("payer").toString(),
                            expense.get("date").toString(),
                            expense.get("groupid").toString(),
                            expense.get("rest") as MutableList<String>,
                            expense.id
                        )
                        tempExpenses.add(newExpense)
                        Log.d("expense log:", "not good!")
                    }
                    expenses = tempExpenses
                }
            }
    }
}
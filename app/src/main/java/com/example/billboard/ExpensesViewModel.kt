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

    fun createExpense (name : String = "",
                       amount : Double = 0.0,
                       payer : String = "",
                       groupid : String,
                       rest : MutableList<String> = mutableListOf(),
                       expid : String = "") : ExpenseClass {
        val newExpense = ExpenseClass(name, amount, payer, Calendar.getInstance().time.toString(), groupid, rest, "")
        return newExpense
    }

    fun addExpenseLine(newExpense : ExpenseClass, expenseNavControl: NavController){

        val fstore = Firebase.firestore.collection("expenses")

            fstore.add(newExpense)
            .addOnSuccessListener {

                Log.d("Add new expense", it.id)
                fstore.document(it.id).update("expid",it.id)

                Firebase.firestore.collection("groups")
                    .document(newExpense.groupid)
                    .update("expenses", FieldValue.arrayUnion(it.id))
                    .addOnSuccessListener {
                        Log.d("Add expense in group", "Success")
                        expenseNavControl.navigate("group")
                    }
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
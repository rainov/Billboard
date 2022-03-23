package com.example.billboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ExpensesViewModel: ViewModel() {

    var expenses = mutableListOf<ExpenseClass>()

    /*
    fun addExpenseLine(newExpense : ExpenseClass, expenseNavControl: NavController, groupsVM: GroupsViewModel){

        val fstore = Firebase.firestore.collection("expenses")

            fstore.add(newExpense)
            .addOnSuccessListener {

                Log.d("Add new expense", it.id)
                fstore.document(it.id).update("expid",it.id)

                        //groupsVM.getGroups()
                        getExpenses(newExpense.groupid)
                        expenseNavControl.navigate("group")
                    }


            }


     */

    fun addExpenseLine(newExpense : ExpenseClass, expenseNavControl: NavController, groupsVM: GroupsViewModel){

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

    fun editExpenseLine(expense : ExpenseClass, expenseNavControl: NavController, groupsVM : GroupsViewModel){

        val firestore = Firebase.firestore.collection("expenses").document(expense.expid)

        firestore.update("name",expense.name)
        firestore.update("amount",expense.amount)
        firestore.update("payer",expense.payer)
        firestore.update("rest",expense.rest)
            .addOnSuccessListener {
                Log.d("Edit expense", expense.expid)
                groupsVM.getGroups()
                expenseNavControl.navigate("group")
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
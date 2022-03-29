package com.example.billboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class ExpensesViewModel: ViewModel() {

    var expenses = mutableListOf<ExpenseClass>()

    fun addExpenseLine(
        newExpense: ExpenseClass,
        expenseNavControl: NavController,
        group: GroupClass,
        groupsVM: GroupsViewModel
    ) {

        val fexp = Firebase.firestore.collection("expenses")
        val fgrp = Firebase.firestore.collection("groups")
        var amountforeach: Double = newExpense.amount / (newExpense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        fexp.add(newExpense)
            .addOnSuccessListener { expense ->
                Log.d("Add new expense", expense.id)
                fexp.document(expense.id).update("expid", expense.id)

                fgrp.document(newExpense.groupid)
                    .update("expenses", FieldValue.arrayUnion(expense.id))

                newExpense.rest.forEach { member ->
                    var previousamt = group.balance[member]?.getValue(newExpense.payer) as Double
                    var prevAmountPayer = group.balance[newExpense.payer]?.getValue(member) as Double
                    group.balance[member]?.set(newExpense.payer,  ( -1 * amountforeach ) + previousamt!!)
                    group.balance[newExpense.payer]?.set(member, amountforeach + prevAmountPayer)
                }

                fgrp.document(newExpense.groupid)
                    .update("balance", group.balance)

                groupsVM.getGroups()
                expenseNavControl.navigate("group")
            }
    }

    fun editExpenseLine(
        expense: ExpenseClass,
        expenseNavControl: NavController,
        group: GroupClass,
        groupsVM: GroupsViewModel,
        formeramount : Double
    ) {

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        val firestore = Firebase.firestore.collection("expenses").document(expense.expid)

        firestore.update("name", expense.name)
        firestore.update("amount", expense.amount)
        firestore.update("payer", expense.payer)
        firestore.update("rest", expense.rest)
            .addOnSuccessListener {
                Log.d("Edit expense", expense.expid)
                //TODO
                //Update balance row if amount change
                //Update balance row if payer changed
                //Update balance row if rest changed
                groupsVM.getGroups()
                expenseNavControl.navigate("group")
            }
    }

    fun deleteExpenseLine(
        expense: ExpenseClass,
        expenseNavControl: NavController,
        groupsVM: GroupsViewModel
    ) {

        val fsexp = Firebase.firestore.collection("expenses").document(expense.expid)
        val fsgrp = Firebase.firestore.collection("groups").document(expense.groupid)

        fsexp.delete()
            .addOnSuccessListener {
                Log.d("Delete expense", expense.expid)
                fsgrp.update("expenses", FieldValue.arrayRemove(expense.expid))
                    .addOnSuccessListener {
                        Log.d("Delete group expense", expense.expid)
                        groupsVM.getGroups()
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
                        val newExpense = ExpenseClass(
                            expense.get("name").toString(),
                            expense.get("amount").toString().toDouble(),
                            expense.get("payer").toString(),
                            expense.get("date").toString(),
                            expense.get("groupid").toString(),
                            expense.get("rest") as MutableList<String>,
                            expense.id
                        )
                        tempExpenses.add(newExpense)
                    }
                    expenses = tempExpenses
                }
            }
    }
}
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
            .addOnSuccessListener {
                Log.d("Add new expense", it.id)
                fexp.document(it.id).update("expid", it.id)


                fgrp.document(newExpense.groupid)
                    .update("expenses", FieldValue.arrayUnion(it.id))
                    .addOnSuccessListener {
                        Log.d("New expense groupid", newExpense.groupid)
                    }


                newExpense.rest.forEach { member ->
                    var previousamt = group.balance[member]?.getValue(newExpense.payer) as Double
                    var prevAmountPayer = group.balance[newExpense.payer]?.getValue(member) as Double
                    group.balance[member]?.set(newExpense.payer, - 1 * amountforeach + previousamt!!)
                    group.balance[newExpense.payer]?.set(member, amountforeach + prevAmountPayer)
                }

                fgrp.document(newExpense.groupid).update("balance", group.balance)

                groupsVM.getGroups()
                expenseNavControl.navigate("group")
            }
    }

    fun editExpenseLine(
        expense: ExpenseClass,
        expenseNavControl: NavController,
        group: GroupClass,
        groupsVM: GroupsViewModel,
        formerexpense : ExpenseClass
    ) {

        var prevamountforeach: Double = formerexpense.amount / (formerexpense.rest.size + 1)
        prevamountforeach = (prevamountforeach * 100.0).roundToInt() / 100.0

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        val firestore = Firebase.firestore.collection("expenses").document(expense.expid)

        firestore.update("name", expense.name)
        firestore.update("amount", expense.amount)
        firestore.update("payer", expense.payer)
        firestore.update("rest", expense.rest)
            .addOnSuccessListener {
                Log.d("Edit expense", expense.expid)

                //First refund all members the former amount expense

                formerexpense.rest.forEach { member ->
                    var previousamt = group.balance[member]?.getValue(formerexpense.payer) as Double
                    var prevAmountPayer = group.balance[formerexpense.payer]?.getValue(member) as Double
                    group.balance[member]?.set(formerexpense.payer, prevamountforeach + previousamt!!)
                    group.balance[formerexpense.payer]?.set(member, - 1 * prevamountforeach + prevAmountPayer)
                }

                //Second update the new bill

                expense.rest.forEach { member ->
                    var previousamt = group.balance[member]?.getValue(expense.payer) as Double
                    var prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
                    group.balance[member]?.set(expense.payer, - 1 * amountforeach + previousamt!!)
                    group.balance[expense.payer]?.set(member, amountforeach + prevAmountPayer)
                }

                groupsVM.editGroup(group)
                expenseNavControl.navigate(expense.expid)
            }
    }

    fun deleteExpenseLine(
        expense: ExpenseClass,
        expenseNavControl: NavController,
        groupsVM: GroupsViewModel,
        group: GroupClass,
        navControl : NavController
    ) {

        val fsexp = Firebase.firestore.collection("expenses").document(expense.expid)
        val fsgrp = Firebase.firestore.collection("groups").document(expense.groupid)

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        fsexp.delete()
            .addOnSuccessListener {
                Log.d("Delete expense", expense.expid)
                fsgrp.update("expenses", FieldValue.arrayRemove(expense.expid))
                    .addOnSuccessListener {
                        Log.d("Delete group expense", expense.expid)

                        expense.rest.forEach { member ->
                            var previousamt = group.balance[member]?.getValue(expense.payer) as Double
                            var prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
                            group.balance[member]?.set(expense.payer, amountforeach + previousamt!!)
                            group.balance[expense.payer]?.set(member, - 1 * amountforeach + prevAmountPayer)
                        }
                        groupsVM.editGroup(group)
                        navControl.navigate(group.id)
                    }
            }
    }

    fun eraseDebt(group: GroupClass, member : String, expense: ExpenseClass, expenseNavControl: NavController,
                  groupsVM: GroupsViewModel, navControl: NavController ){

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        var previousamt = group.balance[member]?.getValue(expense.payer) as Double
        var prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
        group.balance[member]?.set(expense.payer, amountforeach + previousamt!!)
        group.balance[expense.payer]?.set(member, - 1 * amountforeach + prevAmountPayer)

        val firestore = Firebase.firestore.collection("expenses").document(expense.expid)
        firestore.update("amount", expense.amount - amountforeach)
        firestore.update("rest", FieldValue.arrayRemove(member))

        groupsVM.getGroups()
        navControl.navigate(group.id)
        expenseNavControl.navigate(expense.expid)
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
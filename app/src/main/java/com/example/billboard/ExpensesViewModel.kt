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

    val fexp = Firebase.firestore.collection("expenses")
    val fgrp = Firebase.firestore.collection("groups")

    fun addExpenseLine(
        newExpense: ExpenseClass,
        expenseNavControl: NavController,
        group: GroupClass,
        groupsVM: GroupsViewModel
    ) {

        var amountforeach: Double = newExpense.amount / (newExpense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        newExpense.rest.forEach { member ->
            newExpense.paidvalues[member] = false
        }


        fexp.add(newExpense)
            .addOnSuccessListener { expense ->
                Log.d("Add new expense", expense.id)
                fexp.document(expense.id).update("expid", expense.id)


                fgrp.document(newExpense.groupid)

                    .update("expenses", FieldValue.arrayUnion(expense.id))
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
        prevamount : Double,
        prevpayer : String,
        prevrest : MutableList<String>
    ) {

        //TODO delete -> add

        var prevamountforeach: Double = prevamount / (prevrest.size + 1)
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

                prevrest.forEach { member ->
                    Log.d("*********Member", member)
                    var previousamt = group.balance[member]?.getValue(prevpayer) as Double
                    var prevAmountPayer = group.balance[prevpayer]?.getValue(member) as Double
                    group.balance[member]?.set(prevpayer, prevamountforeach + previousamt!!)
                    group.balance[prevpayer]?.set(member, - 1 * prevamountforeach + prevAmountPayer)
                    Log.d("***********Balance", group.balance.toString())
                    Log.d("***********pre amount", previousamt.toString())
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
        expenseNavControl : NavController,
        groupsVM: GroupsViewModel,
        group: GroupClass,
        navControl : NavController
    ) {

        //If a user has erased a debt before deleting the expense, it will refund him, and the payer member will have to pay him back

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
                        fsgrp.update("balance", group.balance, "expenses", FieldValue.arrayRemove(expense.expid))

                        groupsVM.getGroups()
                        navControl.navigate(group.id)
                        //TODO previous line must be changed to lead on expense view
                    }
            }
    }

    fun deleteExpenseLineCancelEraseDebts(
        expense: ExpenseClass,
        groupsVM: GroupsViewModel,
        group: GroupClass,
        navControl : NavController
    ){
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
                            if(expense.paidvalues[member] == false) {
                                //User balance needs to be increased and payer decreased
                                var previousamt =
                                    group.balance[member]?.getValue(expense.payer) as Double
                                var prevAmountPayer =
                                    group.balance[expense.payer]?.getValue(member) as Double
                                group.balance[member]?.set(
                                    expense.payer,
                                    amountforeach + previousamt!!
                                )
                                group.balance[expense.payer]?.set(
                                    member,
                                    -1 * amountforeach + prevAmountPayer
                                )
                            }
                        }
                        fsgrp.update("balance", group.balance, "expenses", FieldValue.arrayRemove(expense.expid))

                        groupsVM.getGroups()
                        navControl.navigate(group.id)
                        //TODO previous line must be changed to lead on expense view
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

        expense.paidvalues[member] = true

        fexp.document(expense.expid).update("paidvalues",expense.paidvalues)
        fgrp.document(group.id).update("balance", group.balance)

        groupsVM.getGroups()
        navControl.navigate(group.id)
        //TODO previous line must be changed to lead on expense view
    }

    fun cancelEraseDebt(group: GroupClass, member : String, expense: ExpenseClass, expenseNavControl: NavController,
                  groupsVM: GroupsViewModel, navControl: NavController ){

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        var previousamt = group.balance[member]?.getValue(expense.payer) as Double
        var prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
        group.balance[member]?.set(expense.payer, - 1 * amountforeach + previousamt!!)
        group.balance[expense.payer]?.set(member, amountforeach + prevAmountPayer)

        expense.paidvalues[member] = false

        fexp.document(expense.expid).update("paidvalues",expense.paidvalues)
        fgrp.document(group.id).update("balance", group.balance)

        groupsVM.getGroups()
        navControl.navigate(group.id)
        //TODO previous line must be changed to lead on expense view
    }

    fun eraseAllDebts(group: GroupClass, expense: ExpenseClass, expenseNavControl: NavController,
                      groupsVM: GroupsViewModel, navControl: NavController){

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        expense.rest.forEach { member ->
            if(expense.paidvalues[member] == false){
                var previousamt = group.balance[member]?.getValue(expense.payer) as Double
                var prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
                group.balance[member]?.set(expense.payer, amountforeach + previousamt!!)
                group.balance[expense.payer]?.set(member, - 1 * amountforeach + prevAmountPayer)
                expense.paidvalues[member] = true
            }
        }

        fexp.document(expense.expid).update("paidvalues",expense.paidvalues)
        fgrp.document(group.id).update("balance", group.balance)

        groupsVM.getGroups()
        navControl.navigate(group.id)
        //TODO previous line must be changed to lead on expense view
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
                            expense.id,
                            expense.get("paidvalues") as MutableMap<String,Boolean>
                        )
                        tempExpenses.add(newExpense)
                    }
                    expenses = tempExpenses
                }
            }
    }
}
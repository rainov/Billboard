package com.example.billboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ExpensesViewModel: ViewModel() {

    var expenses = mutableListOf<ExpenseClass>()

    val fexp = Firebase.firestore.collection("expenses")
    val fgrp = Firebase.firestore.collection("groups")

    fun addExpenseLine(
        newExpense: ExpenseClass,
        expenseNavControl: NavController,
        group: GroupClass,
        groupsVM: GroupsViewModel,
        userVM: UserViewModel
    ) {



        var amountforeach: Double = newExpense.amount / (newExpense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        Log.d("*****", amountforeach.toString())

        if ( newExpense.expid.isEmpty() ) {
            newExpense.rest.forEach { member ->
                val previousamt = group.balance[member]?.getValue(newExpense.payer) as Double
                val prevAmountPayer = group.balance[newExpense.payer]?.getValue(member) as Double
                val formatedMemberAmt = ((- 1 * amountforeach + previousamt)*100.0).roundToInt() / 100.0
                group.balance[member]?.set(newExpense.payer, formatedMemberAmt)
                group.balance[newExpense.payer]?.set(member, ((amountforeach + prevAmountPayer)*100.0).roundToInt() / 100.0)

                newExpense.paidvalues[member] = false

            }
        }



        fexp.add(newExpense)
            .addOnSuccessListener { expense ->
                Log.d("Add new expense", expense.id)
                fexp.document(expense.id).update("expid", expense.id)
                if ( newExpense.expid.isEmpty() ) {
                    userVM.logAction("Expense added")
                    fgrp.document(newExpense.groupid)
                        .update("expenses", FieldValue.arrayUnion(expense.id), "balance", group.balance )
                        .addOnSuccessListener {
                            Log.d("New expense groupid", newExpense.groupid)
                            groupsVM.getGroups()
                            expenseNavControl.navigate("group")
                        }
                } else {
                    userVM.logAction("Expense edited")
                    fexp.document(newExpense.expid)
                        .delete()
                        .addOnSuccessListener {
                            fgrp.document(newExpense.groupid)
                                .update("expenses", FieldValue.arrayRemove(newExpense.expid))
                        }
                    fgrp.document(newExpense.groupid)
                        .update("expenses", FieldValue.arrayUnion(expense.id), "balance", group.balance )
                        .addOnSuccessListener {
                            Log.d("New expense groupid", newExpense.groupid)
                            groupsVM.getGroups()
                            expenseNavControl.navigate("group")
                        }
                }
            }
    }

    fun editExpenseLine(
        expenseNavControl: NavController,
        group: GroupClass,
        groupsVM: GroupsViewModel,
        newExpense : ExpenseClass,
        userVM: UserViewModel
    ) {

        Firebase.firestore
            .collection("expenses")
            .document(newExpense.expid)
            .get()
            .addOnSuccessListener { expenseSnapshot ->
                val oldExpense = ExpenseClass(
                    expenseSnapshot.get("name").toString(),
                    expenseSnapshot.get("amount") as Double,
                    expenseSnapshot.get("payer").toString(),
                    expenseSnapshot.get("date").toString(),
                    expenseSnapshot.get("groupid").toString(),
                    expenseSnapshot.get("rest") as MutableList<String>,
                    expenseSnapshot.get("expid").toString(),
                    expenseSnapshot.get("paidvalues") as MutableMap<String, Boolean>,
                    expenseSnapshot.get("receiptURL").toString()
                )

                var oldSingleShare: Double = oldExpense.amount / (oldExpense.rest.size + 1)
                oldSingleShare = (oldSingleShare * 100.0).roundToInt() / 100.0

                var newSingleShare: Double = newExpense.amount / (newExpense.rest.size + 1)
                newSingleShare = (newSingleShare * 100.0).roundToInt() / 100.0

                oldExpense.rest.forEach { member ->
                    val oldPayer = group.balance[member]?.getValue(oldExpense.payer) as Double
                    val oldMember = group.balance[oldExpense.payer]?.getValue(member) as Double
                    group.balance[member]?.set(oldExpense.payer, ((oldPayer + oldSingleShare)*100.0).roundToInt() / 100.0)
                    val formatedPayerAmt = ((oldMember - oldSingleShare)*100.0).roundToInt() / 100.0
                    group.balance[oldExpense.payer]?.set(member, formatedPayerAmt)
                }

                newExpense.date = SimpleDateFormat("yyyy/MM/dd_HH/mm/ss").format(Date()).toString()

                Firebase.firestore
                    .collection("groups")
                    .document(newExpense.groupid)
                    .update("balance", group.balance)
                    .addOnSuccessListener {
                        newExpense.rest.forEach { member ->
                            val newPayer = group.balance[member]?.getValue(newExpense.payer) as Double
                            val newMember = group.balance[newExpense.payer]?.getValue(member) as Double
                            group.balance[member]?.set(newExpense.payer, ((newPayer - newSingleShare)*100.0).roundToInt() / 100.0)
                            group.balance[newExpense.payer]?.set(member, ((newMember + newSingleShare)*100.0).roundToInt() / 100.0)

                            newExpense.paidvalues[member] = false
                            if(newExpense.paidvalues.containsKey(newExpense.payer)) {
                                newExpense.paidvalues.remove(newExpense.payer)
                            }
                        }
                        addExpenseLine( newExpense, expenseNavControl, group, groupsVM, userVM )
                    }
            }
    }

    fun deleteExpenseLine(
        expense: ExpenseClass,
        expenseNavControl : NavController,
        groupsVM: GroupsViewModel,
        group: GroupClass,
        navControl : NavController,
        userVM: UserViewModel
    ) {

        //If a user has erased a debt before deleting the expense, it will refund him, and the payer member will have to pay him back

        val fsexp = Firebase.firestore.collection("expenses").document(expense.expid)
        val fsgrp = Firebase.firestore.collection("groups").document(expense.groupid)

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        fsexp.delete()
            .addOnSuccessListener {
                Log.d("Delete expense", expense.expid)
                userVM.logAction("Clear balance expense deleted")
                fsgrp.update("expenses", FieldValue.arrayRemove(expense.expid))
                    .addOnSuccessListener {
                        Log.d("Delete group expense", expense.expid)

                        expense.rest.forEach { member ->
                            val previousamt = group.balance[member]?.getValue(expense.payer) as Double
                            val prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
                            val formatedPayerAmt = ((- 1 * amountforeach + prevAmountPayer)*100.0).roundToInt() / 100.0
                            group.balance[member]?.set(expense.payer, ((amountforeach + previousamt)*100.0).roundToInt() / 100.0)
                            group.balance[expense.payer]?.set(member, formatedPayerAmt )
                        }

                        fsgrp.update("balance", group.balance, "expenses", FieldValue.arrayRemove(expense.expid))
                        groupsVM.getGroups()
                        navControl.navigate(group.id)
                    }
            }
    }

    fun deleteExpenseLineCancelEraseDebts(
        expense: ExpenseClass,
        groupsVM: GroupsViewModel,
        group: GroupClass,
        navControl : NavController,
        userVM: UserViewModel
    ){
        val fsexp = Firebase.firestore.collection("expenses").document(expense.expid)
        val fsgrp = Firebase.firestore.collection("groups").document(expense.groupid)

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        fsexp.delete()
            .addOnSuccessListener {
                userVM.logAction("Dirty balance expense deleted")
                Log.d("Delete expense", expense.expid)
                fsgrp.update("expenses", FieldValue.arrayRemove(expense.expid))
                    .addOnSuccessListener {
                        Log.d("Delete group expense", expense.expid)

                        expense.rest.forEach { member ->
                            if(expense.paidvalues[member] == false) {
                                //User balance needs to be increased and payer decreased
                                val previousamt =
                                    group.balance[member]?.getValue(expense.payer) as Double
                                val prevAmountPayer =
                                    group.balance[expense.payer]?.getValue(member) as Double
                                group.balance[member]?.set(
                                    expense.payer,
                                    ((amountforeach + previousamt!!)*100.0).roundToInt() / 100.0
                                )
                                val formatedPayerAmt = (( -1 * amountforeach + prevAmountPayer)*100.0).roundToInt() / 100.0
                                group.balance[expense.payer]?.set(
                                    member,
                                    formatedPayerAmt
                                )
                            }
                        }
                        fsgrp.update("balance", group.balance, "expenses", FieldValue.arrayRemove(expense.expid))

                        groupsVM.getGroups()
                        navControl.navigate(group.id)
                    }
            }
    }

    fun eraseDebt(group: GroupClass, member : String, expense: ExpenseClass, expenseNavControl: NavController,
                  groupsVM: GroupsViewModel, navControl: NavController, userVM: UserViewModel ){

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        val previousamt = group.balance[member]?.getValue(expense.payer) as Double
        val prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
        val formatedPayerAmt = (( -1 * amountforeach + prevAmountPayer)*100.0).roundToInt() / 100.0
        group.balance[member]?.set(expense.payer, ((amountforeach + previousamt!!)*100.0).roundToInt() / 100.0)
        group.balance[expense.payer]?.set(member, formatedPayerAmt)

        expense.paidvalues[member] = true

        fexp.document(expense.expid).update("paidvalues",expense.paidvalues)
            .addOnSuccessListener {
                userVM.logAction("Erased user debt from expense")
            }
        fgrp.document(group.id).update("balance", group.balance)

        groupsVM.getGroups()
        expenseNavControl.navigate(expense.expid)
    }

    fun cancelEraseDebt(group: GroupClass, member : String, expense: ExpenseClass, expenseNavControl: NavController,
                  groupsVM: GroupsViewModel, navControl: NavController, userVM: UserViewModel ){

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        val previousamt = group.balance[member]?.getValue(expense.payer) as Double
        val prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
        val formatedPayerAmt = (( - 1 * amountforeach + previousamt)*100.0).roundToInt() / 100.0
        group.balance[member]?.set(expense.payer, formatedPayerAmt)
        group.balance[expense.payer]?.set(member, ((amountforeach + prevAmountPayer)*100.0).roundToInt() / 100.0)

        expense.paidvalues[member] = false

        fexp.document(expense.expid).update("paidvalues",expense.paidvalues)
            .addOnSuccessListener {
                userVM.logAction("Renew erased user debt")
            }
        fgrp.document(group.id).update("balance", group.balance)

        groupsVM.getGroups()
        expenseNavControl.navigate(expense.expid)
    }

    fun eraseAllDebts(group: GroupClass, expense: ExpenseClass, expenseNavControl: NavController,
                      groupsVM: GroupsViewModel, navControl: NavController, userVM: UserViewModel){

        var amountforeach: Double = expense.amount / (expense.rest.size + 1)
        amountforeach = (amountforeach * 100.0).roundToInt() / 100.0

        expense.rest.forEach { member ->
            if(expense.paidvalues[member] == false){
                val previousamt = group.balance[member]?.getValue(expense.payer) as Double
                val prevAmountPayer = group.balance[expense.payer]?.getValue(member) as Double
                val formatedPayerAmt = (( - 1 * amountforeach + prevAmountPayer)*100.0).roundToInt() / 100.0
                group.balance[member]?.set(expense.payer, (((amountforeach + previousamt!!)*100.0).roundToInt())/100.0)
                group.balance[expense.payer]?.set(member, formatedPayerAmt)
                expense.paidvalues[member] = true
            }
        }

        fexp.document(expense.expid).update("paidvalues",expense.paidvalues)
        fgrp.document(group.id).update("balance", group.balance)

        val fsgrp = Firebase.firestore.collection("groups").document(expense.groupid)
        fsgrp.update("balance", group.balance )
            .addOnSuccessListener {
                userVM.logAction("Cleared all debts in an expense")
                groupsVM.getGroups()
            }


        groupsVM.getGroups()
        navControl.navigate(group.id)
    }

    fun getExpenses(groupId: String) {

        Firebase.firestore
            .collection("expenses")
            .whereEqualTo("groupid", groupId)
            .addSnapshotListener { expensesList, error ->
                if (error != null) {
                    error.message?.let { Log.d("err msg: ", it) }
//                } else if (expensesList != null && !expensesList.isEmpty) {
                } else if (expensesList != null) {
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
                            expense.get("paidvalues") as MutableMap<String,Boolean>,
                            expense.get("receiptURL").toString()
                        )
                        tempExpenses.add(newExpense)
                    }
                    expenses = tempExpenses
                }
            }
    }

    fun addReceipt( id: String, receiptURL: String, expenseNavControl: NavController){
        Firebase.firestore
            .collection("expenses")
            .document(id)
            .update("receiptURL", receiptURL)
            .addOnSuccessListener {
                expenseNavControl.navigate(id)
            }
    }

}
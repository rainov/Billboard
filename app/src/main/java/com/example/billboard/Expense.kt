package com.example.billboard

data class ExpenseClass(
    var name: String,
    var amount: Double,
    var payer: String,
    var date : String,
    var groupid: String,
    var rest: MutableList<String>,
    var expid: String)


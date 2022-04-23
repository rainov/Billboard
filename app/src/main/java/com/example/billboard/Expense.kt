package com.example.billboard

/*====================================================/
|| Expense data class that stores the different data
|| field needed.
/====================================================*/

data class ExpenseClass(
    var name: String,
    var amount: Double,
    var payer: String,
    var date : String,
    var groupid: String,
    var rest: MutableList<String>,
    var expid: String,
    var paidvalues : MutableMap<String,Boolean>,
    var receiptURL: String
)


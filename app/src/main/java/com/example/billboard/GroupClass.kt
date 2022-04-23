package com.example.billboard

/*====================================================/
|| Group data class that stores the different data
|| fields needed.
/====================================================*/

data class GroupClass(
    var admins: List<String>,
    val expenses: List<String>,
    val members: List<String>,
    val name: String,
    val balance: MutableMap<String, MutableMap<String, Double>>,
    val id: String
)

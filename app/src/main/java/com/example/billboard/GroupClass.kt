package com.example.billboard

data class GroupClass(
    var admins: List<String>,
    val expenses: List<String>,
    val members: List<String>,
    val name: String,
    val balance: MutableMap<String, MutableList<MutableMap<String, Double>>>,
    val id: String
)

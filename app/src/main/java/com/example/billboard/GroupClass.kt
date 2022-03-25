package com.example.billboard

data class GroupClass(
    val admins: List<String>,
    val expenses: List<String>,
    val members: List<String>,
    val name: String,
    val balance: Map<String, Map<String, Map<String, Double>>> = emptyMap(),
    val id: String
)

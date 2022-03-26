package com.example.billboard

import androidx.compose.runtime.key

data class BalanceClass(val debt: Map<String, Map<String, Double>>?, val collectingMoney: Map<String, Map<String, Double>>?) {
    fun addMember(member: Map<String, Map<String, Double>>) {
        debt?.plus(member)
    }
}
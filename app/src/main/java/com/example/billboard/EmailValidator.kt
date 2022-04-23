package com.example.billboard

////////////////////////////////////
// Simple email string validator //
//////////////////////////////////
class EmailValidator {
    companion object {
        @JvmStatic
        val EMAIL_REGEX = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
        fun isEmailValid(email: String): Boolean {
            return EMAIL_REGEX.toRegex().matches(email)
        }
    }
}
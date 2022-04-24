package com.example.billboard

/*====================================================/
|| Log data class that stores the different data
|| field needed for logging the user's actions.
/====================================================*/

data class LogAction( val uniqueId: String, val timeStamp: String, val action: String )
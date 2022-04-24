package com.example.billboard

/*====================================================/
|| Affiliate partner data class that stores the different
|| data field needed.
/====================================================*/

data class AffiliatePartner(
    val category: String,
    val description: String,
    val name: String,
    val id: String,
    val imgURL: String
)
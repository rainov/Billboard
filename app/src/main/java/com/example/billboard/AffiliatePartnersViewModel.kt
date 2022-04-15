package com.example.billboard

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AffiliatePartnersViewModel: ViewModel() {

    var partners = mutableListOf<AffiliatePartner>()

    var categories = mutableListOf<String>()

    fun getPartners() {
        Firebase
            .firestore
            .collection("partners")
            .get()
            .addOnSuccessListener { allPartners ->
                val tempPartners = mutableListOf<AffiliatePartner>()
                val tempCategories = mutableListOf<String>()
                allPartners.documents.forEach { affPartner ->
                    if ( !tempCategories.contains( affPartner.get("category").toString()) ) {
                        tempCategories.add(affPartner.get("category").toString())
                    }
                    val partner = AffiliatePartner(
                        category = affPartner.get("category").toString(),
                        description = affPartner.get("description").toString(),
                        name = affPartner.get("name").toString(),
                        id = affPartner.id
                    )
                    tempPartners.add(partner)
                }
                partners = tempPartners
                categories = tempCategories
            }
    }

}
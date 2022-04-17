package com.example.billboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun AffiliatePartnersView(
    navControl: NavController,
    affiliateNavControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    partners: MutableList<AffiliatePartner>,
    categories: MutableList<String>,
    selectedCategory: MutableState<String>
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope) },
        bottomBar = { BottomBarAffiliate( navControl, selectedCategory ) },
        content = { AffiliatePartnersContent( partners, affiliateNavControl, selectedCategory, categories ) },
        drawerContent = { DrawerMainScreen (
                scState,
                scope,
                DrawerContent( navControl, scState, scope )
            )
        }
    )

}

@Composable
fun AffiliatePartnersContent(
    partners: MutableList<AffiliatePartner>,
    affiliateNavControl: NavController,
    categoryName: MutableState<String>,
    categories: MutableList<String>
) {

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(20.dp))


        if ( categoryName.value == "" ) {
            categories.forEach { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .clickable {
                            categoryName.value = category
                        }
                        .height(140.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = 7.dp
                ) {
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text( text = category.uppercase(), textAlign = TextAlign.Center, fontSize = 25.sp, color = MaterialTheme.colors.onPrimary )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

            }
        } else {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                partners.forEach { partner ->
                    if (partner.category == categoryName.value) {
                        Card(
                            modifier = Modifier
                                .clickable { affiliateNavControl.navigate(partner.id) }
                                .fillMaxWidth(.8f)
                                .height(70.dp),
                            shape = MaterialTheme.shapes.large,
                            contentColor = MaterialTheme.colors.onPrimary,
                            elevation = 7.dp
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = partner.name, textAlign = TextAlign.Center, fontSize = 20.sp)
                            }
                        }
                        Spacer( modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
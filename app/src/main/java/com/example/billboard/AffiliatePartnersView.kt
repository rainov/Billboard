package com.example.billboard

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope

@Composable
fun AffiliatePartnersView(
    navControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope) },
        bottomBar = { BottomBarAboutUs( navControl ) },
        content = { AffiliatePartnersContent( navControl ) },
        drawerContent = { DrawerMainScreen (
            scState,
            scope,
            DrawerContent( navControl, scState, scope )
        )
        }
    )
}

@Composable
fun AffiliatePartnersContent(navControl: NavController) {

    var categorySelected by remember { mutableStateOf(false) }

    var categoryName by remember { mutableStateOf("")}

    val categories = listOf("Travel", "Shopping", "Group activities")

    val content = "clemence"

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 1024, 1024)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(20.dp))
        Image( bitmap = bitmap.asImageBitmap(), "dsd" )

        if ( !categorySelected ) {
            categories.forEach { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .clickable {
                            categoryName = category
                            categorySelected = true
                        }
                        .height(140.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = 7.dp
                ) {
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text( text = category, textAlign = TextAlign.Center, fontSize = 30.sp, color = MaterialTheme.colors.onPrimary )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

            }
        } else {
            when ( categoryName ) {
                "Travel" -> Column (

                ) {
                    Text(text = "Travel")
                    Text(text = "Info" )
                    Text(text = "Here" )
                }
                "Shopping" -> Column (

                ) {
                    Text(text = "Shopping" )
                    Text(text = "Info" )
                    Text(text = "Here" )
                }
                "Group activities" -> Column (

                ) {
                    Text(text = "Group activities" )
                    Text(text = "Info" )
                    Text(text = "Here" )
                }
            }
        }
    }
}
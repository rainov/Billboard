package com.example.billboard

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope

@Composable
fun PartnerView(
    partner: AffiliatePartner,
    navControl: NavController,
    affiliateNavControl: NavController,
    userVM: UserViewModel,
    scState: ScaffoldState,
    scope: CoroutineScope
) {

    val qrContent = partner.name + userVM.userEmail

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode( qrContent, BarcodeFormat.QR_CODE, 1024, 1024 )
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope) },
        bottomBar = { BottomBarPartner( affiliateNavControl ) },
        content = { PartnerContent( partner, bitmap ) },
        drawerContent = { DrawerMainScreen (
            scState,
            scope,
            DrawerContent( navControl, scState, scope )
        )
        }
    )

}

@Composable
fun PartnerContent( partner: AffiliatePartner, bitmap: Bitmap ) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer( modifier = Modifier.height(10.dp))
        Image( bitmap = bitmap.asImageBitmap(), "dsd" )
        Spacer( modifier = Modifier.height(10.dp))
        Text(text = partner.name, fontSize = 25.sp, textAlign = TextAlign.Center)
        Spacer( modifier = Modifier.height(10.dp))
        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(.85f),
            color = Billboard_green
        )
        Spacer( modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource( R.string.your_code), fontSize = 18.sp, textAlign = TextAlign.Center)
            Spacer( modifier = Modifier.height(10.dp))
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(),
                color = Billboard_green
            )
            Spacer( modifier = Modifier.height(10.dp))
            Text( text =  partner.description, textAlign = TextAlign.Center, fontSize = 20.sp)
        }
    }

}
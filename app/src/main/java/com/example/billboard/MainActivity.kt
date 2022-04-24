package com.example.billboard

/*===================================================/
|| The MainActivity class calls the BillBoard function
|| to start it.
/====================================================*/

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BillBoardApp()
        }
    }

}

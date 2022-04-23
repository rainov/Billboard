@file:Suppress("SpellCheckingInspection")

package com.example.billboard

/*===================================================/
|| This view is used to upload a picture of a receipt
|| from the device gallery
/====================================================*/

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.Image

@Composable
fun AddReceipt(
    expense: ExpenseClass,
    expenseNavControl: NavController,
    scState: ScaffoldState,
    scope: CoroutineScope,
    expenseVM: ExpensesViewModel,
) {
    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(showMenu = false, scState, false, scope) },
        bottomBar = { BottomBarAddReceipt(expenseNavControl, expense.expid ) },
        content = { AddReceiptContent( expenseNavControl, expense, expenseVM ) }
    )
}

@Composable
fun AddReceiptContent( expenseNavControl: NavController, expense: ExpenseClass, expenseVM: ExpensesViewModel ) {

    ///////////////////////////////
    // Image path on the device //
    /////////////////////////////
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    /////////////////////////////////////////
    // Extension for storing in firestore //
    ///////////////////////////////////////
    var imageExtension by remember { mutableStateOf("") }

    ///////////////////////////////////////////////////////////
    // Bitmap image for displaying preview before uploading //
    /////////////////////////////////////////////////////////
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }

    //////////////////////////////
    // Reference for firestore //
    ////////////////////////////
    val storageRef = Firebase.storage.reference

    /////////////////////////////////////////////////
    // Launcher for selecting picture fom gallery //
    ///////////////////////////////////////////////
    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        if (selectedImageUri != null ) {
            val imageExts = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(selectedImageUri!!))
            imageExtension = imageExts.toString()
        }
    }

    //////////////////////////////////////
    // Task information toast messages //
    ////////////////////////////////////
    fun upToast(context: Context){
        Toast.makeText( context, "UPLOADING", Toast.LENGTH_SHORT).show()
    }
    fun doneToast(context: Context){
        Toast.makeText( context, "UPLOADING COMPLETE", Toast.LENGTH_SHORT).show()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Upload image function, using the expense view model for updating the expense in firebase //
    /////////////////////////////////////////////////////////////////////////////////////////////
    fun uploadImage() {
        upToast(context)
        val receiptRef = storageRef.child("Receipt" + System.currentTimeMillis() + "." + imageExtension)
        receiptRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                doneToast(context)
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { url ->
                        Log.d("imageURL:", url.toString())
                        expense.receiptURL = url.toString()
                        expenseVM.addReceipt( expense.expid, url.toString(), expenseNavControl )
                    }
            }
    }

    ///////////////////////
    // Container column //
    /////////////////////
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.weight((1f))
        ) {

            //////////////////////////////////////////////////////////
            // Image preview, when a file is selected from gallery //
            ////////////////////////////////////////////////////////
            selectedImageUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(context.contentResolver,it)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver,it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let {  btm ->
                    Image(bitmap = btm.asImageBitmap(),
                        contentDescription =null,
                        modifier = Modifier.size(400.dp))
                }
            }

        }

        //////////////////////////////////////////////
        // Container column for the action buttons //
        ////////////////////////////////////////////
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            //////////////////////////
            // Select image button //
            ////////////////////////
            OutlinedButton(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth(.75f)
                    .height(40.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
            ) {
                Text(text = stringResource(R.string.select_image))
            }

            Spacer(modifier = Modifier.height(20.dp))

            ////////////////////////////////////////////////////////
            // Upload button, visible only when file is selected //
            //////////////////////////////////////////////////////
            if ( selectedImageUri != null ) {
                OutlinedButton(
                    onClick = {
                        uploadImage()
                    },
                    modifier = Modifier
                        .fillMaxWidth(.75f)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onPrimary)
                ) {
                    Text(text = stringResource(R.string.upload_image))
                }
            }
        }
    }
}


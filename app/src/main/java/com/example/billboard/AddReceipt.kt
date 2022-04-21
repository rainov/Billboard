package com.example.billboard

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
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
    groupsVM: GroupsViewModel
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(showMenu = false, scState, false, scope) },
        bottomBar = { BottomBarAddReceipt(expenseNavControl, expense.expid ) },
        content = { AddReceiptContent( expenseNavControl, expense, expenseVM, groupsVM ) }
    )
}

@Composable
fun AddReceiptContent( expenseNavControl: NavController, expense: ExpenseClass, expenseVM: ExpensesViewModel, groupsVM: GroupsViewModel ) {

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    var imageExtension by remember { mutableStateOf("") }

    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }

    val storageRef = Firebase.storage.reference

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        Log.d("URI", selectedImageUri.toString())
        if (selectedImageUri != null ) {
            val imageExts = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(selectedImageUri!!))
            imageExtension = imageExts.toString()
            val path = selectedImageUri!!.path
            Log.d("extension", imageExtension)
            Log.d("path", path.toString())
        }
    }

    fun uploadImage() {
        val receiptRef = storageRef.child("Receipt" + System.currentTimeMillis() + "." + imageExtension)
        receiptRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { url ->
                        Log.d("imageURL:", url.toString())
                        expenseVM.addReceipt( expense.expid, expense.groupid, url.toString(), groupsVM, expenseNavControl )
                    }
            }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier.weight((1f))
        ) {

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
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(20.dp))

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


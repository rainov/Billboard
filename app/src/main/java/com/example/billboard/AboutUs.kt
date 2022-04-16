@file:Suppress("SpellCheckingInspection")

package com.example.billboard

/*====================================================/
|| About page with information on the project
|| and Github hyperlink.
/====================================================*/

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutUs (
    scState: ScaffoldState,
    navControl: NavController,
    scope: CoroutineScope
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope ) },
        bottomBar = { BottomBarAboutUs(navControl) },
        content = { AboutUsContent() },
        drawerContent = { DrawerMainScreen (
            scState,
            scope,
            DrawerContent(navControl , scState, scope )
        )
        }
    )

}

@Composable
fun AboutUsContent() {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .fillMaxSize(.8f)
                .border(
                    BorderStroke(.8.dp, Billboard_green),
                    shape = MaterialTheme.shapes.large,
                )
                .padding(15.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(enabled = true, state = ScrollState(1))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Bill", fontSize = 25.sp, color = Billboard_green)
                    Text(text = "Board", fontSize = 25.sp)
                }
                Text(
                    text = stringResource(R.string.slogan),
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.description_1),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = stringResource(R.string.description_2) + " " +
                            stringResource(R.string.description_3),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.description_4) + " " +
                            stringResource(R.string.description_5),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.authors), fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Clémence Cunin",
                    fontStyle = FontStyle.Italic,
                    color = Billboard_green,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Aleksandar Raynov",
                    fontStyle = FontStyle.Italic,
                    color = Billboard_green,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Link to Github repository
                val annotedLink = buildAnnotatedString {
                    val txt = stringResource(R.string.git_repo)
                    append(txt)
                    addStyle(
                        style = SpanStyle(
                            fontSize = 18.sp,
                            color = Billboard_green,
                            textDecoration = TextDecoration.Underline
                        ), start = 0, end = txt.lastIndex + 1
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = "https://github.com/Din20sp-Team10/BillBoard",
                        start = 0,
                        end = txt.lastIndex + 1
                    )
                }

                val uriHandler = LocalUriHandler.current

                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_right_24),
                        contentDescription = "right arrow",
                    )
                    ClickableText(
                        text = annotedLink,
                        onClick = {
                            annotedLink
                                .getStringAnnotations("URL", it, it)
                                .firstOrNull()?.let { stringAnnotation ->
                                    uriHandler.openUri(stringAnnotation.item)
                                }
                        }
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_left_24),
                        contentDescription = "left arrow",
                    )
                }
            }
        }
    }
}
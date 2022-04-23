package com.example.billboard

/*===================================================/
|| This view is showing the balance for each group.
|| Each user is presented by a card, containing info
|| about the money status between him and all others
/====================================================*/

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Billboard_green
import com.example.billboard.ui.theme.Billboard_Red
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt

//////////////////////////////
// Main scaffold container //
////////////////////////////
@Composable
fun GroupBalanceView (
    scState: ScaffoldState,
    expenseNavControl: NavController,
    groupInfo: GroupClass,
    expenses: List<ExpenseClass>,
    scope: CoroutineScope,
    navControl: NavController,
    userVM: UserViewModel,
    groupsVM : GroupsViewModel
) {

    Scaffold(
        scaffoldState = scState,
        topBar = { TopBar(true, scState, false, scope ) },
        bottomBar = { BottomBarBack(expenseNavControl) },
        content = { GroupBalanceContent( groupInfo, expenses ) },
        drawerContent = {
            DrawerMainScreen (
                scState,
                scope,
                DrawerGroupContent(
                    navControl,
                    scState,
                    scope,
                    groupInfo,
                    expenseNavControl,
                    userVM,
                    groupsVM
                )
            )
        }
    )
}

///////////////////////////////
// Content for the scaffold //
/////////////////////////////
@Composable
fun GroupBalanceContent(
    groupInfo: GroupClass,
    expenses: List<ExpenseClass>
) {

    //////////////////////////////////////////////////////////////////////////////////////
    // Total money spent by the group... calculated from all the expenses in the group //
    ////////////////////////////////////////////////////////////////////////////////////
    var totalSpent = 0.0
    expenses.forEach { expense ->
        totalSpent = ((totalSpent + expense.amount) * 100.0).roundToInt() / 100.0
    }

    ///////////////////////
    // Container column //
    /////////////////////
    Column(
        modifier = Modifier
            .fillMaxSize(),
        //.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        ////////////////////////////
        // Headline - Group name //
        //////////////////////////
        Text(text = groupInfo.name, textAlign = TextAlign.Center, fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        /////////////////////////////
        // Inner container column //
        ///////////////////////////
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row {
                /////////////////////////////////////////
                // The total money spent by the group //
                ///////////////////////////////////////
                Text( text = stringResource( R.string.total_spent ), fontSize = 20.sp)
                Spacer(modifier = Modifier.width( 15.dp))
                Text( text = totalSpent.toString() + stringResource(R.string.euro_sign), fontSize = 20.sp, color = Billboard_green )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.83f),
                color = Billboard_green
            )
            Spacer(modifier = Modifier.height(15.dp))

            /////////////////////////////////////////////////////////////////////////////////////////
            // Vertical scroll column containing cards with balance information about each member //
            ///////////////////////////////////////////////////////////////////////////////////////
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(.86f)
                    .verticalScroll(enabled = true, state = ScrollState(1)),
            ) {
                groupInfo.members.forEach { member ->

                    ///////////////////////////
                    // Card for each member //
                    /////////////////////////
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(.85f)
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                                shape = MaterialTheme.shapes.large,
                            ),
                        shape = MaterialTheme.shapes.large,
                        elevation = 7.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            /////////////////////////////////////////////////////////////////////
                            // Username/email of the user based on if he/she is registered or not //
                            ///////////////////////////////////////////////////////////////////
                            val uname = remember { mutableStateOf("default")}
                            getUsername(member, uname)
                            if(uname.value == "null") uname.value = member
                            Text( text = uname.value, textAlign = TextAlign.Center, fontSize = 19.sp )

                            Spacer(modifier = Modifier.height(5.dp))

                            Divider(
                                modifier = Modifier.height(1.dp),
                                color = MaterialTheme.colors.onPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            ////////////////////////////////////////////////////////////
                            // The balance between the info Card user and all others //
                            //////////////////////////////////////////////////////////
                            groupInfo.balance[member]?.forEach { other ->
                                var color: Color = MaterialTheme.colors.onPrimary
                                var amount = 0.0

                                if ( other.value < 0 ) {
                                    color = Billboard_Red
                                    amount = other.value * -1
                                } else if (other.value > 0){
                                    color = MaterialTheme.colors.onPrimary
                                    amount = other.value
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val uname = remember { mutableStateOf("default")}
                                    getUsername(other.key, uname)
                                    if(uname.value == "null") uname.value = other.key
                                    Text( text = uname.value + ": " )
                                    Text( text = amount.toString(), color = color)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

/////////////////////////////////////////////////////////////////
// Function to fetch the chosen username for registered users //
///////////////////////////////////////////////////////////////
fun getUsername(member : String, username : MutableState<String>){
    Firebase.firestore
        .collection("users")
        .document(member)
        .get()
        .addOnSuccessListener {
                username.value = it.get("username").toString()
            }
        }
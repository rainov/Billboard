package com.example.billboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.res.painterResource

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.billboard.ui.theme.Bilboard_green
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun GroupView(
    groupInfo: DocumentSnapshot,
    expenses: List<ExpenseClass>,
    expenseNavControl: NavController,
    navControl: NavController,
    scState: ScaffoldState
) {

    Scaffold(
        topBar = { TopBar(showMenu = true, scState) },
        content = { GroupViewContent( groupInfo, expenses, expenseNavControl, navControl ) }
    )

}

@Composable
fun GroupViewContent( groupInfo: DocumentSnapshot, expenses: List<ExpenseClass>, expenseNavControl: NavController, navControl: NavController ){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ){


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = groupInfo.get("name").toString(), modifier = Modifier.clickable { navControl.navigate("MainScreen") }, textAlign = TextAlign.Center, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(20.dp))

            var adminlist : String = ""
            groupInfo.get("admins").toString().subSequence(1,groupInfo.get("admins").toString().lastIndex).split(",").forEach { admin ->
                adminlist = if(adminlist.isEmpty()) admin.substringBefore("@")
                else adminlist + ", " + admin.substringBefore("@")
            }

            var memberlist : String = ""
            groupInfo.get("members").toString().subSequence(1,groupInfo.get("members").toString().lastIndex).split(",").forEach { member ->
                memberlist = if(memberlist.isEmpty()) member.substringBefore("@")
                else memberlist + ", " + member.substringBefore("@")
            }

            Text(text = "Admin(s): $adminlist", modifier = Modifier.clickable { navControl.navigate("MainScreen") })
            Text(text = "Member(s) $memberlist", modifier = Modifier.clickable { navControl.navigate("MainScreen") })

            Spacer(modifier = Modifier.height(20.dp))

            expenses.forEach{ expense ->
                Spacer(modifier = Modifier.height(5.dp))

                Card( modifier = Modifier
                    .padding(5.dp)
                    .clickable { expenseNavControl.navigate(expense.expid) }
                    .fillMaxWidth(fraction = 0.75f),
                    elevation = 10.dp,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(2.dp, Bilboard_green),
                    backgroundColor = Color.Transparent
                ){
                    Text( text = expense.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(15.dp))
                }

            }
        }
        Row(
            modifier = Modifier
            .fillMaxWidth()
            .weight(1f, false)
            .padding(end = 10.dp, start = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back icon",
                modifier = Modifier.clickable {  navControl.navigate("MainScreen")  })
            FloatingActionButton(onClick = { navControl.navigate("createGroup")},
                backgroundColor = Bilboard_green,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_add),
                    contentDescription = "add expense",
                    modifier = Modifier.clickable {  expenseNavControl.navigate("addExpense")  })
            }
        }
    }
}

//TODO createGroup function and view
fun createGroup(){

}

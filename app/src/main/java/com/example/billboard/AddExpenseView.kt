import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.billboard.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


@Composable

fun AddExpenseView(groupInfo: DocumentSnapshot, expenseNavControl: NavController) {

    var menuExpanded by remember { mutableStateOf(false) }

    /*
    if the id is not empty -> fetch the data to prefill the edit form
     */
    val groupMembers = remember { mutableStateOf(listOf<String>()) }

    getGroupMembers(groupInfo.id, groupMembers)

    var dropDownWidth by remember { mutableStateOf(0) }

    var expenseName by remember { mutableStateOf("")}
    var expenseAmount by remember { mutableStateOf("")}
    var payerMember: String by remember { mutableStateOf("") }
    val membersWhoPay = remember {mutableStateListOf<String>()}

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "BillBoard")
        Text(text = "Add a new expense line")
        OutlinedTextField(value = expenseName, onValueChange = {expenseName = it}, label = { Text(text = "Expense name") })
        OutlinedTextField(value = expenseAmount, onValueChange = {expenseAmount = it}, label = { Text(text = "Expense amount") }, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
        Column() {
            OutlinedTextField(
                value = payerMember,
                onValueChange = { payerMember = it },
                modifier = Modifier
                    .onSizeChanged {
                        dropDownWidth = it.width
                    },
                label = { Text("Payer member") },
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, "Arrow for dropdownmenu",
                        Modifier.clickable { menuExpanded = !menuExpanded })
                }
            )
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { dropDownWidth.toDp() })
            ) {
                groupMembers.value.forEach { member ->
                    DropdownMenuItem(onClick = {
                        payerMember = member
                        if(membersWhoPay.contains(payerMember)) membersWhoPay.remove(payerMember)
                    }) {
                        Text(text = member)
                    }
                }
            }
        }

        Text(text = "Members who have to pay")
        groupMembers.value.forEach { member ->
            if (member != payerMember) {
                Row() {
                    CheckBox(member, membersWhoPay)
                    Text(member)
                }
            }
        }
        var expenseInfo: MutableState<String> = remember { mutableStateOf("")}
        Button(onClick = {
            if(expenseName.isNotEmpty() && expenseAmount.isNotEmpty() && payerMember.isNotEmpty() && membersWhoPay.isNotEmpty()){
            addExpenseLine(
                name = expenseName,
                amount = expenseAmount,
                payer = payerMember,
                membersWhoPay = membersWhoPay,
                groupid = groupInfo.id)
            }}){
            Text(text = "Add a new expense line")
        }
        if(expenseInfo.value.isNotEmpty()){
            Text(text = expenseInfo.value)
        }

        Row(
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back icon",
                modifier = Modifier.clickable {  expenseNavControl.navigate("group")  })
        }
    }
}


@Composable
fun CheckBox(member : String, membersWhoPay : SnapshotStateList<String>){
    val checkState = remember {mutableStateOf(false)}
    Checkbox(
        checked = checkState.value,
        onCheckedChange = { checkState.value = it; if(checkState.value) membersWhoPay.add(member) else membersWhoPay.remove(member)  }
    )
}

/* CAN BE DELETED
fun showExpenseData(name : String, amount : String, payer : String, membersWhoPay : SnapshotStateList<String>, expenseInfo : MutableState<String>){
    expenseInfo.value = "Expense line data : Name -> $name, Amount -> $amount, Payer -> $payer, Members who pay -> "
    membersWhoPay.forEach { member ->
        expenseInfo.value = expenseInfo.value + member + " "
    }

}
*/

fun addExpenseLine(name : String, amount : String, payer : String, membersWhoPay : SnapshotStateList<String>, groupid: String){

    val newExpense = hashMapOf<String, Any>(
        "amount" to amount,
        "name" to name,
        "payer" to payer,
        "rest" to membersWhoPay,
        "date" to Calendar.getInstance().time

    )

    Firebase.firestore.collection("expenses")
        .add(newExpense)
        .addOnSuccessListener {

            Log.d("Add new expense", it.id)

            Firebase.firestore.collection("groups")
                .document(groupid)
                .update("expenses", FieldValue.arrayUnion(it.id))
                .addOnSuccessListener {
                    Log.d("Add expense in group", "Success")
                    /*TODO go back to group view*/
                }
        }
}

fun getGroupMembers(groupid : String, listmembers : MutableState<List<String>>){
    Firebase.firestore.collection("groups")
        .document(groupid)
        .get()
        .addOnSuccessListener {
            var members = mutableListOf<String>()
            val list = it.get("members") as? List<String>
            list!!.forEach { element ->
                members.add(element.substringBefore("@"))
            }

            listmembers.value = members
        }
}
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity


@Composable
fun AddExpenseView() {

    var menuExpanded by remember { mutableStateOf(false) }

    //TODO Has to be changed by fetching the group members
    var groupMembers: List<String> = mutableListOf("John", "Maeve")

    var payerMember: String by remember { mutableStateOf("") }
    var membersWhoPay : MutableList<String> = mutableListOf("")

    var dropDownWidth by remember { mutableStateOf(0) }

    var expenseName by remember { mutableStateOf("")}
    var expenseAmount by remember { mutableStateOf("")}


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "BillBoard")
        OutlinedTextField(value = expenseName, onValueChange = {expenseName = it}, label = { Text(text = "Expense name") })
        //TODO only allowed numbers in this textfield -> function
        OutlinedTextField(value = expenseAmount, onValueChange = {expenseAmount = it}, label = { Text(text = "Expense amount") })
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
                groupMembers.forEach { member ->
                    DropdownMenuItem(onClick = {
                        payerMember = member
                    }) {
                        Text(text = member)
                    }
                }
            }
        }

        Text(text = "Members who have to pay")
        groupMembers.forEach { member ->
            if (member != payerMember) {
                Row() {
                    CheckBox(member, membersWhoPay)
                    Text(member)
                }
            }
        }
        //TODO function which add a new expense in the database
        Button(onClick = {}){
            Text(text = "Add a new expense line")
        }
    }
}

@Composable
fun CheckBox(member : String, membersWhoPay : MutableList<String>){
    val checkState = remember {mutableStateOf(true)}
    Checkbox(
        checked = checkState.value,
        onCheckedChange = { checkState.value = it; if(checkState.value) membersWhoPay.add(member) else membersWhoPay.remove(member)   }
    )
}


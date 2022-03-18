import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.billboard.R


@Composable
fun AddExpenseView(groupId : String) {

    var menuExpanded by remember { mutableStateOf(false) }

    //TODO Has to be changed by fetching the group members
    /*
    if the id is not empty -> fetch the data to prefill the edit formgit
     */
    val groupMembers: List<String> = mutableListOf("John", "Maeve", "Davis", "Emma", "Carl")

    var payerMember: String by remember { mutableStateOf("") }

    val membersWhoPay : MutableList<String> = mutableListOf("")

    var dropDownWidth by remember { mutableStateOf(0) }

    var expenseName by remember { mutableStateOf("")}
    var expenseAmount by remember { mutableStateOf("")}


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "BillBoard")
        Text(text = "Add a new expense line")
        OutlinedTextField(value = expenseName, onValueChange = {expenseName = it}, label = { Text(text = "Expense name") })
        //TODO only allowed numbers in this textfield -> function
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
                groupMembers.forEach { member ->
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
        groupMembers.forEach { member ->
            if (member != payerMember) {
                Row() {
                    CheckBox(member, membersWhoPay)
                    Text(member)
                }
            }
        }
        //TODO function which add a new expense in the database and updated the balance row in group collection
        var expenseInfo: MutableState<String> = remember { mutableStateOf("")}
        Button(onClick = {
            if(expenseName.isNotEmpty() && expenseAmount.isNotEmpty() && payerMember.isNotEmpty() && membersWhoPay.isNotEmpty())
                showExpenseData(
                    name = expenseName,
                    amount = expenseAmount,
                    payer = payerMember,
                    membersWhoPay = membersWhoPay,
                    expenseInfo = expenseInfo)
            }){
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
                modifier = Modifier.clickable {  /*TODO add navigation*/  })
        }
    }
}

@Composable
fun CheckBox(member : String, membersWhoPay : MutableList<String>){
    val checkState = remember {mutableStateOf(false)}
    Checkbox(
        checked = checkState.value,
        onCheckedChange = { checkState.value = it; if(checkState.value) membersWhoPay.add(member) else membersWhoPay.remove(member)  }
    )
}

//TODO when updating with add to database function, the following function has to be deleted
fun showExpenseData(name : String, amount : String, payer : String, membersWhoPay : MutableList<String>, expenseInfo : MutableState<String>){
    expenseInfo.value = "Expense line data : Name -> $name, Amount -> $amount, Payer -> $payer, Members who pay -> "
    membersWhoPay.forEach { member ->
        expenseInfo.value = expenseInfo.value + member + " "
    }

}

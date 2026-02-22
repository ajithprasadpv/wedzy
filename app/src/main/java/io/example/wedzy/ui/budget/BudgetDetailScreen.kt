package io.example.wedzy.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.example.wedzy.data.model.BudgetCategory
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.model.PaymentStatus
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    viewModel: BudgetDetailViewModel = hiltViewModel()
) {
    val item by viewModel.item.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    val currency by viewModel.currency.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    fun formatAmount(amount: Double): String = "${currency.symbol}${NumberFormat.getNumberInstance().format(amount)}"
    
    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }
    
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = { viewModel.deleteItem() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        item?.let { currentItem ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = currentItem.category.name.replace("_", " "),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = currentItem.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CostCard(
                        label = "Estimated",
                        amount = formatAmount(currentItem.estimatedCost),
                        modifier = Modifier.weight(1f),
                        onClick = { showEditDialog = true }
                    )
                    CostCard(
                        label = "Actual",
                        amount = formatAmount(currentItem.actualCost),
                        modifier = Modifier.weight(1f),
                        onClick = { showEditDialog = true }
                    )
                }
                
                CostCard(
                    label = "Paid",
                    amount = formatAmount(currentItem.paidAmount),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showEditDialog = true }
                )
                
                if (currentItem.notes.isNotBlank()) {
                    DetailSection(title = "Notes") {
                        Text(
                            text = currentItem.notes,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showEditDialog = true }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Status",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentItem.paymentStatus.name.replace("_", " "),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                OutlinedButton(
                    onClick = { viewModel.deleteItem() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Text("Delete Expense", modifier = Modifier.padding(start = 8.dp))
                }
                
                if (showEditDialog) {
                    EditBudgetItemDialog(
                        item = currentItem,
                        currency = currency,
                        onDismiss = { showEditDialog = false },
                        onSave = { updatedItem ->
                            viewModel.updateItem(updatedItem)
                            showEditDialog = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBudgetItemDialog(
    item: io.example.wedzy.data.model.BudgetItem,
    currency: Currency,
    onDismiss: () -> Unit,
    onSave: (io.example.wedzy.data.model.BudgetItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var category by remember { mutableStateOf(item.category) }
    var estimatedCost by remember { mutableStateOf(if (item.estimatedCost > 0) item.estimatedCost.toLong().toString() else "") }
    var actualCost by remember { mutableStateOf(if (item.actualCost > 0) item.actualCost.toLong().toString() else "") }
    var paidAmount by remember { mutableStateOf(if (item.paidAmount > 0) item.paidAmount.toLong().toString() else "") }
    var paymentStatus by remember { mutableStateOf(item.paymentStatus) }
    var notes by remember { mutableStateOf(item.notes) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentStatusExpanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        BudgetCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name.replace("_", " ")) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = estimatedCost,
                    onValueChange = { estimatedCost = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Estimated Cost") },
                    prefix = { Text(currency.symbol) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = actualCost,
                    onValueChange = { actualCost = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Actual Cost") },
                    prefix = { Text(currency.symbol) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = paidAmount,
                    onValueChange = { paidAmount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Paid Amount") },
                    prefix = { Text(currency.symbol) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                ExposedDropdownMenuBox(
                    expanded = paymentStatusExpanded,
                    onExpandedChange = { paymentStatusExpanded = it }
                ) {
                    OutlinedTextField(
                        value = paymentStatus.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentStatusExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = paymentStatusExpanded,
                        onDismissRequest = { paymentStatusExpanded = false }
                    ) {
                        PaymentStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace("_", " ")) },
                                onClick = {
                                    paymentStatus = status
                                    paymentStatusExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(item.copy(
                        name = name.trim(),
                        category = category,
                        estimatedCost = estimatedCost.toDoubleOrNull() ?: 0.0,
                        actualCost = actualCost.toDoubleOrNull() ?: 0.0,
                        paidAmount = paidAmount.toDoubleOrNull() ?: 0.0,
                        paymentStatus = paymentStatus,
                        notes = notes.trim()
                    ))
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CostCard(
    label: String,
    amount: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

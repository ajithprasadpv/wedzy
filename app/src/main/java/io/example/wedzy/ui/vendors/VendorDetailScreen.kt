package io.example.wedzy.ui.vendors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorCategory
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.example.wedzy.data.model.VendorStatus
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    vendorId: Long,
    onNavigateBack: () -> Unit,
    viewModel: VendorDetailViewModel = hiltViewModel()
) {
    val vendor by viewModel.vendor.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    var showEditDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(vendorId) {
        viewModel.loadVendor(vendorId)
    }
    
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Details", fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = { viewModel.deleteVendor() }) {
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
        vendor?.let { currentVendor ->
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
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = currentVendor.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = currentVendor.category.name.replace("_", " "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        StatusChip(status = currentVendor.status)
                    }
                }
                
                if (currentVendor.quotedPrice > 0 || currentVendor.agreedPrice > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentVendor.quotedPrice > 0) {
                            PriceCard(
                                label = "Quoted",
                                amount = currencyFormatter.format(currentVendor.quotedPrice),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (currentVendor.agreedPrice > 0) {
                            PriceCard(
                                label = "Agreed",
                                amount = currencyFormatter.format(currentVendor.agreedPrice),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusButton(
                        label = "Researching",
                        isSelected = currentVendor.status == VendorStatus.RESEARCHING,
                        onClick = { viewModel.updateStatus(VendorStatus.RESEARCHING) },
                        modifier = Modifier.weight(1f)
                    )
                    StatusButton(
                        label = "Contacted",
                        isSelected = currentVendor.status == VendorStatus.CONTACTED,
                        onClick = { viewModel.updateStatus(VendorStatus.CONTACTED) },
                        modifier = Modifier.weight(1f)
                    )
                    StatusButton(
                        label = "Booked",
                        isSelected = currentVendor.status == VendorStatus.BOOKED,
                        onClick = { viewModel.updateStatus(VendorStatus.BOOKED) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                if (currentVendor.contactPerson.isNotBlank()) {
                    DetailSection(title = "Contact Person") {
                        Text(
                            text = currentVendor.contactPerson,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                if (currentVendor.email.isNotBlank()) {
                    ContactItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = currentVendor.email
                    )
                }
                
                if (currentVendor.phone.isNotBlank()) {
                    ContactItem(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = currentVendor.phone
                    )
                }
                
                if (currentVendor.website.isNotBlank()) {
                    ContactItem(
                        icon = Icons.Default.Language,
                        label = "Website",
                        value = currentVendor.website
                    )
                }
                
                if (currentVendor.address.isNotBlank()) {
                    ContactItem(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        value = currentVendor.address
                    )
                }
                
                if (currentVendor.notes.isNotBlank()) {
                    DetailSection(title = "Notes") {
                        Text(
                            text = currentVendor.notes,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                OutlinedButton(
                    onClick = { viewModel.deleteVendor() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Text("Remove Vendor", modifier = Modifier.padding(start = 8.dp))
                }
                
                if (showEditDialog) {
                    EditVendorDialog(
                        vendor = currentVendor,
                        onDismiss = { showEditDialog = false },
                        onSave = { updatedVendor ->
                            viewModel.updateVendor(updatedVendor)
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
private fun EditVendorDialog(
    vendor: Vendor,
    onDismiss: () -> Unit,
    onSave: (Vendor) -> Unit
) {
    var name by remember { mutableStateOf(vendor.name) }
    var category by remember { mutableStateOf(vendor.category) }
    var contactPerson by remember { mutableStateOf(vendor.contactPerson) }
    var email by remember { mutableStateOf(vendor.email) }
    var phone by remember { mutableStateOf(vendor.phone) }
    var website by remember { mutableStateOf(vendor.website) }
    var address by remember { mutableStateOf(vendor.address) }
    var quotedPrice by remember { mutableStateOf(if (vendor.quotedPrice > 0) vendor.quotedPrice.toLong().toString() else "") }
    var agreedPrice by remember { mutableStateOf(if (vendor.agreedPrice > 0) vendor.agreedPrice.toLong().toString() else "") }
    var notes by remember { mutableStateOf(vendor.notes) }
    var categoryExpanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Vendor") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Vendor Name") },
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
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        VendorCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name.replace("_", " ")) },
                                onClick = { category = cat; categoryExpanded = false }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    label = { Text("Contact Person") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = quotedPrice,
                    onValueChange = { quotedPrice = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Quoted Price") },
                    prefix = { Text("₹") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = agreedPrice,
                    onValueChange = { agreedPrice = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Agreed Price") },
                    prefix = { Text("₹") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
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
                    onSave(vendor.copy(
                        name = name.trim(),
                        category = category,
                        contactPerson = contactPerson.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        website = website.trim(),
                        address = address.trim(),
                        quotedPrice = quotedPrice.toDoubleOrNull() ?: 0.0,
                        agreedPrice = agreedPrice.toDoubleOrNull() ?: 0.0,
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

@Composable
private fun StatusChip(status: VendorStatus) {
    val color = when (status) {
        VendorStatus.BOOKED, VendorStatus.DEPOSIT_PAID, VendorStatus.COMPLETED -> 
            MaterialTheme.colorScheme.tertiary
        VendorStatus.PROPOSAL_RECEIVED, VendorStatus.MEETING_SCHEDULED -> 
            MaterialTheme.colorScheme.secondary
        VendorStatus.CANCELLED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Text(
        text = status.name.replace("_", " "),
        style = MaterialTheme.typography.labelLarge,
        color = color
    )
}

@Composable
private fun PriceCard(
    label: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
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
private fun StatusButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
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

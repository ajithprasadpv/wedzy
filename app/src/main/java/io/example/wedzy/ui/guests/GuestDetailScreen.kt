package io.example.wedzy.ui.guests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.GuestRelation
import io.example.wedzy.data.model.GuestSide
import io.example.wedzy.data.model.RsvpStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestDetailScreen(
    guestId: Long,
    onNavigateBack: () -> Unit,
    viewModel: GuestDetailViewModel = hiltViewModel()
) {
    val guest by viewModel.guest.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(guestId) {
        viewModel.loadGuest(guestId)
    }
    
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guest Details", fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = { viewModel.deleteGuest() }) {
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
        guest?.let { currentGuest ->
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
                    ),
                    onClick = { showEditDialog = true }
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = currentGuest.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "${currentGuest.side.name} • ${currentGuest.relation.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = "RSVP Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RsvpButton(
                        label = "Confirmed",
                        isSelected = currentGuest.rsvpStatus == RsvpStatus.CONFIRMED,
                        onClick = { viewModel.updateRsvpStatus(RsvpStatus.CONFIRMED) },
                        modifier = Modifier.weight(1f)
                    )
                    RsvpButton(
                        label = "Pending",
                        isSelected = currentGuest.rsvpStatus == RsvpStatus.PENDING,
                        onClick = { viewModel.updateRsvpStatus(RsvpStatus.PENDING) },
                        modifier = Modifier.weight(1f)
                    )
                    RsvpButton(
                        label = "Declined",
                        isSelected = currentGuest.rsvpStatus == RsvpStatus.DECLINED,
                        onClick = { viewModel.updateRsvpStatus(RsvpStatus.DECLINED) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                if (currentGuest.email.isNotBlank()) {
                    DetailSection(title = "Email") {
                        Text(
                            text = currentGuest.email,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                if (currentGuest.phone.isNotBlank()) {
                    DetailSection(title = "Phone") {
                        Text(
                            text = currentGuest.phone,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                if (currentGuest.plusOneAllowed) {
                    DetailSection(title = "Plus One") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentGuest.plusOneConfirmed) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (currentGuest.plusOneConfirmed) 
                                    MaterialTheme.colorScheme.tertiary 
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (currentGuest.plusOneConfirmed) 
                                    currentGuest.plusOneName ?: "Confirmed" 
                                else "Allowed but not confirmed",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                
                if (currentGuest.dietaryRestrictions.isNotBlank()) {
                    DetailSection(title = "Dietary Restrictions") {
                        Text(
                            text = currentGuest.dietaryRestrictions,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                if (currentGuest.notes.isNotBlank()) {
                    DetailSection(title = "Notes") {
                        Text(
                            text = currentGuest.notes,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                OutlinedButton(
                    onClick = { viewModel.deleteGuest() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Text("Remove Guest", modifier = Modifier.padding(start = 8.dp))
                }
                
                if (showEditDialog) {
                    EditGuestDialog(
                        guest = currentGuest,
                        onDismiss = { showEditDialog = false },
                        onSave = { updatedGuest ->
                            viewModel.updateGuest(updatedGuest)
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
private fun EditGuestDialog(
    guest: Guest,
    onDismiss: () -> Unit,
    onSave: (Guest) -> Unit
) {
    var firstName by remember { mutableStateOf(guest.firstName) }
    var lastName by remember { mutableStateOf(guest.lastName) }
    var email by remember { mutableStateOf(guest.email) }
    var phone by remember { mutableStateOf(guest.phone) }
    var side by remember { mutableStateOf(guest.side) }
    var relation by remember { mutableStateOf(guest.relation) }
    var plusOneAllowed by remember { mutableStateOf(guest.plusOneAllowed) }
    var dietaryRestrictions by remember { mutableStateOf(guest.dietaryRestrictions) }
    var notes by remember { mutableStateOf(guest.notes) }
    var sideExpanded by remember { mutableStateOf(false) }
    var relationExpanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Guest") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
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
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                ExposedDropdownMenuBox(
                    expanded = sideExpanded,
                    onExpandedChange = { sideExpanded = it }
                ) {
                    OutlinedTextField(
                        value = side.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Side") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sideExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = sideExpanded,
                        onDismissRequest = { sideExpanded = false }
                    ) {
                        GuestSide.entries.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name) },
                                onClick = { side = s; sideExpanded = false }
                            )
                        }
                    }
                }
                
                ExposedDropdownMenuBox(
                    expanded = relationExpanded,
                    onExpandedChange = { relationExpanded = it }
                ) {
                    OutlinedTextField(
                        value = relation.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relation") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = relationExpanded,
                        onDismissRequest = { relationExpanded = false }
                    ) {
                        GuestRelation.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.name.replace("_", " ")) },
                                onClick = { relation = r; relationExpanded = false }
                            )
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = plusOneAllowed,
                        onCheckedChange = { plusOneAllowed = it }
                    )
                    Text("Plus One Allowed")
                }
                
                OutlinedTextField(
                    value = dietaryRestrictions,
                    onValueChange = { dietaryRestrictions = it },
                    label = { Text("Dietary Restrictions") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                    onSave(guest.copy(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        side = side,
                        relation = relation,
                        plusOneAllowed = plusOneAllowed,
                        dietaryRestrictions = dietaryRestrictions.trim(),
                        notes = notes.trim()
                    ))
                },
                enabled = firstName.isNotBlank()
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
private fun RsvpButton(
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
            Text(label)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(label)
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

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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import io.example.wedzy.data.model.GuestRelation
import io.example.wedzy.data.model.GuestSide

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGuestScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddGuestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var sideExpanded by remember { mutableStateOf(false) }
    var relationExpanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Guest", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = viewModel::updateFirstName,
                    label = { Text("First Name *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = viewModel::updateLastName,
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                placeholder = { Text("guest@example.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                label = { Text("Phone") },
                placeholder = { Text("+1 234 567 8900") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            ExposedDropdownMenuBox(
                expanded = sideExpanded,
                onExpandedChange = { sideExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.side.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Side") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sideExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                
                ExposedDropdownMenu(
                    expanded = sideExpanded,
                    onDismissRequest = { sideExpanded = false }
                ) {
                    GuestSide.entries.forEach { side ->
                        DropdownMenuItem(
                            text = { Text(side.name.replace("_", " ")) },
                            onClick = {
                                viewModel.updateSide(side)
                                sideExpanded = false
                            }
                        )
                    }
                }
            }
            
            ExposedDropdownMenuBox(
                expanded = relationExpanded,
                onExpandedChange = { relationExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.relation.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relation") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                
                ExposedDropdownMenu(
                    expanded = relationExpanded,
                    onDismissRequest = { relationExpanded = false }
                ) {
                    GuestRelation.entries.forEach { relation ->
                        DropdownMenuItem(
                            text = { Text(relation.name.replace("_", " ")) },
                            onClick = {
                                viewModel.updateRelation(relation)
                                relationExpanded = false
                            }
                        )
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.plusOneAllowed,
                    onCheckedChange = viewModel::updatePlusOneAllowed
                )
                Text(
                    text = "Allow Plus One (+1)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            OutlinedTextField(
                value = uiState.dietaryRestrictions,
                onValueChange = viewModel::updateDietaryRestrictions,
                label = { Text("Dietary Restrictions") },
                placeholder = { Text("e.g., Vegetarian, Gluten-free") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes") },
                placeholder = { Text("Any special notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = viewModel::saveGuest,
                enabled = viewModel.canSave() && !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Add Guest")
                }
            }
        }
    }
}

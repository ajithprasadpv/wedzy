package io.example.wedzy.ui.guests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.RsvpStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestsScreen(
    onNavigateToAddGuest: () -> Unit,
    onNavigateToGuestDetail: (Long) -> Unit,
    onNavigateToAddFromContacts: () -> Unit = {},
    viewModel: GuestsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guest List", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = { showAddMenu = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Guest")
                }
                
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Add manually") },
                        onClick = {
                            showAddMenu = false
                            onNavigateToAddGuest()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add from contacts") },
                        onClick = {
                            showAddMenu = false
                            onNavigateToAddFromContacts()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Contacts, contentDescription = null)
                        }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GuestSummaryCard(
                    totalGuests = uiState.totalGuests,
                    confirmedGuests = uiState.confirmedGuests,
                    pendingGuests = uiState.pendingGuests,
                    declinedGuests = uiState.declinedGuests
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(RsvpFilter.entries) { filter ->
                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = { viewModel.setFilter(filter) },
                            label = { Text(filter.name) }
                        )
                    }
                }
            }
            
            if (uiState.filteredGuests.isEmpty()) {
                item {
                    EmptyGuestsState(filter = uiState.selectedFilter)
                }
            } else {
                items(uiState.filteredGuests, key = { it.id }) { guest ->
                    GuestCard(
                        guest = guest,
                        onClick = { onNavigateToGuestDetail(guest.id) },
                        onUpdateStatus = { status -> viewModel.updateRsvpStatus(guest, status) },
                        onDelete = { viewModel.deleteGuest(guest) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GuestSummaryCard(
    totalGuests: Int,
    confirmedGuests: Int,
    pendingGuests: Int,
    declinedGuests: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guest Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GuestStatItem(
                    count = totalGuests,
                    label = "Total"
                )
                GuestStatItem(
                    count = confirmedGuests,
                    label = "Confirmed",
                    color = MaterialTheme.colorScheme.tertiary
                )
                GuestStatItem(
                    count = pendingGuests,
                    label = "Pending",
                    color = MaterialTheme.colorScheme.secondary
                )
                GuestStatItem(
                    count = declinedGuests,
                    label = "Declined",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun GuestStatItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestCard(
    guest: Guest,
    onClick: () -> Unit,
    onUpdateStatus: (RsvpStatus) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guest.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RsvpStatusBadge(status = guest.rsvpStatus)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = guest.side.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (guest.plusOneAllowed) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+1",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            
            Row {
                if (guest.rsvpStatus != RsvpStatus.CONFIRMED) {
                    IconButton(
                        onClick = { onUpdateStatus(RsvpStatus.CONFIRMED) }
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Confirm",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                if (guest.rsvpStatus != RsvpStatus.DECLINED) {
                    IconButton(
                        onClick = { onUpdateStatus(RsvpStatus.DECLINED) }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Decline",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RsvpStatusBadge(status: RsvpStatus) {
    val (icon, color) = when (status) {
        RsvpStatus.CONFIRMED -> Icons.Default.Check to MaterialTheme.colorScheme.tertiary
        RsvpStatus.DECLINED -> Icons.Default.Close to MaterialTheme.colorScheme.error
        RsvpStatus.PENDING -> Icons.Default.QuestionMark to MaterialTheme.colorScheme.secondary
        RsvpStatus.INVITED -> Icons.Default.Person to MaterialTheme.colorScheme.primary
        RsvpStatus.MAYBE -> Icons.Default.QuestionMark to MaterialTheme.colorScheme.secondary
    }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun EmptyGuestsState(filter: RsvpFilter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (filter) {
                    RsvpFilter.ALL -> "No guests yet"
                    else -> "No ${filter.name.lowercase()} guests"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap + to add your first guest",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

package io.example.wedzy.ui.vendors

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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.example.wedzy.data.model.Vendor
import io.example.wedzy.data.model.VendorCategory
import io.example.wedzy.data.model.VendorStatus
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreen(
    onNavigateToAddVendor: () -> Unit,
    onNavigateToVendorDetail: (Long) -> Unit,
    onNavigateToContactPicker: () -> Unit = {},
    viewModel: VendorsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendors", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Vendor")
            }
        }
    ) { padding ->
        if (showAddDialog) {
            AddVendorOptionsDialog(
                onDismiss = { showAddDialog = false },
                onAddManually = {
                    showAddDialog = false
                    onNavigateToAddVendor()
                },
                onAddFromContacts = {
                    showAddDialog = false
                    onNavigateToContactPicker()
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                VendorSummaryCard(
                    totalAmount = uiState.totalAmount,
                    paidAmount = uiState.paidAmount,
                    pendingAmount = uiState.pendingAmount,
                    reservedCount = uiState.bookedVendors,
                    pendingCount = uiState.pendingVendors,
                    rejectedCount = uiState.rejectedVendors
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.setCategory(null) },
                            label = { Text("All") }
                        )
                    }
                    items(VendorCategory.entries.take(8)) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category.name.replace("_", " ")) }
                        )
                    }
                }
            }
            
            if (uiState.filteredVendors.isEmpty()) {
                item {
                    EmptyVendorsState()
                }
            } else {
                items(uiState.filteredVendors, key = { it.id }) { vendor ->
                    VendorCard(
                        vendor = vendor,
                        onClick = { onNavigateToVendorDetail(vendor.id) },
                        onEdit = { onNavigateToVendorDetail(vendor.id) },
                        onDelete = { viewModel.deleteVendor(vendor) }
                    )
                }
            }
        }
    }
}

@Composable
private fun VendorSummaryCard(
    totalAmount: Double,
    paidAmount: Double,
    pendingAmount: Double,
    reservedCount: Int,
    pendingCount: Int,
    rejectedCount: Int
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    fun formatAmount(amount: Double): String = "₹${NumberFormat.getNumberInstance().format(amount.toLong())}"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "SUMMARY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Amount: ${formatAmount(totalAmount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Paid: ${formatAmount(paidAmount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pending: ${formatAmount(pendingAmount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                VendorStatusIndicator(
                    reservedCount = reservedCount,
                    pendingCount = pendingCount,
                    rejectedCount = rejectedCount
                )
            }
        }
    }
}

@Composable
private fun VendorStatusIndicator(
    reservedCount: Int,
    pendingCount: Int,
    rejectedCount: Int
) {
    val total = reservedCount + pendingCount + rejectedCount
    
    Column(
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            val greenColor = Color(0xFF4CAF50)
            val orangeColor = Color(0xFFFF9800)
            val redColor = Color(0xFFF44336)
            val trackColor = MaterialTheme.colorScheme.surfaceVariant
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 6.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)
                
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )
                
                if (total > 0) {
                    val greenAngle = (reservedCount.toFloat() / total) * 360f
                    val orangeAngle = (pendingCount.toFloat() / total) * 360f
                    val redAngle = (rejectedCount.toFloat() / total) * 360f
                    
                    var startAngle = -90f
                    
                    if (greenAngle > 0) {
                        drawArc(
                            color = greenColor,
                            startAngle = startAngle,
                            sweepAngle = greenAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += greenAngle
                    }
                    
                    if (orangeAngle > 0) {
                        drawArc(
                            color = orangeColor,
                            startAngle = startAngle,
                            sweepAngle = orangeAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += orangeAngle
                    }
                    
                    if (redAngle > 0) {
                        drawArc(
                            color = redColor,
                            startAngle = startAngle,
                            sweepAngle = redAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                }
            }
            
            Text(
                text = "$total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        VendorStatusLegendItem(
            color = Color(0xFF4CAF50),
            label = "$reservedCount reserved"
        )
        VendorStatusLegendItem(
            color = Color(0xFFFF9800),
            label = "$pendingCount pending"
        )
        VendorStatusLegendItem(
            color = Color(0xFFF44336),
            label = "$rejectedCount rejected"
        )
    }
}

@Composable
private fun VendorStatusLegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendorCard(
    vendor: Vendor,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vendor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (vendor.contactPerson.isNotBlank() && vendor.contactPerson != vendor.name) {
                        Text(
                            text = vendor.contactPerson,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = vendor.category.name.replace("_", " "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    VendorStatusBadge(status = vendor.status)
                }
                
                if (vendor.quotedPrice > 0 || vendor.agreedPrice > 0) {
                    Text(
                        text = currencyFormatter.format(
                            vendor.agreedPrice.takeIf { it > 0 } ?: vendor.quotedPrice
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VendorStatusBadge(status: VendorStatus) {
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
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}

@Composable
private fun EmptyVendorsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No vendors yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap + to add your first vendor",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AddVendorOptionsDialog(
    onDismiss: () -> Unit,
    onAddManually: () -> Unit,
    onAddFromContacts: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column {
                TextButton(
                    onClick = onAddManually,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "Add manually",
                        modifier = Modifier.weight(1f)
                    )
                }
                TextButton(
                    onClick = onAddFromContacts,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Contacts,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "Add from contacts",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

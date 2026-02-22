package io.example.wedzy.ui.collaboration

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CollaborationContact(
    val id: String,
    val name: String,
    val phone: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborationContactPickerScreen(
    onNavigateBack: () -> Unit,
    onContactsSelected: (List<CollaborationContact>, String) -> Unit,
    inviteCode: String
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    val contacts = remember { mutableStateListOf<CollaborationContact>() }
    val selectedContacts = remember { mutableStateListOf<CollaborationContact>() }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            isLoading = true
        }
    }
    
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
    
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            contacts.clear()
            contacts.addAll(loadContacts(context.contentResolver))
            isLoading = false
        }
    }
    
    val filteredContacts = contacts.filter {
        searchQuery.isEmpty() || 
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.phone?.contains(searchQuery) == true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Team Members", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedContacts.isNotEmpty()) {
                        Button(
                            onClick = { 
                                sendWhatsAppInvites(context, selectedContacts, inviteCode)
                                onContactsSelected(selectedContacts.toList(), inviteCode)
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send (${selectedContacts.size})")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search contacts...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            
            if (!hasPermission) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Contacts permission required",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                            Text("Grant Permission")
                        }
                    }
                }
            } else if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No contacts found" else "No matching contacts",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { contact ->
                        CollaborationContactItem(
                            contact = contact,
                            isSelected = selectedContacts.contains(contact),
                            onToggle = {
                                if (selectedContacts.contains(contact)) {
                                    selectedContacts.remove(contact)
                                } else {
                                    selectedContacts.add(contact)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollaborationContactItem(
    contact: CollaborationContact,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = contact.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (!contact.phone.isNullOrEmpty()) {
                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun loadContacts(contentResolver: ContentResolver): List<CollaborationContact> {
    val contacts = mutableListOf<CollaborationContact>()
    
    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
    )
    
    val cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        projection,
        null,
        null,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
    )
    
    cursor?.use {
        val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
        val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val hasPhoneIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        
        while (it.moveToNext()) {
            val id = it.getString(idIndex) ?: continue
            val name = it.getString(nameIndex) ?: continue
            val hasPhone = it.getInt(hasPhoneIndex) > 0
            
            var phone: String? = null
            
            if (hasPhone) {
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )
                phoneCursor?.use { pc ->
                    if (pc.moveToFirst()) {
                        phone = pc.getString(0)
                    }
                }
            }
            
            if (!phone.isNullOrEmpty()) {
                contacts.add(CollaborationContact(id, name, phone))
            }
        }
    }
    
    return contacts
}

private fun sendWhatsAppInvites(
    context: Context,
    contacts: List<CollaborationContact>,
    inviteCode: String
) {
    val message = """
        Hi! 
        I have been using the Wedzy app and I need your help to plan my wedding: https://play.google.com/store/apps/details?id=app.wedzy.android 
        Install it and join to my wedding by entering this code: $inviteCode 
        See you in the app!
    """.trimIndent()
    
    contacts.forEach { contact ->
        contact.phone?.let { phoneNumber ->
            try {
                val cleanPhone = phoneNumber.replace("[^0-9+]".toRegex(), "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
                    setPackage("com.whatsapp")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // WhatsApp not installed or error - silently ignore
            }
        }
    }
}

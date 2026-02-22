package io.example.wedzy.ui.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChairAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Feedback
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import io.example.wedzy.R
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.model.Task
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToGuests: () -> Unit,
    onNavigateToVendors: () -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSeating: () -> Unit = {},
    onNavigateToInspiration: () -> Unit = {},
    onNavigateToMarketplace: () -> Unit = {},
    onNavigateToCollaboration: () -> Unit = {},
    onNavigateToAI: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val rateApp: () -> Unit = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
            context.startActivity(intent)
        }
    }
    
    val shareApp: () -> Unit = {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Try Wedzy - Wedding Planner App")
            putExtra(Intent.EXTRA_TEXT, "Hey! Check out Wedzy - the perfect app to plan your wedding! 💒💍\n\nDownload it here: https://play.google.com/store/apps/details?id=${context.packageName}")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerContent(
                    brideName = uiState.profile?.brideName ?: "",
                    groomName = uiState.profile?.groomName ?: "",
                    weddingDate = uiState.profile?.weddingDate ?: 0L,
                    isSignedIn = uiState.isSignedIn,
                    userName = uiState.currentUser?.displayName,
                    userEmail = uiState.currentUser?.email,
                    userPhotoUrl = uiState.currentUser?.photoUrl?.toString(),
                    onNavigateToHome = { scope.launch { drawerState.close() } },
                    onNavigateToTasks = { scope.launch { drawerState.close() }; onNavigateToTasks() },
                    onNavigateToBudget = { scope.launch { drawerState.close() }; onNavigateToBudget() },
                    onNavigateToGuests = { scope.launch { drawerState.close() }; onNavigateToGuests() },
                    onNavigateToVendors = { scope.launch { drawerState.close() }; onNavigateToVendors() },
                    onNavigateToCalendar = { scope.launch { drawerState.close() }; onNavigateToCalendar() },
                    onNavigateToCollaboration = { scope.launch { drawerState.close() }; onNavigateToCollaboration() },
                    onNavigateToMarketplace = { scope.launch { drawerState.close() }; onNavigateToMarketplace() },
                    onNavigateToInspiration = { scope.launch { drawerState.close() }; onNavigateToInspiration() },
                    onNavigateToSeating = { scope.launch { drawerState.close() }; onNavigateToSeating() },
                    onNavigateToAnalytics = { scope.launch { drawerState.close() }; onNavigateToAnalytics() },
                    onNavigateToAuth = { scope.launch { drawerState.close() }; onNavigateToAuth() },
                    onNavigateToAbout = { scope.launch { drawerState.close() }; onNavigateToAbout() },
                    onRateApp = { scope.launch { drawerState.close() }; rateApp() },
                    onShareApp = { scope.launch { drawerState.close() }; shareApp() },
                    onLogout = { scope.launch { drawerState.close() }; viewModel.logout(); onNavigateToAuth() },
                    currencySymbol = uiState.selectedCurrency.symbol
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (uiState.profile != null) 
                                    "${uiState.profile?.brideName} & ${uiState.profile?.groomName}" 
                                else "Wedzy",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.daysUntilWedding > 0) {
                                Text(
                                    text = "${uiState.daysUntilWedding} days to go!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                )
            }
        ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                var showEditDialog by remember { mutableStateOf(false) }
                
                val context = LocalContext.current
                val imagePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let { sourceUri ->
                        // Copy image to app's internal storage for persistence
                        try {
                            val inputStream = context.contentResolver.openInputStream(sourceUri)
                            inputStream?.let { stream ->
                                val heroImagesDir = File(context.filesDir, "hero_images")
                                heroImagesDir.mkdirs()
                                val destFile = File(heroImagesDir, "hero_bg_${System.currentTimeMillis()}.jpg")
                                FileOutputStream(destFile).use { output ->
                                    stream.copyTo(output)
                                }
                                stream.close()
                                viewModel.updateHeroBackgroundImage(destFile.absolutePath)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                CountdownCard(
                    daysUntilWedding = uiState.daysUntilWedding,
                    weddingDate = uiState.profile?.weddingDate ?: 0L,
                    brideName = uiState.profile?.brideName ?: "",
                    groomName = uiState.profile?.groomName ?: "",
                    backgroundImageUri = uiState.heroBackgroundImage,
                    onEditClick = { showEditDialog = true },
                    onAddPhotoClick = { imagePickerLauncher.launch("image/*") },
                    onRemovePhotoClick = { viewModel.updateHeroBackgroundImage(null) }
                )
                
                if (showEditDialog) {
                    EditWeddingDetailsDialog(
                        currentBrideName = uiState.profile?.brideName ?: "",
                        currentGroomName = uiState.profile?.groomName ?: "",
                        currentWeddingDate = uiState.profile?.weddingDate ?: System.currentTimeMillis(),
                        currentBudget = uiState.profile?.totalBudget ?: 0.0,
                        currentCurrency = uiState.selectedCurrency,
                        onDismiss = { showEditDialog = false },
                        onSave = { brideName, groomName, weddingDate, budget, currency ->
                            viewModel.updateWeddingDetails(brideName, groomName, weddingDate, budget, currency)
                            showEditDialog = false
                        }
                    )
                }
            }
            
            item {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        title = "Gallery",
                        value = "",
                        subtitle = "photos & ideas",
                        icon = Icons.Default.PhotoLibrary,
                        progress = 0f,
                        onClick = onNavigateToInspiration,
                        modifier = Modifier.weight(1f)
                    )
                    
                    QuickStatCard(
                        title = "Budget",
                        value = formatCurrency(uiState.spentBudget, uiState.selectedCurrency),
                        subtitle = "of ${formatCurrency(uiState.totalBudget, uiState.selectedCurrency)}",
                        icon = Icons.Default.AttachMoney,
                        progress = if (uiState.totalBudget > 0) 
                            (uiState.spentBudget / uiState.totalBudget).toFloat().coerceIn(0f, 1f) 
                        else 0f,
                        onClick = onNavigateToBudget,
                        modifier = Modifier.weight(1f),
                        currencySymbol = uiState.selectedCurrency.symbol
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        title = "Guests",
                        value = "${uiState.confirmedGuests}/${uiState.totalGuests}",
                        subtitle = "confirmed",
                        icon = Icons.Default.People,
                        progress = if (uiState.totalGuests > 0) 
                            uiState.confirmedGuests.toFloat() / uiState.totalGuests 
                        else 0f,
                        onClick = onNavigateToGuests,
                        modifier = Modifier.weight(1f)
                    )
                    
                    QuickStatCard(
                        title = "Events",
                        value = "${uiState.upcomingEvents}",
                        subtitle = "upcoming",
                        icon = Icons.Default.CalendarMonth,
                        progress = 0f,
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "More Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard(
                        title = "Analytics",
                        icon = Icons.Default.Insights,
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        title = "Vendors",
                        icon = Icons.Default.Store,
                        onClick = onNavigateToVendors,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        title = "Seating",
                        icon = Icons.Default.ChairAlt,
                        onClick = onNavigateToSeating,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard(
                        title = "Checklist",
                        icon = Icons.Default.Checklist,
                        onClick = onNavigateToTasks,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        title = "Marketplace",
                        icon = Icons.Default.Storefront,
                        onClick = onNavigateToMarketplace,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        title = "Team",
                        icon = Icons.Default.PeopleOutline,
                        onClick = onNavigateToCollaboration,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Card(
                    onClick = onNavigateToAI,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "AI Recommendations",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Get smart suggestions for your wedding",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            if (uiState.upcomingTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upcoming Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(uiState.upcomingTasks) { task ->
                    TaskItem(
                        task = task,
                        onComplete = { viewModel.completeTask(task) }
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    brideName: String,
    groomName: String,
    weddingDate: Long,
    onNavigateToHome: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToGuests: () -> Unit,
    onNavigateToVendors: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToCollaboration: () -> Unit,
    onNavigateToMarketplace: () -> Unit,
    onNavigateToInspiration: () -> Unit,
    onNavigateToSeating: () -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onRateApp: () -> Unit = {},
    onShareApp: () -> Unit = {},
    onLogout: () -> Unit = {},
    isSignedIn: Boolean = false,
    userName: String? = null,
    userEmail: String? = null,
    userPhotoUrl: String? = null,
    currencySymbol: String = "$"
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            if (isSignedIn && (userName != null || userEmail != null)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    if (userPhotoUrl != null) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (userName?.firstOrNull() ?: userEmail?.firstOrNull() ?: 'U').uppercaseChar().toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userName ?: "User",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (userEmail != null) {
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (brideName.isNotEmpty()) "$brideName & $groomName" else "Your Wedding",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (weddingDate > 0) {
                        Text(
                            text = dateFormatter.format(Date(weddingDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Home") },
                selected = true,
                onClick = onNavigateToHome
            )
            NavigationDrawerItem(
                icon = { 
                    Text(
                        text = currencySymbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                label = { Text("Budget") },
                selected = false,
                onClick = onNavigateToBudget
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Insights, contentDescription = null) },
                label = { Text("Analytics") },
                selected = false,
                onClick = onNavigateToAnalytics
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Checklist, contentDescription = null) },
                label = { Text("Checklist") },
                selected = false,
                onClick = onNavigateToTasks
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                label = { Text("Events") },
                selected = false,
                onClick = onNavigateToCalendar
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.People, contentDescription = null) },
                label = { Text("Guests") },
                selected = false,
                onClick = onNavigateToGuests
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Store, contentDescription = null) },
                label = { Text("Vendors") },
                selected = false,
                onClick = onNavigateToVendors
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                label = { Text("Marketplace") },
                selected = false,
                onClick = onNavigateToMarketplace
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Group, contentDescription = null) },
                label = { Text("Collaborators") },
                selected = false,
                onClick = onNavigateToCollaboration
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.ChairAlt, contentDescription = null) },
                label = { Text("Seating") },
                selected = false,
                onClick = onNavigateToSeating
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Image, contentDescription = null) },
                label = { Text("Inspiration") },
                selected = false,
                onClick = onNavigateToInspiration
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text("Settings") },
                selected = false,
                onClick = { }
            )
            
            if (!isSignedIn) {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Sign In") },
                    selected = false,
                    onClick = onNavigateToAuth
                )
            } else {
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = onLogout
                )
            }
        }
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wedzy_logo),
                    contentDescription = "Wedzy",
                    modifier = Modifier.height(32.dp),
                    contentScale = ContentScale.FillHeight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Version 1.1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            Icons.Default.Feedback,
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onRateApp) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rate this app",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onShareApp) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share app",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CountdownCard(
    daysUntilWedding: Int,
    weddingDate: Long,
    brideName: String,
    groomName: String,
    backgroundImageUri: String?,
    onEditClick: () -> Unit,
    onAddPhotoClick: () -> Unit,
    onRemovePhotoClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    val hasBackgroundImage = backgroundImageUri != null
    
    // Live countdown state
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(weddingDate)) }
    
    // Update countdown every second
    LaunchedEffect(weddingDate) {
        while (true) {
            timeRemaining = calculateTimeRemaining(weddingDate)
            delay(1000L)
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasBackgroundImage) Color.Transparent else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (hasBackgroundImage) {
                // Check if it's a file path or URI
                val imageModel = if (backgroundImageUri.startsWith("/")) {
                    File(backgroundImageUri)
                } else {
                    Uri.parse(backgroundImageUri)
                }
                Image(
                    painter = rememberAsyncImagePainter(model = imageModel),
                    contentDescription = "Couple photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (hasBackgroundImage) Modifier.height(300.dp) else Modifier
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onAddPhotoClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Add couple photo",
                            modifier = Modifier.size(20.dp),
                            tint = if (hasBackgroundImage) Color.White.copy(alpha = 0.9f)
                                   else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    
                    Row {
                        if (hasBackgroundImage) {
                            IconButton(
                                onClick = onRemovePhotoClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit wedding details",
                                modifier = Modifier.size(20.dp),
                                tint = if (hasBackgroundImage) Color.White.copy(alpha = 0.9f)
                                       else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (!hasBackgroundImage) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                val textColor = if (hasBackgroundImage) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                
                if (brideName.isNotBlank() && groomName.isNotBlank()) {
                    Text(
                        text = "$brideName & $groomName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (timeRemaining.totalMillis > 0) {
                    // Show days, hours, minutes, seconds countdown
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CountdownUnit(value = timeRemaining.days, label = "Days", textColor = textColor)
                        CountdownUnit(value = timeRemaining.hours, label = "Hrs", textColor = textColor)
                        CountdownUnit(value = timeRemaining.minutes, label = "Min", textColor = textColor)
                        CountdownUnit(value = timeRemaining.seconds, label = "Sec", textColor = textColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "until your wedding",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.9f)
                    )
                } else if (weddingDate > 0 && timeRemaining.totalMillis <= 0 && timeRemaining.totalMillis > -86400000) {
                    Text(
                        text = "Today's the day!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                } else if (weddingDate <= 0) {
                    Text(
                        text = "Set your wedding date",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "Tap the edit icon to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "Congratulations!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                
                if (weddingDate > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dateFormatter.format(Date(weddingDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
                
                if (hasBackgroundImage) {
                    Spacer(modifier = Modifier.weight(0.3f))
                }
            }
        }
    }
}

// Data class for countdown time
private data class TimeRemaining(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalMillis: Long
)

// Calculate time remaining until wedding
private fun calculateTimeRemaining(weddingDate: Long): TimeRemaining {
    if (weddingDate <= 0) {
        return TimeRemaining(0, 0, 0, 0, -1)
    }
    
    val now = System.currentTimeMillis()
    val diff = weddingDate - now
    
    if (diff <= 0) {
        return TimeRemaining(0, 0, 0, 0, diff)
    }
    
    val seconds = (diff / 1000) % 60
    val minutes = (diff / (1000 * 60)) % 60
    val hours = (diff / (1000 * 60 * 60)) % 24
    val days = (diff / (1000 * 60 * 60 * 24))
    
    return TimeRemaining(
        days = days.toInt(),
        hours = hours.toInt(),
        minutes = minutes.toInt(),
        seconds = seconds.toInt(),
        totalMillis = diff
    )
}

@Composable
private fun CountdownUnit(
    value: Int,
    label: String,
    textColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditWeddingDetailsDialog(
    currentBrideName: String,
    currentGroomName: String,
    currentWeddingDate: Long,
    currentBudget: Double,
    currentCurrency: Currency,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Double, Currency) -> Unit
) {
    var brideName by remember { mutableStateOf(currentBrideName) }
    var groomName by remember { mutableStateOf(currentGroomName) }
    var weddingDate by remember { mutableStateOf(currentWeddingDate) }
    var budgetText by remember { mutableStateOf(if (currentBudget > 0) currentBudget.toLong().toString() else "") }
    var selectedCurrency by remember { mutableStateOf(currentCurrency) }
    var showDatePicker by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }
    
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wedding Details", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = brideName,
                    onValueChange = { brideName = it },
                    label = { Text("Bride's Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = groomName,
                    onValueChange = { groomName = it },
                    label = { Text("Groom's Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = if (weddingDate > 0) dateFormatter.format(Date(weddingDate)) else "",
                    onValueChange = {},
                    label = { Text("Wedding Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date")
                        }
                    }
                )
                
                ExposedDropdownMenuBox(
                    expanded = currencyExpanded,
                    onExpandedChange = { currencyExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedCurrency.symbol} - ${selectedCurrency.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = currencyExpanded,
                        onDismissRequest = { currencyExpanded = false }
                    ) {
                        Currency.entries.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text("${currency.symbol} - ${currency.displayName}") },
                                onClick = {
                                    selectedCurrency = currency
                                    currencyExpanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it.filter { c -> c.isDigit() } },
                    label = { Text("Total Budget") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text(selectedCurrency.symbol) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val budget = budgetText.toDoubleOrNull() ?: 0.0
                    onSave(brideName.trim(), groomName.trim(), weddingDate, budget, selectedCurrency)
                },
                enabled = brideName.isNotBlank() && groomName.isNotBlank()
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
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (weddingDate > 0) weddingDate else System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { weddingDate = it }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (currencySymbol != null) {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (progress > 0f) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onComplete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = false,
                onCheckedChange = { onComplete() }
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.dueDate != null) {
                    Text(
                        text = "Due: ${dateFormatter.format(Date(task.dueDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double, currency: Currency): String {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 0
    return "${currency.symbol}${formatter.format(amount)}"
}

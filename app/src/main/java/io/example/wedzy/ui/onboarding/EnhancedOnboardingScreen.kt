package io.example.wedzy.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.example.wedzy.R
import io.example.wedzy.data.model.Currency
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun EnhancedOnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    viewModel: EnhancedOnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isCompleting) {
        if (uiState.isCompleting && uiState.error == null) {
            onComplete()
        }
    }

    Scaffold(
        topBar = {
            if (uiState.currentStep != OnboardingStep.WELCOME) {
                TopAppBar(
                    title = { 
                        LinearProgressIndicator(
                            progress = uiState.progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        if (uiState.currentStepIndex > 0) {
                            IconButton(onClick = { viewModel.previousStep() }) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                    },
                    actions = {
                        TextButton(onClick = { viewModel.skipOnboarding(); onSkip() }) {
                            Text("Skip")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() with
                            slideOutHorizontally { -it } + fadeOut()
                }
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> WelcomeStep(
                        onGetStarted = { viewModel.nextStep() },
                        onSkip = { viewModel.skipOnboarding(); onSkip() }
                    )
                    OnboardingStep.FEATURES -> FeaturesStep(
                        onNext = { viewModel.nextStep() }
                    )
                    OnboardingStep.PROFILE -> ProfileStep(
                        brideName = uiState.brideName,
                        groomName = uiState.groomName,
                        onBrideNameChange = viewModel::updateBrideName,
                        onGroomNameChange = viewModel::updateGroomName,
                        onNext = { viewModel.nextStep() },
                        canProceed = uiState.canProceed()
                    )
                    OnboardingStep.WEDDING_DATE -> WeddingDateStep(
                        selectedDate = uiState.weddingDate,
                        onDateSelected = viewModel::updateWeddingDate,
                        onNext = { viewModel.nextStep() },
                        canProceed = uiState.canProceed()
                    )
                    OnboardingStep.BUDGET -> BudgetStep(
                        budget = uiState.totalBudget,
                        currency = uiState.selectedCurrency,
                        onBudgetChange = viewModel::updateBudget,
                        onCurrencyChange = viewModel::updateCurrency,
                        onNext = { viewModel.nextStep() },
                        canProceed = uiState.canProceed()
                    )
                    OnboardingStep.PERMISSIONS -> PermissionsStep(
                        wantsNotifications = uiState.wantsNotifications,
                        onNotificationPreferenceChange = viewModel::updateNotificationPreference,
                        onNext = { viewModel.nextStep() }
                    )
                    OnboardingStep.SAMPLE_DATA -> SampleDataStep(
                        wantsSampleData = uiState.wantsSampleData,
                        onSampleDataPreferenceChange = viewModel::updateSampleDataPreference,
                        onComplete = { viewModel.completeOnboarding() },
                        isCompleting = uiState.isCompleting
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Welcome to Wedzy",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your intelligent wedding planning assistant",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Get Started", style = MaterialTheme.typography.titleMedium)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onSkip) {
            Text("Skip for now")
        }
    }
}

@Composable
private fun FeaturesStep(
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "What Wedzy Can Do",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        featureHighlights.forEach { feature ->
            FeatureCard(feature)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun FeatureCard(feature: FeatureHighlight) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (feature.iconName) {
                    "checklist" -> Icons.Default.CheckCircle
                    "budget" -> Icons.Default.AccountBalance
                    "guests" -> Icons.Default.People
                    "vendors" -> Icons.Default.Business
                    "calendar" -> Icons.Default.CalendarToday
                    else -> Icons.Default.Star
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileStep(
    brideName: String,
    groomName: String,
    onBrideNameChange: (String) -> Unit,
    onGroomNameChange: (String) -> Unit,
    onNext: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Tell us about yourselves",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = brideName,
            onValueChange = onBrideNameChange,
            label = { Text("Bride's Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = groomName,
            onValueChange = onGroomNameChange,
            label = { Text("Groom's Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeddingDateStep(
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onNext: () -> Unit,
    canProceed: Boolean
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
    )
    
    LaunchedEffect(datePickerState.selectedDateMillis) {
        onDateSelected(datePickerState.selectedDateMillis)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "When's the big day?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "We'll use this to create your personalized timeline",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        DatePicker(
            state = datePickerState,
            modifier = Modifier.weight(1f)
        )
        
        Button(
            onClick = onNext,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetStep(
    budget: String,
    currency: Currency,
    onBudgetChange: (String) -> Unit,
    onCurrencyChange: (Currency) -> Unit,
    onNext: () -> Unit,
    canProceed: Boolean
) {
    var currencyExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Set your budget",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Don't worry, you can adjust this later",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ExposedDropdownMenuBox(
            expanded = currencyExpanded,
            onExpandedChange = { currencyExpanded = it }
        ) {
            OutlinedTextField(
                value = "${currency.code} (${currency.symbol})",
                onValueChange = {},
                readOnly = true,
                label = { Text("Currency") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = currencyExpanded,
                onDismissRequest = { currencyExpanded = false }
            ) {
                Currency.values().forEach { curr ->
                    DropdownMenuItem(
                        text = { Text("${curr.code} (${curr.symbol}) - ${curr.displayName}") },
                        onClick = {
                            onCurrencyChange(curr)
                            currencyExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = budget,
            onValueChange = onBudgetChange,
            label = { Text("Total Budget") },
            leadingIcon = { Text(currency.symbol) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (budget.isNotBlank() && budget.toDoubleOrNull() != null) {
                    Text("We'll help you allocate this across categories")
                }
            }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun PermissionsStep(
    wantsNotifications: Boolean,
    onNotificationPreferenceChange: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Stay on track",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get reminders for upcoming tasks and important deadlines",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = wantsNotifications,
                    onCheckedChange = onNotificationPreferenceChange
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun SampleDataStep(
    wantsSampleData: Boolean,
    onSampleDataPreferenceChange: (Boolean) -> Unit,
    onComplete: () -> Unit,
    isCompleting: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Almost done!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DataUsage,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Add Sample Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "See how Wedzy works with example budget items, guests, and vendors",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = wantsSampleData,
                    onCheckedChange = onSampleDataPreferenceChange
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onComplete,
            enabled = !isCompleting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isCompleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Start Planning!")
            }
        }
    }
}

package io.example.wedzy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.example.wedzy.ui.ai.AIRecommendationsScreen
import io.example.wedzy.ui.analytics.AnalyticsScreen
import io.example.wedzy.ui.budget.AddBudgetItemScreen
import io.example.wedzy.ui.budget.BudgetDetailScreen
import io.example.wedzy.ui.budget.BudgetScreen
import io.example.wedzy.ui.calendar.AddEventScreen
import io.example.wedzy.ui.calendar.CalendarScreen
import io.example.wedzy.ui.calendar.EventDetailScreen
import io.example.wedzy.ui.about.AboutScreen
import io.example.wedzy.ui.auth.AuthScreen
import io.example.wedzy.ui.auth.JoinWeddingScreen
import io.example.wedzy.ui.collaboration.CollaborationContactPickerScreen
import io.example.wedzy.ui.collaboration.CollaborationScreen
import io.example.wedzy.ui.collaboration.CollaborationViewModel
import io.example.wedzy.ui.guests.AddGuestScreen
import io.example.wedzy.ui.guests.ContactPickerScreen
import io.example.wedzy.ui.guests.GuestDetailScreen
import io.example.wedzy.ui.guests.GuestsScreen
import io.example.wedzy.ui.guests.GuestsViewModel
import io.example.wedzy.ui.home.HomeScreen
import io.example.wedzy.ui.inspiration.InspirationScreen
import io.example.wedzy.ui.marketplace.MarketplaceScreen
import io.example.wedzy.ui.marketplace.MarketplaceVendorDetailScreen
import io.example.wedzy.ui.marketplace.VendorCategoriesScreen
import io.example.wedzy.ui.onboarding.OnboardingScreen
import io.example.wedzy.ui.seating.SeatingScreen
import io.example.wedzy.ui.splash.SplashScreen
import io.example.wedzy.ui.tasks.AddTaskScreen
import io.example.wedzy.ui.tasks.TaskDetailScreen
import io.example.wedzy.ui.tasks.TasksScreen
import io.example.wedzy.ui.vendors.AddVendorScreen
import io.example.wedzy.ui.vendors.VendorDetailScreen
import io.example.wedzy.ui.vendors.VendorsScreen
import io.example.wedzy.ui.vendors.VendorsViewModel

@Composable
fun WedzyNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) },
                onNavigateToGuests = { navController.navigate(Screen.Guests.route) },
                onNavigateToVendors = { navController.navigate(Screen.Vendors.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToSeating = { navController.navigate(Screen.Seating.route) },
                onNavigateToInspiration = { navController.navigate(Screen.Inspiration.route) },
                onNavigateToMarketplace = { navController.navigate(Screen.VendorCategories.route) },
                onNavigateToCollaboration = { navController.navigate(Screen.Collaboration.route) },
                onNavigateToAI = { navController.navigate(Screen.AIRecommendations.route) },
                onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }
        
        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }
        
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Budget.route) {
            BudgetScreen(
                onNavigateToAddItem = { navController.navigate(Screen.AddBudgetItem.route) },
                onNavigateToItemDetail = { itemId ->
                    navController.navigate(Screen.BudgetDetail.createRoute(itemId))
                }
            )
        }
        
        composable(Screen.AddBudgetItem.route) {
            AddBudgetItemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.BudgetDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
            BudgetDetailScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Guests.route) {
            GuestsScreen(
                onNavigateToAddGuest = { navController.navigate(Screen.AddGuest.route) },
                onNavigateToGuestDetail = { guestId ->
                    navController.navigate(Screen.GuestDetail.createRoute(guestId))
                },
                onNavigateToAddFromContacts = { navController.navigate(Screen.ContactPicker.route)
                }
            )
        }
        
        composable(Screen.AddGuest.route) {
            AddGuestScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ContactPicker.route) {
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Screen.Guests.route)
            }
            val guestsViewModel: GuestsViewModel = hiltViewModel(parentEntry)
            
            ContactPickerScreen(
                onNavigateBack = { navController.popBackStack() },
                onContactsSelected = { contacts ->
                    val contactPairs = contacts.map { contact ->
                        contact.name to contact.phone
                    }
                    guestsViewModel.addGuestsFromContacts(contactPairs)
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.GuestDetail.route,
            arguments = listOf(navArgument("guestId") { type = NavType.LongType })
        ) { backStackEntry ->
            val guestId = backStackEntry.arguments?.getLong("guestId") ?: 0L
            GuestDetailScreen(
                guestId = guestId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Vendors.route) {
            val vendorsViewModel: VendorsViewModel = hiltViewModel()
            VendorsScreen(
                onNavigateToAddVendor = { 
                    val selectedCategory = vendorsViewModel.uiState.value.selectedCategory?.name ?: ""
                    navController.navigate(Screen.AddVendor.createRoute(category = selectedCategory))
                },
                onNavigateToVendorDetail = { vendorId ->
                    navController.navigate(Screen.VendorDetail.createRoute(vendorId))
                },
                onNavigateToContactPicker = { 
                    val selectedCategory = vendorsViewModel.uiState.value.selectedCategory?.name ?: ""
                    navController.navigate(Screen.VendorContactPicker.createRoute(category = selectedCategory))
                },
                viewModel = vendorsViewModel
            )
        }
        
        composable(
            route = Screen.AddVendor.route,
            arguments = listOf(
                navArgument("name") { 
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("phone") { 
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("email") { 
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("category") { 
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""
            
            AddVendorScreen(
                onNavigateBack = { navController.popBackStack() },
                initialName = name,
                initialPhone = phone,
                initialEmail = email,
                initialCategory = category
            )
        }
        
        composable(
            route = Screen.VendorContactPicker.route,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            ContactPickerScreen(
                onNavigateBack = { navController.popBackStack() },
                onContactsSelected = { contacts ->
                    if (contacts.isNotEmpty()) {
                        val contact = contacts.first()
                        navController.navigate(
                            Screen.AddVendor.createRoute(
                                name = contact.name,
                                phone = contact.phone ?: "",
                                email = contact.email ?: "",
                                category = category
                            )
                        ) {
                            popUpTo(Screen.Vendors.route)
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
        
        composable(
            route = Screen.VendorDetail.route,
            arguments = listOf(navArgument("vendorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getLong("vendorId") ?: 0L
            VendorDetailScreen(
                vendorId = vendorId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Phase 2 & 3 Screens
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddEvent = { selectedDate ->
                    navController.navigate(Screen.AddEvent.createRoute(selectedDate))
                },
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                }
            )
        }
        
        composable(
            route = Screen.AddEvent.route,
            arguments = listOf(navArgument("selectedDate") { type = NavType.LongType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getLong("selectedDate") ?: System.currentTimeMillis()
            AddEventScreen(
                onNavigateBack = { navController.popBackStack() },
                initialDate = selectedDate
            )
        }
        
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Seating.route) {
            SeatingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Inspiration.route) {
            InspirationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.VendorCategories.route) {
            VendorCategoriesScreen(
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { category ->
                    navController.navigate(Screen.Marketplace.createRoute(category))
                }
            )
        }
        
        composable(
            route = Screen.Marketplace.route,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            MarketplaceScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVendorDetail = { vendorId ->
                    navController.navigate(Screen.MarketplaceVendorDetail.createRoute(vendorId))
                },
                initialCategory = if (category.isNotEmpty()) category else null
            )
        }
        
        composable(
            route = Screen.MarketplaceVendorDetail.route,
            arguments = listOf(
                navArgument("vendorId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getLong("vendorId") ?: 0L
            MarketplaceVendorDetailScreen(
                vendorId = vendorId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Collaboration.route) {
            CollaborationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToContactPicker = { navController.navigate(Screen.CollaborationContactPicker.route) }
            )
        }
        
        composable(Screen.CollaborationContactPicker.route) {
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Screen.Collaboration.route)
            }
            val collaborationViewModel: CollaborationViewModel = hiltViewModel(parentEntry)
            
            var inviteCode by remember { mutableStateOf("") }
            
            LaunchedEffect(Unit) {
                inviteCode = collaborationViewModel.getCurrentWeddingInviteCode()
            }
            
            CollaborationContactPickerScreen(
                onNavigateBack = { navController.popBackStack() },
                onContactsSelected = { contacts, code ->
                    val contactPairs = contacts.map { contact ->
                        contact.name to contact.phone
                    }
                    collaborationViewModel.addCollaboratorsFromContacts(contactPairs)
                    navController.popBackStack()
                },
                inviteCode = inviteCode
            )
        }
        
        composable(Screen.AIRecommendations.route) {
            AIRecommendationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.JoinWedding.route) {
            JoinWeddingScreen(
                onNavigateBack = { navController.popBackStack() },
                onJoinSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

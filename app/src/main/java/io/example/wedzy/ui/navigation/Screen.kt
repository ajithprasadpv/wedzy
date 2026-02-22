package io.example.wedzy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Store
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Main : Screen("main")
    
    data object Home : Screen("home")
    data object Tasks : Screen("tasks")
    data object Budget : Screen("budget")
    data object Guests : Screen("guests")
    data object Vendors : Screen("vendors")
    
    data object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: Long) = "task/$taskId"
    }
    data object AddTask : Screen("task/add")
    
    data object BudgetDetail : Screen("budget/{itemId}") {
        fun createRoute(itemId: Long) = "budget/$itemId"
    }
    data object AddBudgetItem : Screen("budget/add")
    
    data object GuestDetail : Screen("guest/{guestId}") {
        fun createRoute(guestId: Long) = "guest/$guestId"
    }
    data object AddGuest : Screen("guest/add")
    data object ContactPicker : Screen("guest/contacts")
    data object CollaborationContactPicker : Screen("collaboration/contacts")
    
    data object VendorDetail : Screen("vendor/{vendorId}") {
        fun createRoute(vendorId: Long) = "vendor/$vendorId"
    }
    data object AddVendor : Screen("vendor/add?name={name}&phone={phone}&email={email}&category={category}") {
        fun createRoute(name: String = "", phone: String = "", email: String = "", category: String = "") = 
            "vendor/add?name=$name&phone=$phone&email=$email&category=$category"
    }
    data object VendorContactPicker : Screen("vendor/contacts?category={category}") {
        fun createRoute(category: String = "") = "vendor/contacts?category=$category"
    }
    
    data object Settings : Screen("settings")
    data object Profile : Screen("profile")
    data object Auth : Screen("auth")
    data object JoinWedding : Screen("join-wedding")
    data object About : Screen("about")
    
    // Phase 2 & 3 Screens
    data object Analytics : Screen("analytics")
    data object Calendar : Screen("calendar")
    data object AddEvent : Screen("calendar/add/{selectedDate}") {
        fun createRoute(selectedDate: Long) = "calendar/add/$selectedDate"
    }
    data object EventDetail : Screen("calendar/{eventId}") {
        fun createRoute(eventId: Long) = "calendar/$eventId"
    }
    data object Seating : Screen("seating")
    data object Inspiration : Screen("inspiration")
    data object Documents : Screen("documents")
    data object VendorCategories : Screen("vendor-categories")
    data object Marketplace : Screen("marketplace/category/{category}") {
        fun createRoute(category: String) = "marketplace/category/$category"
    }
    data object MarketplaceVendorDetail : Screen("marketplace/vendor/{vendorId}") {
        fun createRoute(vendorId: Long) = "marketplace/vendor/$vendorId"
    }
    data object Collaboration : Screen("collaboration")
    data object AIRecommendations : Screen("ai-recommendations")
    data object Templates : Screen("templates")
    data object Premium : Screen("premium")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Tasks,
        label = "Checklist",
        selectedIcon = Icons.Filled.Checklist,
        unselectedIcon = Icons.Outlined.Checklist
    ),
    BottomNavItem(
        screen = Screen.Budget,
        label = "Budget",
        selectedIcon = Icons.Filled.AttachMoney,
        unselectedIcon = Icons.Outlined.AttachMoney
    ),
    BottomNavItem(
        screen = Screen.Calendar,
        label = "Events",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    BottomNavItem(
        screen = Screen.Vendors,
        label = "Vendors",
        selectedIcon = Icons.Filled.Store,
        unselectedIcon = Icons.Outlined.Store
    )
)

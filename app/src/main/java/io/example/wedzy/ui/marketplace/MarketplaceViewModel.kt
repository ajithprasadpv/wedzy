package io.example.wedzy.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.example.wedzy.data.model.MarketplaceVendor
import io.example.wedzy.data.model.VendorCategory
import io.example.wedzy.data.repository.MarketplaceRepository
import io.example.wedzy.location.LocationHelper
import io.example.wedzy.location.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketplaceUiState(
    val vendors: List<MarketplaceVendor> = emptyList(),
    val filteredVendors: List<MarketplaceVendor> = emptyList(),
    val nearbyVendors: List<MarketplaceVendor> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val showNearbyOnly: Boolean = false,
    val userLocation: UserLocation? = null,
    val locationPermissionNeeded: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {
    
    companion object {
        const val NEARBY_RADIUS_KM = 50.0
    }
    
    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()
    
    init {
        loadVendors()
        loadSampleVendors()
        fetchUserLocation()
    }
    
    fun fetchUserLocation() {
        viewModelScope.launch {
            if (!locationHelper.hasLocationPermission()) {
                _uiState.update { it.copy(locationPermissionNeeded = true) }
                return@launch
            }
            
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                _uiState.update { state ->
                    val nearbyVendors = filterNearbyVendors(state.vendors, location)
                    state.copy(
                        userLocation = location,
                        nearbyVendors = nearbyVendors,
                        locationPermissionNeeded = false,
                        filteredVendors = if (state.showNearbyOnly) nearbyVendors 
                            else filterVendors(state.vendors, state.selectedCategory, state.searchQuery, state.showFavoritesOnly)
                    )
                }
            }
        }
    }
    
    fun onLocationPermissionGranted() {
        _uiState.update { it.copy(locationPermissionNeeded = false) }
        fetchUserLocation()
    }
    
    private fun filterNearbyVendors(vendors: List<MarketplaceVendor>, userLocation: UserLocation): List<MarketplaceVendor> {
        return vendors.filter { vendor ->
            if (vendor.latitude == 0.0 && vendor.longitude == 0.0) return@filter false
            val distance = LocationHelper.calculateDistance(
                userLocation.latitude, userLocation.longitude,
                vendor.latitude, vendor.longitude
            )
            distance <= NEARBY_RADIUS_KM
        }.sortedBy { vendor ->
            LocationHelper.calculateDistance(
                userLocation.latitude, userLocation.longitude,
                vendor.latitude, vendor.longitude
            )
        }
    }
    
    fun toggleNearbyOnly() {
        _uiState.update { state ->
            val newShowNearby = !state.showNearbyOnly
            state.copy(
                showNearbyOnly = newShowNearby,
                filteredVendors = if (newShowNearby && state.userLocation != null) {
                    state.nearbyVendors
                } else {
                    filterVendors(state.vendors, state.selectedCategory, state.searchQuery, state.showFavoritesOnly)
                }
            )
        }
    }
    
    private fun loadVendors() {
        viewModelScope.launch {
            marketplaceRepository.getAllMarketplaceVendors().collect { vendors ->
                _uiState.update { state ->
                    state.copy(
                        vendors = vendors,
                        filteredVendors = filterVendors(
                            vendors,
                            state.selectedCategory,
                            state.searchQuery,
                            state.showFavoritesOnly
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun loadSampleVendors() {
        viewModelScope.launch {
            val existingVendors = _uiState.value.vendors
            // Check if vendors have coordinates - if not, replace with new location-based vendors
            val hasCoordinates = existingVendors.any { it.latitude != 0.0 && it.longitude != 0.0 }
            if (existingVendors.isEmpty() || !hasCoordinates) {
                // Clear old vendors without coordinates
                if (existingVendors.isNotEmpty() && !hasCoordinates) {
                    marketplaceRepository.deleteAllMarketplaceVendors()
                }
                val sampleVendors = listOf(
                    // Chennai vendors
                    MarketplaceVendor(
                        name = "Chennai Wedding Studios",
                        category = VendorCategory.PHOTOGRAPHER.name,
                        description = "Premium wedding photography and cinematography",
                        location = "Chennai, Tamil Nadu",
                        latitude = 13.0827,
                        longitude = 80.2707,
                        priceRange = "$$$",
                        minPrice = 75000.0,
                        maxPrice = 300000.0,
                        rating = 4.9f,
                        reviewCount = 245,
                        isVerified = true
                    ),
                    MarketplaceVendor(
                        name = "Blossom Florals Chennai",
                        category = VendorCategory.FLORIST.name,
                        description = "Traditional and modern floral decorations",
                        location = "T. Nagar, Chennai",
                        latitude = 13.0418,
                        longitude = 80.2341,
                        priceRange = "$$",
                        minPrice = 25000.0,
                        maxPrice = 150000.0,
                        rating = 4.7f,
                        reviewCount = 178,
                        isVerified = true
                    ),
                    MarketplaceVendor(
                        name = "The Grand Mandapam",
                        category = VendorCategory.VENUE.name,
                        description = "Luxurious wedding halls with modern amenities",
                        location = "Adyar, Chennai",
                        latitude = 13.0067,
                        longitude = 80.2572,
                        priceRange = "$$$$",
                        minPrice = 200000.0,
                        maxPrice = 800000.0,
                        rating = 4.8f,
                        reviewCount = 312,
                        isVerified = true,
                        isPremium = true
                    ),
                    // Bangalore vendors
                    MarketplaceVendor(
                        name = "Bangalore Bakes & Cakes",
                        category = VendorCategory.CAKE_BAKER.name,
                        description = "Artisanal wedding cakes and desserts",
                        location = "Koramangala, Bangalore",
                        latitude = 12.9352,
                        longitude = 77.6245,
                        priceRange = "$$",
                        minPrice = 15000.0,
                        maxPrice = 80000.0,
                        rating = 4.6f,
                        reviewCount = 156
                    ),
                    MarketplaceVendor(
                        name = "Rhythm Events Bangalore",
                        category = VendorCategory.DJ_MUSIC.name,
                        description = "Professional DJs and live music for weddings",
                        location = "Indiranagar, Bangalore",
                        latitude = 12.9784,
                        longitude = 77.6408,
                        priceRange = "$$$",
                        minPrice = 50000.0,
                        maxPrice = 200000.0,
                        rating = 4.8f,
                        reviewCount = 98,
                        isVerified = true
                    ),
                    // Mumbai vendors
                    MarketplaceVendor(
                        name = "Mumbai Glamour Makeup",
                        category = VendorCategory.MAKEUP_ARTIST.name,
                        description = "Celebrity makeup artists for your special day",
                        location = "Bandra, Mumbai",
                        latitude = 19.0596,
                        longitude = 72.8295,
                        priceRange = "$$$$",
                        minPrice = 100000.0,
                        maxPrice = 500000.0,
                        rating = 4.9f,
                        reviewCount = 423,
                        isVerified = true,
                        isPremium = true
                    ),
                    // Hyderabad vendors
                    MarketplaceVendor(
                        name = "Hyderabad Royal Caterers",
                        category = VendorCategory.CATERER.name,
                        description = "Authentic Hyderabadi cuisine for weddings",
                        location = "Jubilee Hills, Hyderabad",
                        latitude = 17.4325,
                        longitude = 78.4073,
                        priceRange = "$$$",
                        minPrice = 80000.0,
                        maxPrice = 400000.0,
                        rating = 4.7f,
                        reviewCount = 289,
                        isVerified = true
                    ),
                    // Delhi vendors
                    MarketplaceVendor(
                        name = "Delhi Decorators Elite",
                        category = VendorCategory.DECORATOR.name,
                        description = "Stunning wedding decor and event styling",
                        location = "South Delhi",
                        latitude = 28.5245,
                        longitude = 77.2066,
                        priceRange = "$$$$",
                        minPrice = 150000.0,
                        maxPrice = 1000000.0,
                        rating = 4.8f,
                        reviewCount = 367,
                        isVerified = true,
                        isPremium = true
                    ),
                    // Coimbatore vendors
                    MarketplaceVendor(
                        name = "Coimbatore Clicks Photography",
                        category = VendorCategory.PHOTOGRAPHER.name,
                        description = "Traditional South Indian wedding specialists",
                        location = "RS Puram, Coimbatore",
                        latitude = 11.0168,
                        longitude = 76.9558,
                        priceRange = "$$",
                        minPrice = 40000.0,
                        maxPrice = 150000.0,
                        rating = 4.5f,
                        reviewCount = 134,
                        isVerified = true
                    ),
                    MarketplaceVendor(
                        name = "Kovai Wedding Planners",
                        category = VendorCategory.WEDDING_PLANNER.name,
                        description = "Complete wedding planning services",
                        location = "Peelamedu, Coimbatore",
                        latitude = 11.0248,
                        longitude = 77.0028,
                        priceRange = "$$$",
                        minPrice = 100000.0,
                        maxPrice = 500000.0,
                        rating = 4.6f,
                        reviewCount = 87
                    )
                )
                marketplaceRepository.insertMarketplaceVendors(sampleVendors)
            }
        }
    }
    
    fun setCategory(category: String?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredVendors = filterVendors(
                    state.vendors,
                    category,
                    state.searchQuery,
                    state.showFavoritesOnly
                )
            )
        }
    }
    
    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredVendors = filterVendors(
                    state.vendors,
                    state.selectedCategory,
                    query,
                    state.showFavoritesOnly
                )
            )
        }
    }
    
    fun toggleFavoritesOnly() {
        _uiState.update { state ->
            val newShowFavorites = !state.showFavoritesOnly
            state.copy(
                showFavoritesOnly = newShowFavorites,
                filteredVendors = filterVendors(
                    state.vendors,
                    state.selectedCategory,
                    state.searchQuery,
                    newShowFavorites
                )
            )
        }
    }
    
    private fun filterVendors(
        vendors: List<MarketplaceVendor>,
        category: String?,
        query: String,
        favoritesOnly: Boolean
    ): List<MarketplaceVendor> {
        return vendors.filter { vendor ->
            val categoryMatch = category == null || vendor.category == category
            val queryMatch = query.isBlank() || 
                vendor.name.contains(query, ignoreCase = true) ||
                vendor.description.contains(query, ignoreCase = true) ||
                vendor.location.contains(query, ignoreCase = true)
            val favoriteMatch = !favoritesOnly || vendor.isFavorited
            categoryMatch && queryMatch && favoriteMatch
        }
    }
    
    fun toggleFavorite(vendor: MarketplaceVendor) {
        viewModelScope.launch {
            marketplaceRepository.toggleFavorite(vendor.id, !vendor.isFavorited)
        }
    }
}

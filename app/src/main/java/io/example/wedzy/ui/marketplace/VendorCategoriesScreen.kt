package io.example.wedzy.ui.marketplace

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class VendorCategoryDisplay(
    val name: String,
    val subcategories: String,
    val imageUrl: String,
    val accentColor: Color,
    val categoryKey: String
)

val vendorCategories = listOf(
    VendorCategoryDisplay(
        name = "Venues",
        subcategories = "Banquet Halls, Marriage Gardens, Hotels, Resorts",
        imageUrl = "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=400",
        accentColor = Color(0xFFE8D5B7),
        categoryKey = "VENUE"
    ),
    VendorCategoryDisplay(
        name = "Photographers",
        subcategories = "Wedding Photography, Pre-Wedding, Candid",
        imageUrl = "https://images.unsplash.com/photo-1537633552985-df8429e8048b?w=400",
        accentColor = Color(0xFFF5E6E0),
        categoryKey = "PHOTOGRAPHER"
    ),
    VendorCategoryDisplay(
        name = "Makeup & Beauty",
        subcategories = "Bridal Makeup Artists, Family Makeup, Hair Stylists",
        imageUrl = "https://images.unsplash.com/photo-1487412947147-5cebf100ffc2?w=400",
        accentColor = Color(0xFFFFE4E1),
        categoryKey = "MAKEUP_ARTIST"
    ),
    VendorCategoryDisplay(
        name = "Planning & Decor",
        subcategories = "Wedding Planners, Decorators, Event Stylists",
        imageUrl = "https://images.unsplash.com/photo-1478146059778-26a7eb82a8f7?w=400",
        accentColor = Color(0xFFDEB887),
        categoryKey = "DECORATOR"
    ),
    VendorCategoryDisplay(
        name = "Catering",
        subcategories = "Wedding Caterers, Multi-cuisine, Traditional",
        imageUrl = "https://images.unsplash.com/photo-1555244162-803834f70033?w=400",
        accentColor = Color(0xFFFFF8DC),
        categoryKey = "CATERER"
    ),
    VendorCategoryDisplay(
        name = "Mehndi Artists",
        subcategories = "Bridal Mehndi, Arabic, Traditional Designs",
        imageUrl = "https://images.unsplash.com/photo-1595907934625-a8c13c16b2be?w=400",
        accentColor = Color(0xFFFFDAB9),
        categoryKey = "OTHER"
    ),
    VendorCategoryDisplay(
        name = "Music & Dance",
        subcategories = "DJs, Sangeet Choreographers, Live Bands",
        imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
        accentColor = Color(0xFFE6E6FA),
        categoryKey = "DJ_MUSIC"
    ),
    VendorCategoryDisplay(
        name = "Jewellery",
        subcategories = "Bridal Jewellery, Gold, Diamond, Artificial",
        imageUrl = "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=400",
        accentColor = Color(0xFFFFE4B5),
        categoryKey = "JEWELER"
    ),
    VendorCategoryDisplay(
        name = "Bridal Wear",
        subcategories = "Lehengas, Sarees, Designer Wear",
        imageUrl = "https://images.unsplash.com/photo-1594463750939-ebb28c3f7f75?w=400",
        accentColor = Color(0xFFFFC0CB),
        categoryKey = "DRESS_DESIGNER"
    ),
    VendorCategoryDisplay(
        name = "Invitations",
        subcategories = "Wedding Cards, Digital Invites, E-Cards",
        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=400",
        accentColor = Color(0xFFE0FFFF),
        categoryKey = "INVITATION_DESIGNER"
    ),
    VendorCategoryDisplay(
        name = "Florists",
        subcategories = "Floral Decorations, Bouquets, Mandap Flowers",
        imageUrl = "https://images.unsplash.com/photo-1487530811176-3780de880c2d?w=400",
        accentColor = Color(0xFFFFB6C1),
        categoryKey = "FLORIST"
    ),
    VendorCategoryDisplay(
        name = "Cakes & Desserts",
        subcategories = "Wedding Cakes, Dessert Tables, Custom Cakes",
        imageUrl = "https://images.unsplash.com/photo-1535254973040-607b474cb50d?w=400",
        accentColor = Color(0xFFFAEBD7),
        categoryKey = "CAKE_BAKER"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorCategoriesScreen(
    onNavigateBack: () -> Unit,
    onCategoryClick: (String) -> Unit,
    userLocation: String = "Your Location"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Vendor Categories",
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = userLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(vendorCategories) { category ->
                VendorCategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category.categoryKey) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendorCategoryCard(
    category: VendorCategoryDisplay,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxSize()
                    .background(category.accentColor)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                category.accentColor.copy(alpha = 0.3f),
                                Color.White
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = category.subcategories,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = category.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 40.dp,
                                bottomStart = 40.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

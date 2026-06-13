package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.content.Context
import com.example.data.TSLuxeWearRepository
import com.example.data.AuthManager
import com.example.data.UserRole
import com.example.model.Product
import com.example.model.Store
import coil.compose.AsyncImage
import com.example.model.Order
import com.example.model.Offer
import com.example.model.ProductReview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import com.example.ui.theme.LuxeBurgundy
import com.example.ui.theme.LuxeCream
import com.example.ui.theme.LuxeGold
import com.example.ui.theme.LuxeLightGold
import com.example.ui.theme.LuxeDustyRose

@Composable
fun CustomerDashboardScreen(
    repository: TSLuxeWearRepository,
    onProductClick: (Product) -> Unit,
    onVirtualTryClick: (Product) -> Unit
) {
    val stores by repository.storesFlow.collectAsState()
    val products by repository.productsFlow.collectAsState()
    val followedStoreIds by repository.followedStoreIdsFlow.collectAsState()
    val wishlist by repository.wishlistFlow.collectAsState()
    val offers by repository.offersFlow.collectAsState()

    var selectedStoreId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Active Tab in Shopping mode
    var activeSubTab by remember { mutableStateOf(0) } // 0: Browse, 1: Followed Stores, 2: Wishlist, 3: Orders, 4: Inquiries
    var nearbyStoresDiscovered by remember { mutableStateOf(false) }

    // Filter active stores only
    val activeStores = stores.filter { it.status == "Active" }
    val activeStoreIds = activeStores.map { it.id }.toSet()

    // Filter products linked with active stores
    val displayProducts = products.filter {
        it.storeId in activeStoreIds &&
        (selectedStoreId == null || it.storeId == selectedStoreId) &&
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize().background(LuxeCream)) {
        // Aesthetic Sub Navigation row
        ScrollableTabRow(
            selectedTabIndex = activeSubTab,
            containerColor = Color.White,
            contentColor = LuxeBurgundy,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    color = LuxeBurgundy
                )
            }
        ) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Explore Collections", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Followed Boutiques", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.StarBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Wishlist", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Tab(selected = activeSubTab == 3, onClick = { activeSubTab = 3 }) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("My Orders", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Tab(selected = activeSubTab == 4, onClick = { activeSubTab = 4 }) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inquiries", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            Tab(selected = activeSubTab == 5, onClick = { activeSubTab = 5 }) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Profile & Alerts", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        AnimatedContent(
            targetState = activeSubTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "CustomerTabNavigator"
        ) { targetTab ->
            when (targetTab) {
            0 -> {
                // Explore / Catalog View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Search and Category Section
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Discover Luxury Handloom Wear",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = LuxeBurgundy
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Premium custom styles across India's boutique designers.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth().testTag("search_input"),
                                    placeholder = { Text("Search elegant sarees, kurtis...", fontSize = 13.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LuxeBurgundy) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LuxeBurgundy,
                                        unfocusedBorderColor = Color.LightGray
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Boutiques Row
                    item {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = "Featured Boutique Stores",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = LuxeBurgundy,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    FilterChip(
                                        selected = selectedStoreId == null,
                                        onClick = { selectedStoreId = null },
                                        label = { Text("All Boutiques") }
                                    )
                                }
                                items(activeStores) { store ->
                                    val isFollowed = followedStoreIds.contains(store.id)
                                    FilterChip(
                                        selected = selectedStoreId == store.id,
                                        onClick = { selectedStoreId = store.id },
                                        label = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(store.logoUrl)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(store.name)
                                                if (isFollowed) {
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Icon(Icons.Default.CheckCircle, null, tint = LuxeGold, modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Custom Brand Banner for Selected Store
                    if (selectedStoreId != null) {
                        val activeStore = activeStores.find { it.id == selectedStoreId }
                        if (activeStore != null) {
                            item {
                                StoreShowcaseBanner(store = activeStore, isFollowed = followedStoreIds.contains(activeStore.id)) {
                                    repository.toggleFollowStore(activeStore.id)
                                }
                            }

                            val storeOffers = offers.filter { it.storeId == selectedStoreId }
                            if (storeOffers.isNotEmpty()) {
                                item {
                                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                        Text(
                                            text = "✨ Active Boutique Promo Coupons:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = LuxeBurgundy
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(storeOffers) { offer ->
                                                Card(
                                                    modifier = Modifier.width(220.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCF6F0)),
                                                    border = BorderStroke(1.2.dp, LuxeGold.copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.ConfirmationNumber, null, tint = LuxeGold, modifier = Modifier.size(14.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(offer.code, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LuxeBurgundy)
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("(${offer.discountPercent}% OFF)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeGold)
                                                        }
                                                        Spacer(modifier = Modifier.height(3.dp))
                                                        Text(offer.title, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                                        Text(offer.description, fontSize = 9.sp, color = Color.Gray, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Categories Selector
                    item {
                        val availableCategories = listOf("All", "Sarees", "Kurtis", "Dresses", "Western Wear", "Ethnic Wear", "Accessories")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableCategories) { cat ->
                                SuggestionChip(
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 12.sp) },
                                    border = BorderStroke(1.dp, if (selectedCategory == cat) LuxeBurgundy else Color.LightGray),
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (selectedCategory == cat) LuxeBurgundy.copy(alpha = 0.08f) else Color.Transparent,
                                        labelColor = if (selectedCategory == cat) LuxeBurgundy else Color.DarkGray
                                    )
                                )
                            }
                        }
                    }

                    // Lookbook Products Grid (Feminine modern card rows)
                    if (displayProducts.isEmpty()) {
                        item {
                            EmptyStatePlaceholder(
                                message = "No high-fashion products match your filter.",
                                hint = "Try clearing search keywords or choosing 'All Boutiques'"
                            )
                        }
                    } else {
                        val rows = displayProducts.chunked(2)
                        items(rows) { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (p in rowItems) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductGridCard(
                                            product = p,
                                            isWishlisted = wishlist.contains(p.id),
                                            onWishlistClick = { repository.addToWishlist(p.id) },
                                            onVirtualTryClick = { onVirtualTryClick(p) },
                                            onClick = { onProductClick(p) }
                                        )
                                    }
                                }
                                if (rowItems.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Followed Boutiques tab
                val followedStores = activeStores.filter { followedStoreIds.contains(it.id) }
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Text("Boutiques You Follow", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxeBurgundy)
                        Text("Get notified when these store owners publish new festival launches.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        val context = LocalContext.current
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, LuxeGold.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = LuxeBurgundy, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Luxe Handloom Atelier Finder", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = LuxeBurgundy)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "To locate physical multitenant weavers and interactive dynamic showrooms near your geographic area, click 'Locate Ateliers' below.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                if (nearbyStoresDiscovered) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(LuxeCream, RoundedCornerShape(6.dp))
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("🌸 PRIYA BOUTIQUE (1.2 km away) - Active Weaving Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                        Text("👗 FASHION QUEEN (3.5 km away) - Silk Showcase open", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                        Text("⭐ GOLDEN WEAVES (4.8 km away) - Designer loom is active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            com.example.data.PermissionManager.requestPermissionContext(
                                                com.example.data.LuxePermission.LOCATION,
                                                onGranted = {
                                                    nearbyStoresDiscovered = true
                                                },
                                                onDenied = {
                                                    // Dynamic gracefully handled fallback state - continues browsing normally, but notices them
                                                    android.widget.Toast.makeText(context, "Location permission declined. Displaying default national online indexes instead.", android.widget.Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_locate_nearby_stores"),
                                        colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Locate Ateliers Near Me", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    if (followedStores.isEmpty()) {
                        item {
                            EmptyStatePlaceholder(
                                message = "You aren't following any design boutiques yet.",
                                hint = "Go to 'Explore' tab and follow boutiques like Priya Boutique or Fashion Queen to show support!"
                            )
                        }
                    } else {
                        items(followedStores) { st ->
                            StoreShowcaseBanner(store = st, isFollowed = true) {
                                repository.toggleFollowStore(st.id)
                            }
                        }
                    }
                }
            }

            2 -> {
                // Wishlist Tab
                val wishlistedProducts = products.filter { wishlist.contains(it.id) && it.storeId in activeStoreIds }
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("My Boutique Wishlist", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxeBurgundy)
                        Text("Items you love. You'll get automatically alerted when stock is restocked.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (wishlistedProducts.isEmpty()) {
                        item {
                            EmptyStatePlaceholder(
                                message = "Your wishlist is empty.",
                                hint = "Tap the star icon on sarees to add them to your curated favorites."
                            )
                        }
                    } else {
                        items(wishlistedProducts) { prod ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onProductClick(prod) },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFF2EBEB))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(60.dp).background(LuxeCream, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (prod.imageUrl.startsWith("http")) {
                                            AsyncImage(
                                                model = prod.imageUrl,
                                                contentDescription = prod.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        } else {
                                            Text(prod.imageUrl, fontSize = 32.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = prod.storeName, style = MaterialTheme.typography.labelSmall, color = LuxeDustyRose)
                                        Text(text = prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxeBurgundy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "₹${prod.price}",
                                                style = MaterialTheme.typography.bodySmall,
                                                textDecoration = if (prod.discountPrice != null) TextDecoration.LineThrough else null,
                                                color = Color.Gray
                                            )
                                            if (prod.discountPrice != null) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(text = "₹${prod.discountPrice}", fontWeight = FontWeight.Bold, color = LuxeBurgundy, fontSize = 13.sp)
                                            }
                                        }
                                        if (prod.stockQuantity == 0) {
                                            Text("Out of Stock (Auto Restock Alert ON)", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.SemiBold)
                                        } else if (prod.stockQuantity < prod.lowStockThreshold) {
                                            Text("Running Low! Only ${prod.stockQuantity} left", fontSize = 11.sp, color = LuxeGold, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    IconButton(onClick = { repository.addToWishlist(prod.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Orders tab (Includes tracking timeline and custom invoice download references)
                val orders by repository.ordersFlow.collectAsState()
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("My Boutique Inquiry Orders", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxeBurgundy)
                        Text("Track the real-time status of your WhatsApp boutique bookings.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (orders.isEmpty()) {
                        item {
                            EmptyStatePlaceholder(
                                message = "No order inquiries found.",
                                hint = "Select a designer piece, click 'Buy Now', fill the details form, and submit!"
                            )
                        }
                    } else {
                        items(orders) { order ->
                            OrderTrackingCard(order = order)
                        }
                    }
                }
            }

            4 -> {
                // Inquiries tab
                val inquiries by repository.inquiriesFlow.collectAsState()
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Text("My Product Questions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxeBurgundy)
                        Text("Talk directly to boutique designers regarding fabric customization, custom lengths, etc.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (inquiries.isEmpty()) {
                        item {
                            EmptyStatePlaceholder(
                                message = "No questions filed yet.",
                                hint = "Have questions about fabric care, sizing or blouse fitting? Go to any product's details page and tap 'Ask Question'!"
                            )
                        }
                    } else {
                        items(inquiries) { inq ->
                            InquiryChatCard(inq = inq)
                        }
                    }
                }
            }

            5 -> {
                CustomerProfileAndAlertsSettingsScreen(repository)
            }
        }
        }
    }
}

@Composable
fun StoreShowcaseBanner(store: Store, isFollowed: Boolean, onFollowClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color(store.bannerColor).copy(alpha = 0.2f))
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp).background(
                    Brush.horizontalGradient(
                        listOf(Color(store.bannerColor), Color(store.bannerColor).copy(alpha = 0.7f))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(46.dp).background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(store.logoUrl, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = store.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = store.storeType,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Owner: ${store.ownerName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("${store.followersCount} followers", fontSize = 11.sp, color = Color.Gray)
                }

                Button(
                    onClick = {
                        if (!isFollowed) {
                            com.example.data.PermissionManager.requestPermissionContext(
                                com.example.data.LuxePermission.NOTIFICATION,
                                onGranted = {
                                    onFollowClick()
                                },
                                onDenied = {
                                    onFollowClick()
                                }
                            )
                        } else {
                            onFollowClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowed) Color.LightGray else LuxeBurgundy,
                        contentColor = if (isFollowed) Color.DarkGray else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier.height(34.dp).testTag("follow_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isFollowed) "Following" else "Follow Store", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    isWishlisted: Boolean,
    onWishlistClick: () -> Unit,
    onVirtualTryClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(140.dp).background(LuxeLightGold),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.startsWith("http")) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text(product.imageUrl, fontSize = 64.sp)
                }

                // Store badge
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(LuxeBurgundy, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = product.storeName,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Wishlist icon
                IconButton(
                    onClick = onWishlistClick,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.9f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (isWishlisted) LuxeGold else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Inventory warnings
                if (product.stockQuantity == 0) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color.Red.copy(alpha = 0.8f)).align(Alignment.BottomCenter).padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("OUT OF STOCK", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (product.stockQuantity <= product.lowStockThreshold) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(LuxeGold.copy(alpha = 0.9f)).align(Alignment.BottomCenter).padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("LOW STOCK: ONLY ${product.stockQuantity} LEFT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LuxeBurgundy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.fabric,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.discountPrice != null) {
                        Text(
                            text = "₹${product.discountPrice}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LuxeBurgundy
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${product.price}",
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    } else {
                        Text(
                            text = "₹${product.price}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LuxeBurgundy
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onVirtualTryClick() },
                    modifier = Modifier.fillMaxWidth().height(32.dp).testTag("product_card_try_ai_${product.id}"),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy.copy(alpha = 0.08f), contentColor = LuxeBurgundy),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Virtual Try On",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Virtual Try AI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    product: Product,
    repository: TSLuxeWearRepository,
    onBack: () -> Unit,
    onVirtualTryClick: () -> Unit,
    onPlaceOrder: (Order) -> Unit
) {
    val context = LocalContext.current
    val wishlist by repository.wishlistFlow.collectAsState()

    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "Standard") }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: "Standard") }

    // Dialog form triggers
    var showOrderForm by remember { mutableStateOf(false) }
    var showInquiryDialog by remember { mutableStateOf(false) }

    // Forms retrieved from SharedPreferences for auto-fill returning clients
    val sharedPrefs = context.getSharedPreferences("luxe_customer_prefs", Context.MODE_PRIVATE)
    val activeUser = AuthManager.currentUserFlow.value
    val defaultName = if (activeUser != null && activeUser.role != UserRole.GUEST) activeUser.displayName else ""
    val defaultPhone = ""
    val defaultAddress = ""
    var custName by remember { mutableStateOf(sharedPrefs.getString("saved_name", defaultName) ?: defaultName) }
    var custPhone by remember { mutableStateOf(sharedPrefs.getString("saved_phone", defaultPhone) ?: defaultPhone) }
    var custAddress by remember { mutableStateOf(sharedPrefs.getString("saved_address", defaultAddress) ?: defaultAddress) }

    var custNameError by remember { mutableStateOf<String?>(null) }
    var custPhoneError by remember { mutableStateOf<String?>(null) }
    var custAddressError by remember { mutableStateOf<String?>(null) }

    val storeSettingsMap by repository.storeSettingsFlow.collectAsState()
    val matchingSettings = storeSettingsMap[product.storeId] ?: com.example.model.StoreOrderSettings(product.storeId)

    var isCodSelected by remember(matchingSettings) { mutableStateOf(matchingSettings.codAvailable) }

    var inquiryQuestionByCustomer by remember { mutableStateOf("") }

    // WhatsApp simulated dialog
    var showWhatsAppResult by remember { mutableStateOf<Order?>(null) }

    // Dynamic rating review and coupon states
    var localRatingInput by remember { mutableStateOf(5) }
    var localReviewTextInput by remember { mutableStateOf("") }
    var localReviewerNameInput by remember { mutableStateOf("") }
    var reviewPhotoAttached by remember { mutableStateOf<String?>(null) }

    var promoCodeEntered by remember { mutableStateOf("") }
    var appliedPromoCode by remember { mutableStateOf<com.example.model.Offer?>(null) }
    var promoFeedbackMessage by remember { mutableStateOf("") }

    val stores by repository.storesFlow.collectAsState()
    val offers by repository.offersFlow.collectAsState()
    val matchingStore = stores.find { it.id == product.storeId }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().height(260.dp).background(LuxeLightGold),
                contentAlignment = Alignment.Center
            ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                ) {
                    Box(modifier = Modifier.size(36.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LuxeBurgundy)
                    }
                }

                if (product.imageUrl.startsWith("http")) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text(product.imageUrl, fontSize = 120.sp)
                }

                // High Fashion Ribbons
                Box(
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).background(LuxeBurgundy, RoundedCornerShape(4.dp))
                ) {
                    Text(
                        product.category,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        product.storeName,
                        fontWeight = FontWeight.Bold,
                        color = LuxeDustyRose,
                        fontSize = 14.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier.testTag("share_product_btn"),
                            onClick = {
                                val shareLink = "myapp.com/product?storeId=${product.storeId}&productId=${product.id}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("TS LuxeWear Product", shareLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Product link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                try {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, "Exquisite find on TS LuxeWear! View ${product.name}: $shareLink")
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Lookbook Sensation")
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {}
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Partner Sensation link",
                                tint = LuxeBurgundy
                            )
                        }

                        IconButton(onClick = { repository.addToWishlist(product.id) }) {
                            Icon(
                                imageVector = if (wishlist.contains(product.id)) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Curated",
                                tint = if (wishlist.contains(product.id)) LuxeGold else Color.Gray
                            )
                        }
                    }
                }

                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = LuxeBurgundy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.discountPrice != null) {
                        Text(
                            text = "₹${product.discountPrice}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = LuxeBurgundy
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "₹${product.price}",
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 15.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.background(LuxeGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            val pct = (((product.price - product.discountPrice) / product.price) * 100).toInt()
                            Text("$pct% OFF", color = LuxeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "₹${product.price}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = LuxeBurgundy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Luxury Highlights & Specs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxeBurgundy)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LuxeCream),
                    border = BorderStroke(1.dp, Color(0xFFF2EBEB))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("Premium Fabric: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                            Text(product.fabric, fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("Boutique address: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                            Text(
                                "Locate on Maps 📍",
                                fontSize = 12.sp,
                                color = LuxeBurgundy,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    // Simulated MAP link launch
                                }
                            )
                        }
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("Stock Status: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                            if (product.stockQuantity == 0) {
                                Text("Sold Out (Can still request custom tailored order)", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("${product.stockQuantity} units available", color = if (product.stockQuantity <= product.lowStockThreshold) LuxeGold else Color.DarkGray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Configuration selectors (Sizes)
                Text("Select Tailoring Size", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (sz in product.sizes) {
                        val isSelected = selectedSize == sz
                        InputChip(
                            selected = isSelected,
                            onClick = { selectedSize = sz },
                            label = { Text(sz) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Colors
                Text("Exquisite Shade Choice", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (cl in product.colors) {
                        val isSelected = selectedColor == cl
                        InputChip(
                            selected = isSelected,
                            onClick = { selectedColor = cl },
                            label = { Text(cl) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Description", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Premium AI Try-On Button
                Button(
                    onClick = { onVirtualTryClick() },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("product_detail_try_ai_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxeGold, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Virtual Try AI (Fit & Drape)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showInquiryDialog = true },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("ask_question_btn"),
                        border = BorderStroke(1.5.dp, LuxeBurgundy),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxeBurgundy)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ask Boutique", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { showOrderForm = true },
                        modifier = Modifier.weight(1.5f).height(48.dp).testTag("buy_now_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Order via WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Dynamic Reviews and Ratings Section
                val reviews by repository.reviewsFlow.collectAsState()
                val productReviews = reviews.filter { it.productId == product.id }
                val avgStars = if (productReviews.isEmpty()) 5.0 else productReviews.map { it.rating }.average()

                Text("Boutique Styling Reviews & Fit Feedback", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = LuxeBurgundy)
                Spacer(modifier = Modifier.height(8.dp))

                // Show Average Score
                Card(
                    colors = CardDefaults.cardColors(containerColor = LuxeCream),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    border = BorderStroke(1.dp, Color(0xFFF2EBEB))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 16.dp)) {
                            Text(String.format("%.1f", avgStars), fontSize = 32.sp, fontWeight = FontWeight.Black, color = LuxeBurgundy)
                            Text("out of 5", fontSize = 10.sp, color = Color.Gray)
                        }
                        Column {
                            val starRowsCount = productReviews.size
                            Text(
                                text = "Based on $starRowsCount verified customer purchases",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                repeat(5) { starIdx ->
                                    Icon(
                                        imageVector = if (starIdx < avgStars.toInt()) Icons.Default.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = LuxeGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(" (Highly fits standard sizing)", fontSize = 10.sp, color = Color(0xFF137333), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Render reviews inline list
                if (productReviews.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp)).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No styling reviews yet. Be the first to add your drape feedback!", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                } else {
                    productReviews.forEach { rev ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(0.8.dp, Color(0xFFEFE6E8)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(rev.reviewerName, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.DarkGray)
                                    Row {
                                        repeat(5) { starIdx ->
                                            Icon(
                                                imageVector = if (starIdx < rev.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                                contentDescription = null,
                                                tint = LuxeGold,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rev.feedback,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add styling review form CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, LuxeBurgundy.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Provide purchase rating & fit support details", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LuxeBurgundy)
                        Text("Submit feedback for luxury silk drape and blouse alignment:", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Star rating Selector Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Drape score:", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            repeat(5) { starIdx ->
                                val starRatingVal = starIdx + 1
                                IconButton(
                                    onClick = { localRatingInput = starRatingVal },
                                    modifier = Modifier.size(28.dp).testTag("select_star_${starRatingVal}")
                                ) {
                                    Icon(
                                        imageVector = if (starIdx < localRatingInput) Icons.Default.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = LuxeGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = localReviewTextInput,
                            onValueChange = { localReviewTextInput = it },
                            placeholder = { Text("Write about silk fabric stiffness, waist sizing details...", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().height(64.dp).testTag("post_review_input_text"),
                            textStyle = TextStyle(fontSize = 11.sp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LuxeBurgundy)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Product Review photo attachment
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (reviewPhotoAttached != null) Icons.Default.CameraAlt else Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = LuxeBurgundy,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (reviewPhotoAttached != null) "Photo attached: $reviewPhotoAttached" else "Attach drape photos to review:",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LuxeBurgundy.copy(alpha = 0.08f))
                                    .clickable {
                                        com.example.data.PermissionManager.requestPermissionContext(
                                            com.example.data.LuxePermission.CAMERA,
                                            onGranted = {
                                                reviewPhotoAttached = "📸 drape_capture.jpg"
                                            },
                                            onDenied = {
                                                // Graced fallback - still allow gallery photo choice directly!
                                                com.example.data.PermissionManager.requestPermissionContext(
                                                    com.example.data.LuxePermission.GALLERY,
                                                    onGranted = {
                                                        reviewPhotoAttached = "🖼️ lookbook_gallery.png"
                                                    },
                                                    onDenied = {}
                                                )
                                            }
                                        )
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+ Add Photo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = localReviewerNameInput,
                                onValueChange = { localReviewerNameInput = it },
                                label = { Text("Reviewer Name", fontSize = 10.sp) },
                                modifier = Modifier.width(150.dp).height(46.dp).testTag("post_reviewer_name_text"),
                                textStyle = TextStyle(fontSize = 11.sp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LuxeBurgundy)
                            )
                            Button(
                                onClick = {
                                    if (localReviewerNameInput.isNotEmpty() && localReviewTextInput.isNotEmpty()) {
                                        repository.submitReviewRating(product.id, localReviewerNameInput, localRatingInput, localReviewTextInput)
                                        localReviewTextInput = ""
                                        localReviewerNameInput = ""
                                        localRatingInput = 5
                                        reviewPhotoAttached = null
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(36.dp).testTag("submit_rating_feedback_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Publish Feedback", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet 1: Inquiry Dialog
    if (showInquiryDialog) {
        Dialog(onDismissRequest = { showInquiryDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Custom Inquiry", fontWeight = FontWeight.Bold, color = LuxeBurgundy, fontSize = 16.sp)
                        IconButton(onClick = { showInquiryDialog = false }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    Text("Your question about: ${product.name}", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inquiryQuestionByCustomer,
                        onValueChange = { inquiryQuestionByCustomer = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("inquiry_text_input"),
                        placeholder = { Text("Ask about custom blouse sizing or length limits...", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeBurgundy,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (inquiryQuestionByCustomer.isNotEmpty()) {
                                repository.submitProductInquiry(
                                    customerId = activeUser?.email ?: "guest_customer",
                                    customerName = custName,
                                    product = product,
                                    questionInText = inquiryQuestionByCustomer
                                )
                                inquiryQuestionByCustomer = ""
                                showInquiryDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("submit_inquiry_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy)
                    ) {
                        Text("Submit Question")
                    }
                }
            }
        }
    }

    // Modal Sheet 2: Customer Details Form BEFORE WhatsApp opens!
    if (showOrderForm) {
        Dialog(onDismissRequest = { showOrderForm = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, LuxeBurgundy.copy(alpha = 0.2f))
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Boutique Order Request", fontWeight = FontWeight.Bold, color = LuxeBurgundy, fontSize = 18.sp)
                            IconButton(onClick = { showOrderForm = false }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                        Text(
                            text = "Fill your delivery credentials below to document this booking on the platform ledger. WhatsApp will open automatically.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = custName,
                            onValueChange = { 
                                custName = it
                                custNameError = null
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("customer_name_input"),
                            label = { Text("FullName", fontSize = 12.sp) },
                            singleLine = true,
                            isError = custNameError != null,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LuxeBurgundy)
                        )
                        custNameError?.let {
                            Text(it, color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = custPhone,
                            onValueChange = { 
                                custPhone = it
                                custPhoneError = null
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("customer_phone_input"),
                            label = { Text("WhatsApp Phone Number", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = custPhoneError != null,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LuxeBurgundy)
                        )
                        custPhoneError?.let {
                            Text(it, color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = custAddress,
                            onValueChange = { 
                                custAddress = it
                                custAddressError = null
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("customer_address_input"),
                            label = { Text("Complete Delivery Address", fontSize = 12.sp) },
                            isError = custAddressError != null,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LuxeBurgundy)
                        )
                        custAddressError?.let {
                            Text(it, color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Boutique Discount Coupon", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LuxeBurgundy)
                        
                        val itemOffers = offers.filter { it.storeId == product.storeId }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = promoCodeEntered,
                                onValueChange = { promoCodeEntered = it },
                                label = { Text("Enter Coupon Code", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("coupon_input_text"),
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LuxeBurgundy)
                            )
                            Button(
                                onClick = {
                                    val matched = itemOffers.find { it.code.lowercase() == promoCodeEntered.trim().lowercase() }
                                    if (matched != null) {
                                        appliedPromoCode = matched
                                        promoFeedbackMessage = "Successfully Applied: ${matched.discountPercent}% Discount!"
                                    } else {
                                        appliedPromoCode = null
                                        promoFeedbackMessage = "Invalid boutique coupon code"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.height(48.dp).testTag("apply_coupon_btn")
                            ) {
                                Text("Apply", fontSize = 12.sp)
                            }
                        }
                        if (promoFeedbackMessage.isNotEmpty()) {
                            Text(
                                text = promoFeedbackMessage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (appliedPromoCode != null) Color(0xFF137333) else Color.Red,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // Prepopulated Coupons selector
                        if (itemOffers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("TAP TO APPLY BOUTIQUE VOUCHERS:", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                itemOffers.forEach { offer ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFCF6F0), RoundedCornerShape(4.dp))
                                            .border(0.8.dp, LuxeGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .clickable {
                                                promoCodeEntered = offer.code
                                                appliedPromoCode = offer
                                                promoFeedbackMessage = "Successfully Applied: ${offer.discountPercent}% Discount!"
                                            }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = "${offer.code} (-${offer.discountPercent}%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Shipping & Payment Method", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LuxeBurgundy)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = LuxeLightGold.copy(alpha = 0.35f)),
                            border = BorderStroke(0.5.dp, LuxeGold.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (matchingSettings.codAvailable) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(androidx.compose.material.icons.Icons.Default.CheckCircle, "Available", tint = Color(0xFF137333), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Cash on Delivery (COD) Available", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    if (matchingSettings.returnPolicyEnabled) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(androidx.compose.material.icons.Icons.Default.Refresh, "Returns Enabled", tint = Color(0xFF137333), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("🔄 7-Day boutique returns & exchanges allowed!", fontSize = 10.sp, color = Color(0xFF137333))
                                        }
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(androidx.compose.material.icons.Icons.Default.Info, "Returns Disabled", tint = LuxeBurgundy, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("⚠️ All sales final (No returns allowed for COD)", fontSize = 10.sp, color = LuxeBurgundy)
                                        }
                                    }
                                    
                                    // Segment choice
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .clickable { isCodSelected = true }
                                            .testTag("cod_selection_row"),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isCodSelected,
                                            onClick = { isCodSelected = true },
                                            colors = RadioButtonDefaults.colors(selectedColor = LuxeBurgundy)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cash on Delivery (COD)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isCodSelected = false },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = !isCodSelected,
                                            onClick = { isCodSelected = false },
                                            colors = RadioButtonDefaults.colors(selectedColor = LuxeBurgundy)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Prepay & Order directly on WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(androidx.compose.material.icons.Icons.Default.Warning, "Not Available", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("COD NOT Available for this boutique", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("This designer only accepts pre-paid / WhatsApp chat bookings.", fontSize = 10.sp, color = Color.Gray)
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = true,
                                            onClick = {},
                                            enabled = false,
                                            colors = RadioButtonDefaults.colors(selectedColor = LuxeBurgundy)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Order & Prepay on WhatsApp (Forced choice)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                if (matchingSettings.deliveryChargeOn && matchingSettings.deliveryCharge > 0) {
                                    Text("🚚 Standard delivery shipping charge of ₹${matchingSettings.deliveryCharge.toInt()} applies.", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                                } else {
                                    Text("🚚 Enjoy FREE elite insured shipping on this order!", fontSize = 10.sp, color = Color(0xFF137333), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Billing Breakdown & Ledger Log", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LuxeBurgundy)
                        
                        val basePrice = product.discountPrice ?: product.price
                        val discountMultiplier = if (appliedPromoCode != null) appliedPromoCode!!.discountPercent / 100.0 else 0.0
                        val savings = basePrice * discountMultiplier
                        val courierFee = if (matchingSettings.deliveryChargeOn) matchingSettings.deliveryCharge else 0.0
                        val finalPrice = basePrice - savings + courierFee
 
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = LuxeCream),
                            border = BorderStroke(1.dp, Color(0xFFEAD8DC))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Boutique Drape Item Price:", fontSize = 11.sp, color = Color.Gray)
                                    Text("₹$basePrice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (savings > 0) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Coupon Savings (${appliedPromoCode!!.code}):", fontSize = 11.sp, color = Color(0xFF137333))
                                        Text("-₹${savings.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Delivery Courier Fee:", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = if (courierFee > 0) "₹${courierFee.toInt()}" else "FREE (Insured)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (courierFee > 0) Color.DarkGray else Color(0xFF137333)
                                    )
                                }
                                Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color.LightGray.copy(alpha = 0.5f))
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Final Amount Due:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                    Text("₹${finalPrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = LuxeBurgundy)
                                }
                            }
                        }
 
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                var validationOk = true
                                if (custName.trim().length < 3) {
                                    custNameError = "Please enter a valid name (at least 3 characters)"
                                    validationOk = false
                                } else {
                                    custNameError = null
                                }
                                
                                val digitsOnly = custPhone.filter { it.isDigit() }
                                if (digitsOnly.length < 10) {
                                    custPhoneError = "Please enter a valid 10-digit WhatsApp phone number"
                                    validationOk = false
                                } else {
                                    custPhoneError = null
                                }
                                
                                if (custAddress.trim().length < 8) {
                                    custAddressError = "Please enter a complete delivery address (at least 8 characters)"
                                    validationOk = false
                                } else {
                                    custAddressError = null
                                }

                                if (validationOk) {
                                    // Save state to SharedPreferences for auto-fill on future orders
                                    sharedPrefs.edit().apply {
                                        putString("saved_name", custName)
                                        putString("saved_phone", custPhone)
                                        putString("saved_address", custAddress)
                                        apply()
                                    }

                                    val orderCreated = repository.createCustomerOrder(
                                        customerName = custName.trim(),
                                        customerPhone = custPhone.trim(),
                                        customerAddress = custAddress.trim(),
                                        product = product,
                                        color = selectedColor,
                                        size = selectedSize,
                                        deliveryCharge = courierFee,
                                        isCod = isCodSelected,
                                        overridePrice = finalPrice
                                    )
                                    
                                    // Trigger real on-device intent redirection to WhatsApp
                                    try {
                                        val targetPhone = matchingStore?.ownerWhatsapp?.ifBlank { matchingStore?.ownerPhone } ?: "919833445566"
                                        val productLink = "myapp.com/product?storeId=${product.storeId}&productId=${product.id}"
                                        val payMethodText = if (isCodSelected) "Cash on Delivery (COD)" else "Order & Prepay via Chat"
                                        val messageText = """
Hello, I want to order this product.

Customer Name: ${custName.trim()}
Phone: ${custPhone.trim()}
Address: ${custAddress.trim()}

Product Name: ${product.name}
Size: $selectedSize
Color: $selectedColor
Price: ₹${finalPrice.toInt()}
Payment Method: $payMethodText

Product Link: $productLink
""".trimIndent()
                                        
                                        val intUri = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$targetPhone&text=${android.net.Uri.encode(messageText)}")
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, intUri)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {}

                                    showWhatsAppResult = orderCreated
                                    onPlaceOrder(orderCreated)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("confirm_whatsapp_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy)
                        ) {
                            Text("Confirm Booking & Open WhatsApp")
                        }
                    }
                }
            }
        }
    }

    // Simulated WhatsApp Message Dialog (Because emulator doesn't have real dynamic link routing, this shows exactly what's being sent)
    val waResult = showWhatsAppResult
    if (waResult != null) {
        val cleanMapLink = matchingStore?.addressMapLink ?: "Map not saved"

        Dialog(onDismissRequest = { showWhatsAppResult = null; showOrderForm = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE7FFDB)), // WhatsApp light green
                border = BorderStroke(1.5.dp, Color(0xFF25D366))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, "WA", tint = Color(0xFF25D366), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulated WhatsApp Form", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF128C7E))
                        }
                        IconButton(onClick = { showWhatsAppResult = null; showOrderForm = false }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    Divider(color = Color(0xFF25D366).copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "To Support Owner: ${matchingStore?.ownerName ?: "Designer"}\n" +
                               "WhatsApp Target No: ${matchingStore?.ownerPhone ?: "9198XXX"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(6.dp)).padding(12.dp)
                    ) {
                        Text(
                            text = "Hello, I want to order this product from TS LuxeWear platform!\n\n" +
                                   "Customer Name: ${waResult.customerName}\n" +
                                   "Phone Number: ${waResult.customerPhone}\n" +
                                   "Address: ${waResult.customerAddress}\n\n" +
                                   "Product Name: ${waResult.productName}\n" +
                                   "Fabric: ${product.fabric}\n" +
                                   "Size Chosen: ${waResult.productSize}\n" +
                                   "Shade Chosen: ${waResult.productColor}\n" +
                                   "Price: ₹${waResult.productPrice}\n" +
                                   "COD available: Yes\n" +
                                   "Platform Order Reference: ${waResult.orderId}\n" +
                                   "Product Map: $cleanMapLink",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF1E1E1E)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            showWhatsAppResult = null
                            showOrderForm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF128C7E)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send simulated text to ${waResult.storeName}")
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingCard(order: Order) {
    var expandedTimeline by remember { mutableStateOf(false) }

    val statusSteps = listOf("Pending", "Confirmed", "Packed", "Shipped", "Delivered")
    val currentStepIndex = statusSteps.indexOf(order.orderStatus)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEAE2E4)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Order ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Store: ${order.storeName}", fontSize = 11.sp, color = LuxeDustyRose, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier.background(
                        color = when (order.orderStatus) {
                            "Pending" -> Color(0xFFFFF9E6)
                            "Confirmed" -> Color(0xFFE6F4EA)
                            "Packed" -> Color(0xFFE8F0FE)
                            "Shipped" -> Color(0xFFF3E8FF)
                            "Delivered" -> Color(0xFFD4EDDA)
                            else -> Color(0xFFF8D7DA)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = when (order.orderStatus) {
                            "Pending" -> Color(0xFFB06000)
                            "Confirmed" -> Color(0xFF137333)
                            "Packed" -> Color(0xFF1A73E8)
                            "Shipped" -> Color(0xFF6B21A8)
                            "Delivered" -> Color(0xFF155724)
                            else -> Color(0xFF721C24)
                        }
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF2EBEB))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).background(LuxeCream, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(order.productImageUrl, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = order.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = "Size: ${order.productSize} | Shade: ${order.productColor}", fontSize = 11.sp, color = Color.Gray)
                    Text(text = "Price: ₹${order.productPrice} (COD COD)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                }
            }

            // Invoice display after Confirmation!
            if (order.invoiceId != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().background(LuxeLightGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp)).padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Platform Secured Invoice", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxeGold)
                            Text("Invoice No: ${order.invoiceId}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val context = LocalContext.current
                            OutlinedButton(
                                onClick = {
                                    com.example.data.PermissionManager.requestPermissionContext(
                                        com.example.data.LuxePermission.STORAGE,
                                        onGranted = {
                                            android.widget.Toast.makeText(context, "Storage Approved: Saved receipt '${order.invoiceId}.pdf' directly to Downloads folder 🎉", android.widget.Toast.LENGTH_LONG).show()
                                        },
                                        onDenied = {
                                            android.widget.Toast.makeText(context, "Storage Blocked: Invoice '${order.invoiceId}' could not be cached. Please grant storage access.", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    )
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("customer_pdf_invoice_${order.orderId}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxeBurgundy),
                                border = BorderStroke(1.dp, LuxeBurgundy)
                            ) {
                                Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("PDF", fontSize = 9.sp)
                            }

                            // Share on WhatsApp Button
                            OutlinedButton(
                                onClick = {
                                    val shareText = "TS LuxeWear Invoice Receipt:\nInvoice No: ${order.invoiceId}\nOrder ID: ${order.orderId}\nBoutique: ${order.storeName}\nProduct: ${order.productImageUrl} ${order.productName}\nAmount: ₹${order.productPrice}\nStatus: ${order.orderStatus}\nThank you for shopping on TS LuxeWear!"
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                            setPackage("com.whatsapp")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // WhatsApp not installed, fallback to standard share chooser
                                        val chooserIntent = android.content.Intent.createChooser(
                                            android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                            },
                                            "Share Invoice via"
                                        )
                                        context.startActivity(chooserIntent)
                                    }
                                    android.widget.Toast.makeText(context, "Sharing Invoice on WhatsApp... 📲", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("customer_whatsapp_invoice_${order.orderId}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                                border = BorderStroke(1.dp, Color(0xFF25D366))
                            ) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("WhatsApp", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable Timeline Toggle
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expandedTimeline = !expandedTimeline },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(if (expandedTimeline) "Collapse Live Tracking" else "View Live Tracking Timeline", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                Icon(
                    imageVector = if (expandedTimeline) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = LuxeBurgundy,
                    modifier = Modifier.size(14.dp)
                )
            }

            AnimatedVisibility(
                visible = expandedTimeline,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    statusSteps.forEachIndexed { idx, step ->
                        val isDone = idx <= currentStepIndex
                        val isCurrent = idx == currentStepIndex

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(20.dp).background(
                                        color = if (isDone) LuxeBurgundy else Color.LightGray,
                                        shape = CircleShape
                                    ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = step,
                                    fontSize = 12.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) LuxeBurgundy else if (isDone) Color.DarkGray else Color.LightGray
                                )
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(Current Status)", fontSize = 10.sp, color = LuxeGold, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            val historyEntry = order.statusHistory.firstOrNull { it.startsWith("$step:") }
                            val timeString = if (historyEntry != null) {
                                val parts = historyEntry.split(":")
                                if (parts.size >= 2) {
                                    val ts = parts[1].toLongOrNull()
                                    if (ts != null) {
                                        val sdf = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault())
                                        sdf.format(java.util.Date(ts))
                                    } else null
                                } else null
                            } else null
                            
                            if (timeString != null) {
                                Text(text = timeString, fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InquiryChatCard(inq: com.example.model.Inquiry) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEFE6E8))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(inq.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Store owner: ${inq.storeName}", fontSize = 10.sp, color = Color.Gray)
                }

                Box(
                    modifier = Modifier.background(
                        color = if (inq.status == "Resolved") Color(0xFFD4EDDA) else Color(0xFFFFF3CD),
                        shape = RoundedCornerShape(4.dp)
                    ).padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (inq.status == "Resolved") "RESOLVED" else "PENDING OWNERS REPLY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (inq.status == "Resolved") Color(0xFF155724) else Color(0xFF856404)
                    )
                }
            }
            Divider(color = Color(0xFFF9F1F2), modifier = Modifier.padding(vertical = 8.dp))

            // User's Question Bubble
            Box(
                modifier = Modifier.fillMaxWidth(0.9f).background(LuxeCream, RoundedCornerShape(8.dp)).padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "My Inquiry Question:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                        if (com.example.data.MessageEncryption.isShielded(inq.question)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = "Encrypted", tint = LuxeGold, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Shielded", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = LuxeGold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = com.example.data.MessageEncryption.decrypt(inq.question), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Designers Answer Bubble
            if (inq.answer != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.End).background(LuxeLightGold, RoundedCornerShape(8.dp)).padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Designer Answer (${inq.storeName}):", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = LuxeGold)
                            if (com.example.data.MessageEncryption.isShielded(inq.answer)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = "Encrypted", tint = LuxeBurgundy, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Shielded", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = LuxeBurgundy)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = com.example.data.MessageEncryption.decrypt(inq.answer), fontSize = 12.sp, color = LuxeBurgundy)
                    }
                }
            } else {
                Text(
                    text = "💬 Designer is offline. You'll get notified here once answered.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStatePlaceholder(message: String, hint: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEBE3E4))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(48.dp), tint = LuxeDustyRose.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = message, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = LuxeBurgundy)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = hint, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CustomerProfileAndAlertsSettingsScreen(repository: TSLuxeWearRepository) {
    var avatarSymbol by remember { mutableStateOf("👩") }
    var orderAlertsEnabled by remember { mutableStateOf(true) }
    var productAlertsEnabled by remember { mutableStateOf(false) }
    var promoAlertsEnabled by remember { mutableStateOf(false) }
    var storeAlertsEnabled by remember { mutableStateOf(true) }
    var inquiryAlertsEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEBE3E4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TS LuxeWear Client Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LuxeBurgundy
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(LuxeCream, CircleShape)
                            .border(1.5.dp, LuxeGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(avatarSymbol, fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "shakirsir2122@gmail.com",
                        fontWeight = FontWeight.SemiBold,
                        color = LuxeBurgundy,
                        fontSize = 13.sp
                    )
                    Text("Standard Customer account", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.CAMERA,
                                    onGranted = {
                                        avatarSymbol = "🤳"
                                        android.widget.Toast.makeText(context, "Profile camera access enabled: Updated avatar successfully! 🙌", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    onDenied = {
                                        // Still allow user to select galleries/fallbacks
                                        com.example.data.PermissionManager.requestPermissionContext(
                                            com.example.data.LuxePermission.GALLERY,
                                            onGranted = {
                                                avatarSymbol = "🦄"
                                                android.widget.Toast.makeText(context, "Profile gallery loaded successfully! 🖼️", android.widget.Toast.LENGTH_SHORT).show()
                                            },
                                            onDenied = {
                                                android.widget.Toast.makeText(context, "Both options disabled. Profile remains unchanged.", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("Use Camera", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.GALLERY,
                                    onGranted = {
                                        avatarSymbol = "🎨"
                                        android.widget.Toast.makeText(context, "Updated avatar from library successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    onDenied = {
                                        android.widget.Toast.makeText(context, "Gallery selection denied.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxeLightGold, contentColor = LuxeBurgundy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("Use Gallery", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEBE3E4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notification & Alert Preferences",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = LuxeBurgundy
                    )
                    Text(
                        text = "TS LuxeWear respects your privacy. Alerts are secure, zero spam, and fully customisable.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // 1. Alerts for orders
                    NotificationPrefSwitchRow(
                        label = "Order Dispatch Alerts",
                        description = "Notify me when my custom drapes are shipped, packed or delivered.",
                        checked = orderAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.NOTIFICATION,
                                    onGranted = { orderAlertsEnabled = true },
                                    onDenied = { orderAlertsEnabled = false }
                                )
                            } else {
                                orderAlertsEnabled = false
                            }
                        }
                    )

                    // 2. Product updates
                    NotificationPrefSwitchRow(
                        label = "New Design Collection Updates",
                        description = "Notify me when boutiques launch new catalog weaves, saree drapes, or kurtas.",
                        checked = productAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.NOTIFICATION,
                                    onGranted = { productAlertsEnabled = true },
                                    onDenied = { productAlertsEnabled = false }
                                )
                            } else {
                                productAlertsEnabled = false
                            }
                        }
                    )

                    // 3. Promotional notifications
                    NotificationPrefSwitchRow(
                        label = "Promotional Offers & Festivals",
                        description = "Notify me when boutique owners run seasonal discount offers.",
                        checked = promoAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.NOTIFICATION,
                                    onGranted = { promoAlertsEnabled = true },
                                    onDenied = { promoAlertsEnabled = false }
                                )
                            } else {
                                promoAlertsEnabled = false
                            }
                        }
                    )

                    // 4. Store updates
                    NotificationPrefSwitchRow(
                        label = "Followed Store Announcements",
                        description = "Important news and event notices from designer weavers you follow.",
                        checked = storeAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.NOTIFICATION,
                                    onGranted = { storeAlertsEnabled = true },
                                    onDenied = { storeAlertsEnabled = false }
                                )
                            } else {
                                storeAlertsEnabled = false
                            }
                        }
                    )

                    // 5. Inquiry reply notifications
                    NotificationPrefSwitchRow(
                        label = "Styling Inquiry Replies",
                        description = "Trigger push updates immediately when designers answer your loom size questions.",
                        checked = inquiryAlertsEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                com.example.data.PermissionManager.requestPermissionContext(
                                    com.example.data.LuxePermission.NOTIFICATION,
                                    onGranted = { inquiryAlertsEnabled = true },
                                    onDenied = { inquiryAlertsEnabled = false }
                                )
                            } else {
                                inquiryAlertsEnabled = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationPrefSwitchRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LuxeBurgundy
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = LuxeBurgundy,
                    uncheckedThumbColor = Color.LightGray
                )
            )
        }
        Text(
            text = description,
            fontSize = 10.sp,
            color = Color.Gray,
            lineHeight = 14.sp,
            modifier = Modifier.padding(end = 48.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
    }
}

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.R
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.components.AuthGateCard
import com.example.ui.components.JournalCoverInspectionDialog
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WaveSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Curated stationery inventory ensuring immediate, rich display of pens, notebooks,
 * washi tapes, bookmarks, and refills across offline or unseeded states.
 */
val defaultStationeryProducts = listOf(
    Product(
        id = "prod_journal_abiola_blue",
        title = "Write Your Wave Journal (Signature Ocean Blue)",
        slug = "write-your-wave-journal-signature-blue",
        category = "Journals",
        description = "Signature spiral wire-bound edition featuring student creator Abiola. Structured guided daily prompts for Study, Career, Financial Freedom, Self Care, and Success with gold foil crown lettering.",
        price = 5.0,
        currency = "USD",
        sku = "WYW-JOUR-01",
        coverImageUrl = "android.resource://com.example/" + R.drawable.journal_cover_abiola_blue,
        isPublished = true,
        inStock = true,
        stockQuantity = 100L,
        features = listOf(
            "Spiral Double-Wire Bound Spine",
            "5 Life Pillars Alignment System",
            "120 GSM Acid-Free Bleed-Proof Paper",
            "Unique Serial Verification Number",
            "Manufactured at WIT Sinkor Production Hub"
        )
    ),
    Product(
        id = "prod_journal_purple_gold",
        title = "Write Your Wave Journal (Royal Purple & Gold)",
        slug = "write-your-wave-journal-purple-gold",
        category = "Journals",
        description = "A Journey to the Life I Want. Inspiring gold crown calligraphy with daily morning intentionality sections and evening gratitude reflections.",
        price = 5.0,
        currency = "USD",
        sku = "WYW-JOUR-02",
        coverImageUrl = "android.resource://com.example/" + R.drawable.journal_cover_purple_gold,
        isPublished = true,
        inStock = true,
        stockQuantity = 100L,
        features = listOf(
            "Royal Purple & Gold Foil Stamped",
            "Study & Career Roadmap Prompts",
            "120 GSM Acid-Free Archival Paper",
            "Unique Serial Verification Number",
            "Manufactured at WIT Sinkor Production Hub"
        )
    ),
    Product(
        id = "prod_journal_noir_gold",
        title = "Write Your Wave Journal (Noir Gold Class of 2026)",
        slug = "write-your-wave-journal-noir-gold",
        category = "Journals",
        description = "Focus. Learn. Level Up. Big Dreams Require Discipline. Premium noir black and gold foil edition designed for academic sprinting and long-term milestones.",
        price = 5.0,
        currency = "USD",
        sku = "WYW-JOUR-03",
        coverImageUrl = "android.resource://com.example/" + R.drawable.journal_cover_noir_gold,
        isPublished = true,
        inStock = true,
        stockQuantity = 100L,
        features = listOf(
            "Luxury Black Noir & Metallic Gold",
            "Plan, Study, Grow & Succeed Trackers",
            "120 GSM Bleed-Proof Heavyweight Paper",
            "Unique Serial Verification Number",
            "Manufactured at WIT Sinkor Production Hub"
        )
    ),
    Product(
        id = "prod_journal_radiant_pink",
        title = "Write Your Wave Journal (Radiant Pink & Purple)",
        slug = "write-your-wave-journal-radiant-pink",
        category = "Journals",
        description = "Dream. Plan. Achieve. Repeat. Vibrant brush-radiance cover with dedicated personalized name slot and creative vision board spreads.",
        price = 5.0,
        currency = "USD",
        sku = "WYW-JOUR-04",
        coverImageUrl = "android.resource://com.example/" + R.drawable.journal_cover_radiant_pink,
        isPublished = true,
        inStock = true,
        stockQuantity = 100L,
        features = listOf(
            "Personalized Nameplate Slot",
            "Dream, Plan, Achieve Daily Spreads",
            "Durable Wire-O Binding",
            "Unique Serial Verification Number",
            "Manufactured at WIT Sinkor Production Hub"
        )
    )
)

/**
 * StationeryProductListScreen provides a responsive 2-column (or adaptive) LazyVerticalGrid
 * displaying official Write Your Wave journals ($5.00 USD) with imagery loaded via Coil (AsyncImage),
 * interactive 'Add to Cart' buttons with dynamic animation feedback, search filtering,
 * and an interactive Shopping Cart drawer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationeryProductListScreen(
    products: List<Product>,
    isLoading: Boolean = false,
    cartItems: List<CartItem> = emptyList(),
    onAddToCart: (Product) -> Unit = {},
    onUpdateCartQuantity: (Product, Int) -> Unit = { _, _ -> },
    onClearCart: () -> Unit = {},
    onCheckoutCart: suspend (items: List<CartItem>, name: String, email: String, phone: String?, address: String) -> Result<Order>,
    onNavigateToCustomizeWithProduct: (Product) -> Unit = {},
    isSignedIn: Boolean = false,
    currentUserEmail: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Journals") }
    var showCartSheet by remember { mutableStateOf(false) }
    var selectedProductForInspection by remember { mutableStateOf<Product?>(null) }
    var orderSuccessResult by remember { mutableStateOf<Order?>(null) }

    // Merge supplied products with default journal catalog - strictly journals only, $5.00 USD
    val combinedProducts = remember(products) {
        val list = products
            .filter { it.category.contains("Journal", ignoreCase = true) || it.id.startsWith("prod_journal") }
            .toMutableList()
        val existingIds = list.map { it.id }.toSet()
        for (defaultP in defaultStationeryProducts) {
            if (!existingIds.contains(defaultP.id)) {
                list.add(defaultP)
            }
        }
        // Strict $5.00 USD pricing for all official journals
        list.map { it.copy(price = 5.0) }
    }

    val categories = listOf(
        "All Journals",
        "Ocean Blue",
        "Purple & Gold",
        "Noir Gold",
        "Radiant Pink"
    )

    val filteredProducts = combinedProducts.filter { prod ->
        val matchesCategory = when (selectedCategory) {
            "All Journals", "All" -> true
            "Ocean Blue" -> prod.title.contains("Blue", ignoreCase = true) || prod.title.contains("Ocean", ignoreCase = true)
            "Purple & Gold" -> prod.title.contains("Purple", ignoreCase = true)
            "Noir Gold" -> prod.title.contains("Noir", ignoreCase = true) || prod.title.contains("Black", ignoreCase = true)
            "Radiant Pink" -> prod.title.contains("Pink", ignoreCase = true) || prod.title.contains("Radiant", ignoreCase = true)
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                prod.title.contains(searchQuery, ignoreCase = true) ||
                prod.description.contains(searchQuery, ignoreCase = true) ||
                prod.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    // Keep track of which products were recently added for visual confirmation button animation
    val recentlyAddedMap = remember { mutableStateMapOf<String, Boolean>() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("stationery_product_list_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- 1. Dedicated Search Bar at Top of Product Screen ---
        Surface(
            color = WaveNavyDark,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("product_search_bar_header")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header Row with Title, Official Logo Badge, and Bag Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, WaveGold),
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wyw_official_logo),
                                contentDescription = "Write Your Wave Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = WaveGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "WRITE YOUR WAVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WaveGoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Stationery & Tools",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Cart Action Button in Header
                    IconButton(
                        onClick = { showCartSheet = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(WaveNavyLight)
                            .testTag("header_cart_button")
                    ) {
                        val totalCount = cartItems.sumOf { it.quantity }
                        if (totalCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = WaveGold,
                                        contentColor = WaveNavyDark
                                    ) {
                                        Text("$totalCount", fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Shopping Cart with $totalCount items",
                                    tint = Color.White
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Empty Shopping Cart",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The Search Bar (Text State Filter for Name or Category)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Filter journal editions (e.g. Ocean Blue, Purple & Gold, Noir)...",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search Stationery",
                            tint = WaveGold,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.testTag("clear_search_button")
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WaveNavy,
                        unfocusedContainerColor = WaveNavy,
                        focusedBorderColor = WaveGold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_search_bar")
                        .testTag("product_search_input")
                        .testTag("stationery_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WaveGold,
                                selectedLabelColor = WaveNavyDark,
                                containerColor = WaveNavy,
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) WaveGold else Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("stationery_chip_${cat.replace(" ", "_")}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Production & Serial Verification Info Banner
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.8.dp, WaveGold.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = WaveGold,
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("$5", color = WaveNavyDark, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "All Journal Editions • $5.00 USD",
                                color = WaveGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Powered & Produced at Waterloop Institute of Technology (WIT), Airfield Shortcut, Sinkor. Each journal receives a unique series verification number after production.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 9.5.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // --- Active Filter Summary Indicator ---
        if (searchQuery.isNotBlank() || (selectedCategory != "All" && selectedCategory != "All Journals")) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE2E8F0))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        append("Filter: ")
                        if (searchQuery.isNotBlank()) append("\"$searchQuery\" ")
                        if (selectedCategory != "All") append("in $selectedCategory ")
                        append("(${filteredProducts.size} found)")
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WaveNavyDark
                )
                Text(
                    text = "Clear filter",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626),
                    modifier = Modifier
                        .clickable {
                            searchQuery = ""
                            selectedCategory = "All"
                        }
                        .padding(4.dp)
                        .testTag("reset_search_filter_text")
                )
            }
        }

        // --- 2. Product Grid & Results Area ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("stationery_lazy_vertical_grid")
            ) {
                // Counter Bar
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredProducts.size} Stationery Items Available",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        if (cartItems.isNotEmpty()) {
                            Text(
                                text = "${cartItems.sumOf { it.quantity }} in Bag",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = WaveNavy
                            )
                        }
                    }
                }

                // Loading State
                if (isLoading && combinedProducts.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = WaveNavy)
                        }
                    }
                } else if (filteredProducts.isEmpty()) {
                    // Empty Search Results State
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🖋️", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No stationery items match",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = WaveNavyDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank())
                                        "No products matching \"$searchQuery\" in category \"$selectedCategory\"."
                                    else
                                        "Try adjusting your search query or selecting a different category.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        searchQuery = ""
                                        selectedCategory = "All"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("clear_filter_button")
                                ) {
                                    Text("Reset Filters & Show All", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Product Grid Cards
                    items(
                        items = filteredProducts,
                        key = { it.id }
                    ) { product ->
                        val isRecentlyAdded = recentlyAddedMap[product.id] == true

                        StationeryProductCard(
                            product = product,
                            isRecentlyAdded = isRecentlyAdded,
                            onAddToCart = {
                                onAddToCart(product)
                                recentlyAddedMap[product.id] = true
                                coroutineScope.launch {
                                    delay(1200)
                                    recentlyAddedMap[product.id] = false
                                }
                                Toast.makeText(context, "Added ${product.title} to Cart", Toast.LENGTH_SHORT).show()
                            },
                            onProductClick = {
                                selectedProductForInspection = product
                            },
                            onCustomizeClick = {
                                onNavigateToCustomizeWithProduct(product)
                            }
                        )
                    }
                }
            }
        }
    }

        // --- Persistent Floating Cart Bar when Cart is Not Empty ---
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            val totalItems = cartItems.sumOf { it.quantity }
            val subtotal = cartItems.sumOf { it.totalPrice }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WaveNavyDark),
                border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCartSheet = true }
                    .testTag("floating_cart_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(WaveGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart Icon",
                                tint = WaveNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "$totalItems ${if (totalItems == 1) "item" else "items"} in Bag",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$${String.format("%.2f", subtotal)} USD",
                                color = WaveGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showCartSheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WaveGold,
                            contentColor = WaveNavyDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("view_cart_button")
                    ) {
                        Text(
                            text = "View Bag",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // --- Product Detail Inspection Dialog ---
    selectedProductForInspection?.let { product ->
        StationeryDetailDialog(
            product = product,
            onDismiss = { selectedProductForInspection = null },
            onAddToCart = {
                onAddToCart(product)
                Toast.makeText(context, "Added ${product.title} to Cart", Toast.LENGTH_SHORT).show()
                selectedProductForInspection = null
            },
            onCustomize = {
                selectedProductForInspection = null
                onNavigateToCustomizeWithProduct(product)
            }
        )
    }

    // --- Shopping Cart Bottom Sheet ---
    if (showCartSheet) {
        StationeryCartBottomSheet(
            cartItems = cartItems,
            isSignedIn = isSignedIn,
            userEmail = currentUserEmail,
            onDismiss = { showCartSheet = false },
            onUpdateQuantity = onUpdateCartQuantity,
            onClearCart = onClearCart,
            onCheckout = { name, email, phone, address ->
                coroutineScope.launch {
                    val result = onCheckoutCart(cartItems, name, email, phone, address)
                    result.onSuccess { order ->
                        showCartSheet = false
                        orderSuccessResult = order
                        onClearCart()
                    }.onFailure { err ->
                        Toast.makeText(context, "Checkout error: ${err.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    // --- Order Placed Confirmation Dialog ---
    orderSuccessResult?.let { order ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { orderSuccessResult = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("stationery_order_success_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(WaveSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Order Successful",
                            tint = WaveSuccess,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Stationery Order Confirmed!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your order reference number:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = WaveNavyDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = order.orderReference,
                            color = WaveGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Total: $${String.format("%.2f", order.totalPrice)} ${order.currency}\nStatus: ${order.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WaveNavy,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { orderSuccessResult = null },
                        colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("close_stationery_order_dialog")
                    ) {
                        Text("Continue Exploring", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Individual stationery item card in the LazyVerticalGrid.
 * Displays Coil-loaded imagery, category badge, title, pricing, and an interactive 'Add to Cart' button.
 */
@Composable
fun StationeryProductCard(
    product: Product,
    isRecentlyAdded: Boolean,
    onAddToCart: () -> Unit,
    onProductClick: () -> Unit,
    onCustomizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonBgColor by animateColorAsState(
        targetValue = if (isRecentlyAdded) WaveSuccess else WaveNavy,
        label = "btn_bg_anim"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("stationery_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // --- Product Image Loaded via Coil (AsyncImage) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .background(Color(0xFFF1F5F9))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.resolvedImageUrl())
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(product.resolvedFrontCoverRes()),
                    error = painterResource(product.resolvedFrontCoverRes()),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .testTag("product_image_${product.id}")
                )

                // Category Tag overlay on image
                Surface(
                    color = WaveNavyDark.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = product.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WaveGold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                // In-Stock Indicator Tag
                Surface(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (product.inStock) WaveSuccess else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (product.inStock) "In Stock" else "Pre-order",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // --- Product Metadata ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Product Title
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = WaveNavyDark,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                    modifier = Modifier.height(34.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Short Features Preview
                val featurePreview = product.features.firstOrNull() ?: product.sku
                Text(
                    text = featurePreview,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Product Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRICE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "$${String.format("%.2f", product.price)} ${product.currency}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = WaveNavy,
                            fontSize = 15.sp,
                            modifier = Modifier.testTag("price_tag_${product.id}")
                        )
                    }

                    // Rating Star
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = WaveGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "4.9",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- Add to Cart Button ---
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBgColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_to_cart_${product.id}")
                ) {
                    if (isRecentlyAdded) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Added",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Added!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = WaveGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add to Cart",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Inspection dialog giving full product specs, image view, and actions.
 */
@Composable
fun StationeryDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit,
    onCustomize: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("stationery_detail_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = WaveNavyDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = product.category,
                            color = WaveGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close dialog")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Large Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.resolvedImageUrl())
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(product.resolvedFrontCoverRes()),
                        error = painterResource(product.resolvedFrontCoverRes()),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = WaveNavyDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%.2f", product.price)} ${product.currency}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = WaveNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                if (product.features.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Craft Specifications:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    product.features.forEach { feat ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(WaveGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = feat, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (product.category.contains("Journal", ignoreCase = true) || product.category.contains("Notebook", ignoreCase = true)) {
                        OutlinedButton(
                            onClick = onCustomize,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, WaveNavy),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Customize", color = WaveNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_dialog_add_to_cart")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Add", tint = WaveGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Interactive Shopping Bag Bottom Sheet with quantity adjustment,
 * subtotal calculator, and integrated guest/user checkout flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationeryCartBottomSheet(
    cartItems: List<CartItem>,
    isSignedIn: Boolean,
    userEmail: String?,
    onDismiss: () -> Unit,
    onUpdateQuantity: (Product, Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: (name: String, email: String, phone: String?, address: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isCheckingOut by remember { mutableStateOf(false) }

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf(userEmail ?: "") }
    var phoneInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val shipping = if (subtotal > 35.0 || subtotal == 0.0) 0.0 else 4.50
    val totalAmount = subtotal + shipping

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("stationery_cart_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = WaveNavy,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your Stationery Bag",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                }

                if (cartItems.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        fontSize = 12.sp,
                        color = Color.Red.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onClearCart() }
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🛍️", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your shopping bag is empty",
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add journals, pens, or washi tape from the stationery grid.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Cart Items List
                Column(modifier = Modifier.fillMaxWidth()) {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.product.resolvedImageUrl())
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(item.product.resolvedFrontCoverRes()),
                                error = painterResource(item.product.resolvedFrontCoverRes()),
                                contentDescription = item.product.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = WaveNavyDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$${String.format("%.2f", item.product.price)} each",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            // Stepper (- qty +)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.product, item.quantity - 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = if (item.quantity == 1) Color.Red else WaveNavyDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "${item.quantity}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = WaveNavyDark,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { onUpdateQuantity(item.product, item.quantity + 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = WaveNavyDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "$${String.format("%.2f", item.totalPrice)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = WaveNavy
                            )
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Summary
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", fontSize = 12.sp, color = Color.Gray)
                            Text("$${String.format("%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = if (shipping == 0.0) "FREE" else "$${String.format("%.2f", shipping)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (shipping == 0.0) WaveSuccess else WaveNavyDark
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WaveNavyDark)
                            Text(
                                text = "$${String.format("%.2f", totalAmount)} USD",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = WaveNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isCheckingOut) {
                    Button(
                        onClick = { isCheckingOut = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("proceed_to_checkout_button")
                    ) {
                        Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    // Delivery Details Form
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Delivery & Customer Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Your Full Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address for Order Confirmation") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_email_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number (Optional)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            label = { Text("Delivery Address / Campus Hall") },
                            maxLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val validName = if (nameInput.isNotBlank()) nameInput else "Valued Wave Maker"
                                val validEmail = if (emailInput.isNotBlank()) emailInput else (userEmail ?: "guest@writeyourwave.com")
                                val validAddress = if (addressInput.isNotBlank()) addressInput else "Standard Campus / Postal Delivery"
                                onCheckout(validName, validEmail, phoneInput.ifBlank { null }, validAddress)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("confirm_place_order_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = WaveGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirm & Place Order ($${String.format("%.2f", totalAmount)})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

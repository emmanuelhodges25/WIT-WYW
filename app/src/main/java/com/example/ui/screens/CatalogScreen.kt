package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.components.JournalCoverInspectionDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.components.AuthGateCard
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WaveSuccess
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    products: List<Product>,
    isLoading: Boolean,
    isSignedIn: Boolean,
    currentUserEmail: String?,
    onPlaceOrder: suspend (productId: String, title: String, price: Double, qty: Long, name: String, email: String, phone: String?, address: String, customDetails: String?) -> Result<Order>,
    onNavigateToCustomizeWithProduct: (Product) -> Unit,
    cartItems: List<com.example.data.model.CartItem> = emptyList(),
    onAddToCart: (Product) -> Unit = {},
    onUpdateCartQuantity: (Product, Int) -> Unit = { _, _ -> },
    onClearCart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf("GRID") } // "GRID" or "EDITORIAL"

    if (viewMode == "GRID") {
        StationeryProductListScreen(
            products = products,
            isLoading = isLoading,
            cartItems = cartItems,
            onAddToCart = onAddToCart,
            onUpdateCartQuantity = onUpdateCartQuantity,
            onClearCart = onClearCart,
            onCheckoutCart = { items, name, email, phone, addr ->
                var lastOrder: Order? = null
                var errorResult: Result<Order>? = null
                for (item in items) {
                    val res = onPlaceOrder(
                        item.product.id,
                        item.product.title,
                        item.product.price,
                        item.quantity.toLong(),
                        name,
                        email,
                        phone,
                        addr,
                        "Order from Stationery Bag: ${item.quantity}x ${item.product.title}"
                    )
                    if (res.isSuccess) {
                        lastOrder = res.getOrNull()
                    } else {
                        errorResult = res
                    }
                }
                if (lastOrder != null) Result.success(lastOrder)
                else errorResult ?: Result.failure(Exception("Could not place order"))
            },
            onNavigateToCustomizeWithProduct = onNavigateToCustomizeWithProduct,
            isSignedIn = isSignedIn,
            currentUserEmail = currentUserEmail,
            modifier = modifier
        )
        return
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedProductForOrder by remember { mutableStateOf<Product?>(null) }
    var orderSuccessResult by remember { mutableStateOf<Order?>(null) }

    val categories = listOf(
        "All",
        "University Student Notebook",
        "Student Notebook",
        "Journal",
        "Premium Journal"
    )

    val filteredProducts = products.filter { prod ->
        val matchesCategory = (selectedCategory == "All") || prod.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                prod.title.contains(searchQuery, ignoreCase = true) ||
                prod.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("catalog_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- Header & Search ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WaveNavyDark)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Product Catalog & Collections",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "High-grade archival paper, structured guided layouts & personalizations",
                    style = MaterialTheme.typography.bodySmall,
                    color = WaveGold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search notebooks, journals, SKUs...", fontSize = 13.sp, color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WaveGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WaveNavy,
                        unfocusedContainerColor = WaveNavy,
                        focusedBorderColor = WaveGold,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input")
                )
            }
        }

        // --- Category Filter Chips ---
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
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
                            selectedContainerColor = WaveNavy,
                            selectedLabelColor = WaveGold,
                            containerColor = Color.White,
                            labelColor = WaveNavyDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) WaveNavy else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("category_chip_${cat.replace(" ", "_")}")
                    )
                }
            }
        }

        // --- Result count & Empty states ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} Products Available",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = WaveNavy)
                }
            }
        } else if (filteredProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📚", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching stationery found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try adjusting your search query or selecting a different category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredProducts) { product ->
                ProductCatalogItem(
                    product = product,
                    onOrderClick = { selectedProductForOrder = product },
                    onCustomizeClick = { onNavigateToCustomizeWithProduct(product) }
                )
            }
        }
    }

    // --- Order Checkout Bottom Sheet ---
    selectedProductForOrder?.let { product ->
        OrderBottomSheet(
            product = product,
            isSignedIn = isSignedIn,
            userEmail = currentUserEmail,
            onDismiss = { selectedProductForOrder = null },
            onConfirmOrder = { qty, name, email, phone, addr, notes ->
                onPlaceOrder(
                    product.id,
                    product.title,
                    product.price,
                    qty,
                    name,
                    email,
                    phone,
                    addr,
                    notes
                )
            },
            onOrderSuccess = { order ->
                selectedProductForOrder = null
                orderSuccessResult = order
            }
        )
    }

    // --- Order Success Dialog ---
    orderSuccessResult?.let { order ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { orderSuccessResult = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("order_success_dialog")
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
                            contentDescription = "Success",
                            tint = WaveSuccess,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Order Placed Successfully!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your unique order reference is:",
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
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Item: ${order.productTitle} (Qty: ${order.quantity})\nTotal: $${String.format("%.2f", order.totalPrice)} ${order.currency}\nStatus: ${order.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WaveNavy,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { orderSuccessResult = null },
                        colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("close_order_success_button")
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCatalogItem(
    product: Product,
    onOrderClick: () -> Unit,
    onCustomizeClick: () -> Unit
) {
    var showBackCover by remember { mutableStateOf(false) }
    var showCoverDialog by remember { mutableStateOf(false) }

    if (showCoverDialog) {
        JournalCoverInspectionDialog(
            initialIsBackCover = showBackCover,
            onDismiss = { showCoverDialog = false }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("product_item_${product.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Authentic Cover Flip Card Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showCoverDialog = true }
                    .testTag("catalog_cover_preview_${product.id}")
            ) {
                Image(
                    painter = painterResource(
                        id = if (showBackCover) product.resolvedBackCoverRes() else product.resolvedFrontCoverRes()
                    ),
                    contentDescription = if (showBackCover) "Back Cover" else "Front Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Side pill badge & flip trigger
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(20.dp))
                        .clickable { showBackCover = !showBackCover }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Switch cover side",
                        tint = WaveGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showBackCover) "Back (2026/27)" else "Front Cover",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tap to zoom hint
                Surface(
                    color = WaveNavyDark.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "🔍 Tap to inspect",
                        color = WaveGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = WaveGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = product.category.uppercase(),
                            color = WaveNavyDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                }

                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = WaveNavyDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 19.sp
            )

            if (product.features.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    product.features.take(3).forEach { feature ->
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "✓ $feature",
                                fontSize = 10.sp,
                                color = WaveNavyLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCustomizeClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaveNavy),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("customize_product_button_${product.id}")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Personalize", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOrderClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveNavy,
                        contentColor = WaveGold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("order_product_button_${product.id}")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Order Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    product: Product,
    isSignedIn: Boolean,
    userEmail: String?,
    onDismiss: () -> Unit,
    onConfirmOrder: suspend (qty: Long, name: String, email: String, phone: String?, addr: String, notes: String?) -> Result<Order>,
    onOrderSuccess: (Order) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var quantity by remember { mutableStateOf(1L) }
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf(userEmail ?: "") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var customizationNotes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("order_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Place Order",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WaveGold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cover Preview Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WaveNavyDark, RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = product.resolvedFrontCoverRes()),
                    contentDescription = "Front Cover",
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = product.resolvedBackCoverRes()),
                    contentDescription = "Back Cover",
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Authentic Hardcover Edition", color = WaveGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Includes 2026/27 Calendar & Liberia holidays on back", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pricing & Quantity
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Unit Price: $${String.format("%.2f", product.price)}", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Total: $${String.format("%.2f", product.price * quantity)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = WaveNavyDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = "$quantity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }

            if (!isSignedIn) {
                Spacer(modifier = Modifier.height(16.dp))
                AuthGateCard(
                    title = "Sign in to Order",
                    subtitle = "Sign in with Google to save orders securely to Firestore and receive tracking updates."
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("order_customer_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerEmail,
                    onValueChange = { customerEmail = it },
                    label = { Text("Email Address *") },
                    modifier = Modifier.fillMaxWidth().testTag("order_customer_email_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Phone / WhatsApp (Optional)") },
                    modifier = Modifier.fillMaxWidth().testTag("order_customer_phone_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Delivery Address / Campus Dorm *") },
                    modifier = Modifier.fillMaxWidth().testTag("order_delivery_address_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customizationNotes,
                    onValueChange = { customizationNotes = it },
                    label = { Text("Customization Notes (Name on cover, quote, etc.)") },
                    modifier = Modifier.fillMaxWidth().testTag("order_customization_notes_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (customerName.isBlank() || customerEmail.isBlank() || deliveryAddress.isBlank()) {
                            errorMessage = "Please fill in all required fields (Name, Email, Delivery Address)."
                            return@Button
                        }
                        isSubmitting = true
                        errorMessage = null
                        coroutineScope.launch {
                            val res = onConfirmOrder(
                                quantity,
                                customerName.trim(),
                                customerEmail.trim(),
                                customerPhone.trim().ifEmpty { null },
                                deliveryAddress.trim(),
                                customizationNotes.trim().ifEmpty { null }
                            )
                            isSubmitting = false
                            if (res.isSuccess) {
                                onOrderSuccess(res.getOrThrow())
                            } else {
                                errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Failed to place order"
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveNavy,
                        contentColor = WaveGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_order_confirm_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Confirm & Place Order ($${String.format("%.2f", product.price * quantity)})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

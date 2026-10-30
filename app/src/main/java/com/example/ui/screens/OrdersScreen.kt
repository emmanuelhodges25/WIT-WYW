package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.components.AuthGateCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WaveSuccess
import kotlinx.coroutines.launch

@Composable
fun OrdersScreen(
    orders: List<Order>,
    isSignedIn: Boolean,
    onLookupReference: suspend (String) -> Order?,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(if (isSignedIn) 0 else 1) }
    var searchReference by remember { mutableStateOf("") }
    var lookupResult by remember { mutableStateOf<Order?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("orders_screen_root")
    ) {
        // --- Header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WaveNavyDark)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Order Management & Tracking",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Track your dispatched notebooks and review fulfillment status.",
                style = MaterialTheme.typography.bodySmall,
                color = WaveGold
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = WaveNavy,
            contentColor = WaveGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = WaveGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("My Account Orders", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                modifier = Modifier.testTag("my_orders_tab")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Track by Reference", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                modifier = Modifier.testTag("track_reference_tab")
            )
        }

        if (selectedTab == 0) {
            if (!isSignedIn) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    AuthGateCard(
                        title = "Sign in to View Order History",
                        subtitle = "Access all your orders and shipments synchronized across your account."
                    )
                }
            } else if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📦", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No orders have been received yet.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Explore the catalog to order your first student notebook or signature reflection journal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(orders) { order ->
                        OrderCardItem(order = order)
                    }
                }
            }
        } else {
            // Track by Reference Tab
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Secure Public Order Lookup",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter your reference number received at checkout (e.g. WYW-2026-000101):",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = searchReference,
                                onValueChange = {
                                    searchReference = it.uppercase()
                                    searchError = null
                                },
                                placeholder = { Text("WYW-2026-XXXXXX") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("order_lookup_input")
                            )

                            if (searchError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = searchError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (searchReference.isBlank()) {
                                        searchError = "Please enter an order reference number."
                                        return@Button
                                    }
                                    isSearching = true
                                    searchError = null
                                    lookupResult = null
                                    coroutineScope.launch {
                                        val found = onLookupReference(searchReference.trim())
                                        isSearching = false
                                        if (found != null) {
                                            lookupResult = found
                                        } else {
                                            searchError = "No matching order found for reference '$searchReference'."
                                        }
                                    }
                                },
                                enabled = !isSearching,
                                colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("lookup_order_button")
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Track Shipment", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                lookupResult?.let { resultOrder ->
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SEARCH RESULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = WaveNavyLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OrderCardItem(order = resultOrder)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(order: Order) {
    val statusSteps = listOf("PENDING", "CONFIRMED", "PROCESSING", "READY", "OUT_FOR_DELIVERY", "COMPLETED")
    val currentIndex = statusSteps.indexOf(order.status.uppercase()).coerceAtLeast(0)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderReference}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderReference,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = WaveNavyDark
                    )
                    Text(
                        text = "Placed for: ${order.customerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = order.paymentStatus)
                    StatusBadge(status = order.status)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order items info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WaveNavyDark
                    )
                    Text(
                        text = "Quantity: ${order.quantity} | Delivery: ${order.deliveryAddress}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    if (!order.customizationDetails.isNullOrBlank()) {
                        Text(
                            text = "Customization: ${order.customizationDetails}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WaveGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = "$${String.format("%.2f", order.totalPrice)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = WaveNavyDark
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Status Progression Timeline
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Shipment Lifecycle Progress:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    statusSteps.forEachIndexed { idx, step ->
                        val isDone = idx <= currentIndex
                        val isCurrent = idx == currentIndex
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isDone) WaveNavy else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WaveGold, modifier = Modifier.size(14.dp))
                            }
                        }
                        if (idx < statusSteps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .background(if (idx < currentIndex) WaveNavy else Color(0xFFE2E8F0))
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Ordered", fontSize = 9.sp, color = Color.Gray)
                    Text(text = "Processing", fontSize = 9.sp, color = Color.Gray)
                    Text(text = "Delivered", fontSize = 9.sp, color = Color.Gray)
                }
            }
        }
    }
}

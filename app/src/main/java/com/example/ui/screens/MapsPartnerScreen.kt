package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiService
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Verified Store & Campus Hub location specification.
 */
data class VerifiedLocation(
    val id: String,
    val name: String,
    val campusDivision: String,
    val address: String,
    val landmark: String,
    val roleBadge: String,
    val isProductionHub: Boolean,
    val hours: String,
    val contact: String,
    val pickupStatus: String,
    val description: String,
    val services: List<String>,
    val directionsStepByStep: List<String>
)

val officialVerifiedLocations = listOf(
    VerifiedLocation(
        id = "starz_university",
        name = "Starz University",
        campusDivision = "Main Campus & New Campus",
        address = "Airfield, Sinkor",
        landmark = "Airfield main campus corridor, Monrovia, Liberia",
        roleBadge = "Verified Campus Partner & Official Pickup Hub",
        isProductionHub = false,
        hours = "Mon – Sat: 8:00 AM – 6:00 PM",
        contact = "+231 77 782 7900 • campus@starz.edu.lr",
        pickupStatus = "Active Student Pickup Hub • $5.00 Journals",
        description = "Starz University is the official verified student campus partner for Write Your Wave. Students across both Main Campus and New Campus can inspect physical journal editions, collect pre-orders, and submit bulk Class of 2026 orders directly on campus.",
        services = listOf(
            "On-campus Journal Pickup ($5.00 USD)",
            "Class of 2026 Student Batch Orders",
            "Physical Cover Finish & Page Weight Inspection Desk",
            "Direct Campus Representative Support"
        ),
        directionsStepByStep = listOf(
            "From Tubman Boulevard, take the turn into Airfield, Sinkor.",
            "Continue down the main Airfield paved thoroughfare toward Starz University.",
            "Look for the prominent Starz University signage (Main Campus & New Campus entrance).",
            "The Write Your Wave Student Hub desk is located at the Student Services center."
        )
    ),
    VerifiedLocation(
        id = "waterloop_institute",
        name = "Waterloop Institute of Technology (WIT)",
        campusDivision = "Physical Production Facility & Innovation Lab",
        address = "Airfield Shortcut, Sinkor",
        landmark = "Airfield Shortcut connecting link, Monrovia, Liberia",
        roleBadge = "Official Production Center & Platform Creator",
        isProductionHub = true,
        hours = "Mon – Fri: 8:30 AM – 5:30 PM • Sat: 9:00 AM – 2:00 PM",
        contact = "+231 88 642 9100 • production@waterloop.org",
        pickupStatus = "Official Production Facility & Verification Hub",
        description = "Waterloop Institute of Technology (WIT) is the birthplace and engineering engine of Write Your Wave. This dedicated facility conducts all physical journal printing, 120 GSM acid-free bleed-proof binding, gold foil hot stamping, and post-production serial number verification.",
        services = listOf(
            "Primary Journal Manufacturing & Wire-O Binding",
            "Hot Foil Gold Stamping & Cover Finishing",
            "Unique Post-Production Series Number Stamping",
            "Platform Engineering & Digital Verification Systems",
            "Direct Factory Wholesale & Institutional Orders"
        ),
        directionsStepByStep = listOf(
            "Turn onto Airfield Shortcut from either Old Road or Airfield Sinkor entrance.",
            "Follow the paved shortcut connecting road toward Waterloop Institute of Technology.",
            "Locate the WIT Innovation & Manufacturing complex.",
            "Production inquiries and verified batch pick-ups enter through the main Reception."
        )
    )
)

@Composable
fun MapsPartnerScreen(
    geminiService: GeminiService,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf("ALL") } // "ALL", "STARZ", "WIT"
    var viewMode by remember { mutableStateOf("CARDS") } // "CARDS" or "MAP"
    var searchQuery by remember { mutableStateOf("") }
    var copiedAddressNotice by remember { mutableStateOf<String?>(null) }
    var locationForDirectionsDialog by remember { mutableStateOf<VerifiedLocation?>(null) }
    var showSerialVerificationInfo by remember { mutableStateOf(false) }

    // Google Maps Grounding Assistant State
    var aiQuery by remember { mutableStateOf("Starz University Campus (Main & New Campus) - Airfield, Sinkor") }
    var aiResultText by remember { mutableStateOf<String?>(null) }
    var isAiSearching by remember { mutableStateOf(false) }

    val filteredLocations = remember(selectedTab, searchQuery) {
        officialVerifiedLocations.filter { loc ->
            val matchesTab = when (selectedTab) {
                "STARZ" -> loc.id == "starz_university"
                "WIT" -> loc.id == "waterloop_institute"
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                loc.name.contains(searchQuery, ignoreCase = true) ||
                loc.address.contains(searchQuery, ignoreCase = true) ||
                loc.campusDivision.contains(searchQuery, ignoreCase = true) ||
                loc.description.contains(searchQuery, ignoreCase = true)

            matchesTab && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("maps_partner_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- 1. DEDICATED VERIFIED LOCATIONS HERO HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(WaveNavyDark, Color(0xFF040A14))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = WaveGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.6f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("👑", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "OFFICIAL STORE LOCATOR",
                                    color = WaveGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.1.sp
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VERIFIED NETWORK",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Verified Campus & Production Locations",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Official certified hubs for Write Your Wave $5.00 USD journals. From student pickup at Starz University to precision manufacturing at Waterloop Institute of Technology.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trust Badges Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("2 Verified", color = WaveGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Official Centers", color = Color.LightGray, fontSize = 9.sp)
                            }
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Sinkor Corridor", color = WaveCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Airfield, Monrovia", color = Color.LightGray, fontSize = 9.sp)
                            }
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("$5.00 Flat", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Verified Price", color = Color.LightGray, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- 2. COPIED TOAST / BANNER NOTIFICATION ---
        item {
            AnimatedVisibility(
                visible = copiedAddressNotice != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = Color(0xFF065F46),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = copiedAddressNotice ?: "",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // --- 3. FILTER TABS & VIEW TOGGLE ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // View Mode Toggle (Directory Cards vs Campus Corridor Map)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = if (viewMode == "CARDS") WaveNavy else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewMode = "CARDS" }
                            .testTag("toggle_view_cards")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = if (viewMode == "CARDS") Color.White else WaveNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Verified Cards",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (viewMode == "CARDS") Color.White else WaveNavyDark
                            )
                        }
                    }

                    Surface(
                        color = if (viewMode == "MAP") WaveNavy else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewMode = "MAP" }
                            .testTag("toggle_view_map")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = if (viewMode == "MAP") Color.White else WaveNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Corridor Schematic Map",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (viewMode == "MAP") Color.White else WaveNavyDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter verified locations by keyword, landmark...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WaveNavy) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = WaveNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verified_locations_search_bar")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedTab == "ALL",
                            onClick = { selectedTab = "ALL" },
                            label = { Text("All Verified (2)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WaveNavy,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTab == "STARZ",
                            onClick = { selectedTab = "STARZ" },
                            label = { Text("Starz University (Airfield)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WaveNavy,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedTab == "WIT",
                            onClick = { selectedTab = "WIT" },
                            label = { Text("Waterloop Institute (Airfield Shortcut)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WaveNavy,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        // --- 4. MAP SCHEMATIC VIEW (IF TOGGLED) ---
        if (viewMode == "MAP") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    AirfieldCorridorSchematicMap(
                        onSelectLocation = { locId ->
                            selectedTab = if (locId == "starz_university") "STARZ" else "WIT"
                            viewMode = "CARDS"
                        }
                    )
                }
            }
        }

        // --- 5. VERIFIED LOCATION CARDS LIST ---
        items(filteredLocations) { loc ->
            VerifiedLocationCard(
                location = loc,
                onCopyAddress = {
                    clipboardManager.setText(AnnotatedString(loc.address))
                    copiedAddressNotice = "Address copied: ${loc.address}"
                    coroutineScope.launch {
                        delay(3000)
                        copiedAddressNotice = null
                    }
                },
                onGetDirections = {
                    locationForDirectionsDialog = loc
                },
                onGroundWithAI = {
                    aiQuery = "${loc.name} - ${loc.address}"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // --- 6. PLATFORM SPONSORSHIP & SERIAL VERIFICATION SHOWCASE ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WaveNavyDark),
                    border = BorderStroke(1.5.dp, WaveGold),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wit_powered_and_verification_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = WaveGold,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("⚡", fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Platform Powered by WIT",
                                    color = WaveGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Waterloop Institute of Technology • Airfield Shortcut",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Waterloop Institute of Technology (WIT) powers both the physical manufacturing of the Write Your Wave journals and the modern digital architecture behind this application.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Serial verification note box
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSerialVerificationInfo = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = WaveGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Post-Production Series Verification",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Every journal receives a unique series number stamped at WIT after production for student authenticity.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 7. GOOGLE MAPS AI TRANSIT ASSISTANT ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = WaveNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ask Maps Grounding Assistant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Query transit routes between Starz University and Waterloop Institute in Airfield Sinkor.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = aiQuery,
                            onValueChange = { aiQuery = it },
                            label = { Text("Search location or transit query:") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = WaveNavy) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("maps_search_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (aiQuery.isNotBlank() && !isAiSearching) {
                                    isAiSearching = true
                                    aiResultText = null
                                    coroutineScope.launch {
                                        val res = geminiService.findStoreLocationsAndCampuses(aiQuery.trim())
                                        isAiSearching = false
                                        aiResultText = if (res.isSuccess) {
                                            res.getOrThrow()
                                        } else {
                                            "Could not fetch Maps Grounding at this moment. Please verify GEMINI_API_KEY in the Secrets panel."
                                        }
                                    }
                                }
                            },
                            enabled = !isAiSearching,
                            colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("execute_maps_search_button")
                        ) {
                            if (isAiSearching) {
                                CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Grounding with Google Maps...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Find Location with AI Maps", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        aiResultText?.let { results ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("maps_grounded_results_card")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Maps Grounding Analysis:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = WaveNavyDark
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = results,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIRECTIONS DIALOG ---
    locationForDirectionsDialog?.let { loc ->
        AlertDialog(
            onDismissRequest = { locationForDirectionsDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null,
                    tint = WaveNavy,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Directions to ${loc.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = loc.address,
                        color = WaveNavy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Turn-by-turn route guidelines in Sinkor corridor:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    loc.directionsStepByStep.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = WaveNavy,
                                shape = CircleShape,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(loc.address))
                        copiedAddressNotice = "Address copied: ${loc.address}"
                        locationForDirectionsDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WaveNavy)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { locationForDirectionsDialog = null }) {
                    Text("Close")
                }
            }
        )
    }

    // --- SERIAL VERIFICATION INFO DIALOG ---
    if (showSerialVerificationInfo) {
        AlertDialog(
            onDismissRequest = { showSerialVerificationInfo = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Journal Series Verification System",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "To guarantee physical authenticity for students and leaders:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Every journal manufactured at Waterloop Institute of Technology (Airfield Shortcut) will be stamped with a unique alphanumeric series ID.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• After production, students will be able to input their journal's serial ID into this platform to view the official batch certificate, production date, and authenticity stamp.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• This system will be activated immediately following the inaugural batch production run at WIT.",
                        fontSize = 12.sp,
                        color = WaveNavy,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSerialVerificationInfo = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WaveNavy)
                ) {
                    Text("Understood")
                }
            }
        )
    }
}

/**
 * Dedicated verified location card showing official credentials, verified address,
 * campus details, and direct interaction buttons.
 */
@Composable
fun VerifiedLocationCard(
    location: VerifiedLocation,
    onCopyAddress: () -> Unit,
    onGetDirections: () -> Unit,
    onGroundWithAI: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (location.isProductionHub) 1.8.dp else 1.2.dp,
            color = if (location.isProductionHub) WaveGold else Color(0xFFCBD5E1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.testTag("verified_location_card_${location.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Verified Badge and Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official Verified Seal
                Surface(
                    color = if (location.isProductionHub) Color(0xFFFEF3C7) else Color(0xFFECFDF5),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (location.isProductionHub) Color(0xFFF59E0B) else Color(0xFF10B981)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Checkmark",
                            tint = if (location.isProductionHub) Color(0xFFD97706) else Color(0xFF059669),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (location.isProductionHub) "VERIFIED PRODUCTION HUB ✓" else "VERIFIED CAMPUS PARTNER ✓",
                            color = if (location.isProductionHub) Color(0xFF92400E) else Color(0xFF065F46),
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Campus Division Pill
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (location.isProductionHub) "WIT FACILITY" else "CAMPUS STORE",
                        color = Color(0xFF475569),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Name and Campus Division
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WaveNavyDark
            )

            Text(
                text = location.campusDivision,
                style = MaterialTheme.typography.bodySmall,
                color = if (location.isProductionHub) Color(0xFFB45309) else WaveNavy,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Highlighted Address Container
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (location.isProductionHub) WaveGold.copy(alpha = 0.25f) else WaveNavy.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (location.isProductionHub) Color(0xFFB45309) else WaveNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "VERIFIED ADDRESS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = location.address,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = WaveNavyDark
                            )
                            Text(
                                text = location.landmark,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Copy Address IconButton
                    IconButton(
                        onClick = onCopyAddress,
                        modifier = Modifier.testTag("copy_address_button_${if (location.isProductionHub) "waterloop" else "starz"}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Address",
                            tint = WaveNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Info Grid: Hours & Pickup Readiness
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location.hours,
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.6.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🏷️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$5.00 Journals Available",
                            fontSize = 10.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = location.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                lineHeight = 17.sp
            )

            // Expandable Services Checklist
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (location.isProductionHub) "FACILITY & PRODUCTION SCOPE" else "CAMPUS SERVICES AVAILABLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    location.services.forEach { service ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (location.isProductionHub) Color(0xFFD97706) else Color(0xFF059669),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = service,
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onGetDirections,
                    colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { isExpanded = !isExpanded },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, WaveNavy),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isExpanded) "Hide Specs" else "View Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Custom graphical corridor schematic map of the Airfield / Sinkor area in Monrovia, Liberia,
 * visualizing the proximity between Starz University and Waterloop Institute of Technology.
 */
@Composable
fun AirfieldCorridorSchematicMap(
    onSelectLocation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("corridor_map_canvas")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = WaveNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Airfield, Sinkor Corridor Schematic",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                }
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.6.dp, Color(0xFF86EFAC))
                ) {
                    Text(
                        text = "~3-5 MIN TRANSIT",
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Schematic Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Main Thoroughfare (Airfield Road)
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(w * 0.1f, h * 0.35f),
                        end = Offset(w * 0.9f, h * 0.35f),
                        strokeWidth = 14f
                    )

                    // Connecting Shortcut (Airfield Shortcut)
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(w * 0.3f, h * 0.35f),
                        end = Offset(w * 0.75f, h * 0.8f),
                        strokeWidth = 10f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                    )

                    // Secondary Link
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(w * 0.75f, h * 0.8f),
                        end = Offset(w * 0.95f, h * 0.8f),
                        strokeWidth = 12f
                    )

                    // Node 1: Starz University (Main & New Campus)
                    val starzCenter = Offset(w * 0.3f, h * 0.35f)
                    drawCircle(color = Color(0xFF38BDF8).copy(alpha = 0.25f), radius = 26f, center = starzCenter)
                    drawCircle(color = Color(0xFF1D4ED8), radius = 16f, center = starzCenter)
                    drawCircle(color = Color.White, radius = 6f, center = starzCenter)

                    // Node 2: Waterloop Institute of Technology (Airfield Shortcut)
                    val witCenter = Offset(w * 0.75f, h * 0.8f)
                    drawCircle(color = Color(0xFFFBBF24).copy(alpha = 0.25f), radius = 28f, center = witCenter)
                    drawCircle(color = Color(0xFFD97706), radius = 16f, center = witCenter)
                    drawCircle(color = Color.White, radius = 6f, center = witCenter)
                }

                // Node 1 Interactive Overlay: Starz University
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF1D4ED8)),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 16.dp)
                        .clickable { onSelectLocation("starz_university") }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎓", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Starz University", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = WaveNavyDark)
                        }
                        Text("Airfield, Sinkor", fontSize = 9.sp, color = Color.Gray)
                    }
                }

                // Node 2 Interactive Overlay: Waterloop Institute of Technology
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFD97706)),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                        .clickable { onSelectLocation("waterloop_institute") }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏭", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Waterloop Institute (WIT)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = WaveNavyDark)
                        }
                        Text("Airfield Shortcut, Sinkor (Production)", fontSize = 9.sp, color = Color(0xFFB45309))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tap either location node above to view verified address, operating hours, and turn-by-turn routing.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

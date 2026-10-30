package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.model.CustomizationRequest
import com.example.data.model.Order
import com.example.data.model.SavedAddress
import com.example.data.model.UserProfile
import com.example.data.repository.WriteYourWaveRepository
import com.example.ui.components.AuthGateCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WaveSuccess
import com.example.ui.theme.WaveWarning
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ProfileSubTab(val title: String) {
    OVERVIEW("Overview"),
    PERSONAL_INFO("Personal Info"),
    ORDERS("Order History"),
    CUSTOMIZATION("Custom Requests"),
    ADDRESSES("Campus & Addresses"),
    PREFERENCES("Preferences"),
    SECURITY("Security")
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: WriteYourWaveRepository,
    currentUser: FirebaseUser?,
    userProfile: UserProfile?,
    orders: List<Order>,
    customizations: List<CustomizationRequest>,
    savedAddresses: List<SavedAddress>,
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToJournalEditor: () -> Unit = {},
    onNavigateToCustomize: () -> Unit = {},
    onNavigateToStores: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = ProfileSubTab.values()

    // Form editing states
    var firstName by remember(userProfile, currentUser) {
        mutableStateOf(userProfile?.firstName ?: currentUser?.displayName?.split(" ")?.firstOrNull() ?: "")
    }
    var lastName by remember(userProfile, currentUser) {
        mutableStateOf(userProfile?.lastName ?: currentUser?.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "")
    }
    var displayName by remember(userProfile, currentUser) {
        mutableStateOf(userProfile?.displayName ?: currentUser?.displayName ?: "Wave Scholar")
    }
    var phone by remember(userProfile) { mutableStateOf(userProfile?.phone ?: "") }
    var city by remember(userProfile) { mutableStateOf(userProfile?.city ?: "Monrovia") }
    var country by remember(userProfile) { mutableStateOf(userProfile?.country ?: "Liberia") }
    var affiliation by remember(userProfile) { mutableStateOf(userProfile?.universityOrAffiliation ?: "Starz University") }
    var bio by remember(userProfile) { mutableStateOf(userProfile?.bio ?: "Living with purpose, writing my wave.") }

    var emailNotifications by remember(userProfile) { mutableStateOf(userProfile?.emailNotifications ?: true) }
    var orderAlerts by remember(userProfile) { mutableStateOf(userProfile?.orderAlerts ?: true) }
    var seriesVerificationAlerts by remember(userProfile) { mutableStateOf(userProfile?.seriesVerificationAlerts ?: true) }

    var isSavingProfile by remember { mutableStateOf(false) }
    var saveSuccessNotice by remember { mutableStateOf<String?>(null) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showSignOutConfirmation by remember { mutableStateOf(false) }

    // If unauthenticated, show AuthGateCard
    if (currentUser == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(WaveParchment)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            AuthGateCard(
                title = "Access Your Write Your Wave Profile",
                subtitle = "Sign in with your Google account to manage your $5.00 journal orders, track personalized gold foil cover requests, and view your saved campus addresses.",
                onSuccess = {}
            )
        }
        return
    }

    // Profile Completion Metric (0-100%)
    val completionScore = remember(firstName, lastName, phone, affiliation, bio) {
        var score = 40 // Base for authenticated Google email
        if (firstName.isNotBlank()) score += 15
        if (lastName.isNotBlank()) score += 15
        if (phone.isNotBlank()) score += 15
        if (affiliation.isNotBlank()) score += 10
        if (bio.isNotBlank()) score += 5
        score.coerceAtMost(100)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("profile_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- 1. REFINED EDITORIAL PROFILE HEADER ---
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
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OFFICIAL MEMBER DASHBOARD",
                                    color = WaveGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.1.sp
                                )
                            }
                        }

                        // Verified Status Badge
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
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "VERIFIED ACCOUNT",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Initials / Photo Avatar
                        Surface(
                            color = WaveGold,
                            shape = CircleShape,
                            border = BorderStroke(2.dp, Color.White),
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initials = (displayName.takeIf { it.isNotBlank() } ?: "W")
                                    .split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString("")
                                    .uppercase()
                                Text(
                                    text = initials,
                                    color = WaveNavyDark,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName.ifBlank { "Wave Member" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentUser.email ?: "student@campus.edu.lr",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = WaveNavy,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.6.dp, Color(0xFF334155))
                            ) {
                                Text(
                                    text = affiliation.ifBlank { "Starz University Campus" },
                                    fontSize = 10.sp,
                                    color = WaveGoldLight,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Completion Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profile Completion",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$completionScore%",
                                fontSize = 11.sp,
                                color = WaveGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { completionScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = WaveGold,
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            }
        }

        // --- 2. PERSONALIZED WELCOME BANNER ---
        item {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✍️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "\"Your space. Your stories. Your next chapter.\"",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = WaveNavyDark
                        )
                        Text(
                            text = "Manage your authentic $5.00 Write Your Wave journals, personal gold-foil requests, and campus pickup hubs.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // --- 3. SAVE FEEDBACK NOTICE ---
        item {
            AnimatedVisibility(
                visible = saveSuccessNotice != null,
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
                            text = saveSuccessNotice ?: "",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // --- 4. NAVIGATION SUB-TABS ---
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = WaveNavyDark,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = WaveNavy,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color(0xFFE2E8F0)))
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (selectedTabIndex == index) WaveNavyDark else Color.Gray
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // --- 5. TAB CONTENT DISPATCH ---
        when (tabs[selectedTabIndex]) {
            ProfileSubTab.OVERVIEW -> {
                // Summary Cards
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "ACCOUNT SUMMARY METRICS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyLight,
                            letterSpacing = 1.1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                title = "Total Orders",
                                value = "${orders.size}",
                                subtitle = "$5.00 Journals",
                                color = WaveNavy,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Custom Covers",
                                value = "${customizations.size}",
                                subtitle = "Foil Stamp Requests",
                                color = WaveCardDark,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Campus Hub",
                                value = "Starz",
                                subtitle = "Airfield, Sinkor",
                                color = Color(0xFF047857),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Action Shortcuts
                        Text(
                            text = "QUICK ACTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyLight,
                            letterSpacing = 1.1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToJournalEditor,
                                colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Write Journal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onNavigateToCatalog,
                                colors = ButtonDefaults.buttonColors(containerColor = WaveGold, contentColor = WaveNavyDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Shop $5.00", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToCustomize,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, WaveNavy),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Design Cover", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onNavigateToStores,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, WaveNavy),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Campus Hubs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Verified Campus Production Information Card
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Powered by Waterloop Institute of Technology (WIT)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = WaveNavyDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your orders are produced at WIT (Airfield Shortcut, Sinkor) with unique serial verification numbers stamped after production.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            ProfileSubTab.PERSONAL_INFO -> {
                // Personal Information Form
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Personal & Campus Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyDark
                            )
                            Text(
                                text = "Keep your contact and campus information current for accurate order delivery and journal customization.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    label = { Text("First Name *") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    label = { Text("Last Name *") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = { Text("Display Name *") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = currentUser.email ?: "",
                                onValueChange = {},
                                label = { Text("Email (Verified via Google)") },
                                enabled = false,
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = WaveNavy) },
                                trailingIcon = { Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF10B981)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone / WhatsApp (for order alerts)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = WaveNavy) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = affiliation,
                                onValueChange = { affiliation = it },
                                label = { Text("Campus / Institutional Affiliation") },
                                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = WaveNavy) },
                                placeholder = { Text("e.g. Starz University Main Campus") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = country,
                                    onValueChange = { country = it },
                                    label = { Text("Country") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("Personal Writing Motto / Bio") },
                                maxLines = 3,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (!isSavingProfile) {
                                        isSavingProfile = true
                                        saveSuccessNotice = null
                                        coroutineScope.launch {
                                            val updated = UserProfile(
                                                id = currentUser.uid,
                                                displayName = displayName.ifBlank { "Wave Scholar" },
                                                firstName = firstName,
                                                lastName = lastName,
                                                email = currentUser.email ?: "",
                                                phone = phone,
                                                city = city,
                                                country = country,
                                                universityOrAffiliation = affiliation,
                                                bio = bio,
                                                emailNotifications = emailNotifications,
                                                orderAlerts = orderAlerts,
                                                seriesVerificationAlerts = seriesVerificationAlerts
                                            )
                                            val res = repository.saveUserProfile(updated)
                                            isSavingProfile = false
                                            if (res.isSuccess) {
                                                saveSuccessNotice = "Profile successfully updated and saved!"
                                                delay(3500)
                                                saveSuccessNotice = null
                                            }
                                        }
                                    }
                                },
                                enabled = !isSavingProfile,
                                colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_profile_button")
                            ) {
                                if (isSavingProfile) {
                                    CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Saving Changes...", fontSize = 13.sp)
                                } else {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Personal Info", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            ProfileSubTab.ORDERS -> {
                // Customer Order History
                if (orders.isEmpty()) {
                    item {
                        EmptyStateCard(
                            emoji = "📦",
                            title = "No Orders Yet",
                            description = "You haven't placed any orders yet. Discover our official Write Your Wave journals priced at $5.00 USD.",
                            actionLabel = "Browse Journals ($5.00)",
                            onActionClick = onNavigateToCatalog
                        )
                    }
                } else {
                    items(orders) { order ->
                        OrderSummaryCard(
                            order = order,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            ProfileSubTab.CUSTOMIZATION -> {
                // Customization Requests Queue
                if (customizations.isEmpty()) {
                    item {
                        EmptyStateCard(
                            emoji = "✨",
                            title = "No Customization Requests",
                            description = "Personalize your journal cover with your name, quote, and custom gold foil hot stamping.",
                            actionLabel = "Design Custom Cover",
                            onActionClick = onNavigateToCustomize
                        )
                    }
                } else {
                    items(customizations) { req ->
                        CustomizationRequestCard(
                            request = req,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            ProfileSubTab.ADDRESSES -> {
                // Saved Campus & Delivery Addresses
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CAMPUS PICKUP & DELIVERY ADDRESSES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyLight,
                                letterSpacing = 1.1.sp
                            )
                            TextButton(onClick = { showAddressDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Address", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (savedAddresses.isEmpty()) {
                    item {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Starz University Campus (Main & New Campus)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = WaveNavyDark
                                )
                                Text(
                                    text = "Airfield, Sinkor • Default Verified Campus Pickup Hub",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color(0xFFF0FDF4),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(0.6.dp, Color(0xFF86EFAC))
                                ) {
                                    Text(
                                        text = "PRIMARY PICKUP LOCATION ✓",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(savedAddresses) { addr ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = addr.recipientName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = WaveNavyDark
                                    )
                                    Text(
                                        text = addr.addressLine,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "${addr.city}, ${addr.country}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.deleteSavedAddress(addr.id)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            ProfileSubTab.PREFERENCES -> {
                // Preferences Form
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Account & Notification Preferences",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyDark
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            PreferenceToggleRow(
                                title = "Order Status & Shipment Alerts",
                                description = "Receive updates when your $5.00 journal is printed at WIT and ready for pickup at Starz University.",
                                checked = orderAlerts,
                                onCheckedChange = {
                                    orderAlerts = it
                                    coroutineScope.launch {
                                        val updated = (userProfile ?: UserProfile(id = currentUser.uid)).copy(orderAlerts = it)
                                        repository.saveUserProfile(updated)
                                    }
                                }
                            )

                            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 12.dp))

                            PreferenceToggleRow(
                                title = "Series Number Verification Alerts",
                                description = "Get notified as soon as your journal's serial verification code is stamped and activated.",
                                checked = seriesVerificationAlerts,
                                onCheckedChange = {
                                    seriesVerificationAlerts = it
                                    coroutineScope.launch {
                                        val updated = (userProfile ?: UserProfile(id = currentUser.uid)).copy(seriesVerificationAlerts = it)
                                        repository.saveUserProfile(updated)
                                    }
                                }
                            )

                            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 12.dp))

                            PreferenceToggleRow(
                                title = "Movement Community Announcements",
                                description = "Receive early invitations to student workshops, Class of 2026 specials, and creator programs.",
                                checked = emailNotifications,
                                onCheckedChange = {
                                    emailNotifications = it
                                    coroutineScope.launch {
                                        val updated = (userProfile ?: UserProfile(id = currentUser.uid)).copy(emailNotifications = it)
                                        repository.saveUserProfile(updated)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            ProfileSubTab.SECURITY -> {
                // Security & Sessions
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Security & Active Sessions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = WaveNavy, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Google Sign-In Authenticated",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = WaveNavyDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Your account is cryptographically secured via Google Identity Services and Firebase Authentication.",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = { showSignOutConfirmation = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out of Account", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- ADD ADDRESS DIALOG ---
    if (showAddressDialog) {
        var newRecipient by remember { mutableStateOf(displayName) }
        var newPhone by remember { mutableStateOf(phone) }
        var newAddressLine by remember { mutableStateOf("") }
        var newInstructions by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Add Delivery / Campus Location", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newRecipient,
                        onValueChange = { newRecipient = it },
                        label = { Text("Recipient Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAddressLine,
                        onValueChange = { newAddressLine = it },
                        label = { Text("Address / Campus Building") },
                        placeholder = { Text("e.g. Starz University New Campus Gate") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newInstructions,
                        onValueChange = { newInstructions = it },
                        label = { Text("Pickup Instructions") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddressLine.isNotBlank()) {
                            coroutineScope.launch {
                                repository.saveSavedAddress(
                                    SavedAddress(
                                        userId = currentUser.uid,
                                        recipientName = newRecipient,
                                        phone = newPhone,
                                        addressLine = newAddressLine,
                                        city = "Sinkor, Monrovia",
                                        country = "Liberia",
                                        deliveryInstructions = newInstructions,
                                        isDefault = savedAddresses.isEmpty()
                                    )
                                )
                                showAddressDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WaveNavy)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- SIGN OUT CONFIRMATION DIALOG ---
    if (showSignOutConfirmation) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmation = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out of your Write Your Wave account?") },
            confirmButton = {
                Button(
                    onClick = {
                        Firebase.auth.signOut()
                        showSignOutConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.height(84.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = WaveGoldLight,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PreferenceToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = WaveNavyDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WaveNavy,
                checkedTrackColor = WaveCyan.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun OrderSummaryCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderReference,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = WaveNavy
                )
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${order.productTitle} × ${order.quantity}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = WaveNavyDark
            )
            Text(
                text = "Total: $${String.format("%.2f", order.totalPrice)} • Payment: ${order.paymentStatus}",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.deliveryAddress,
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CustomizationRequestCard(
    request: CustomizationRequest,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, WaveGold.copy(alpha = 0.7f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = WaveGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "GOLD FOIL REQUEST",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                StatusBadge(status = request.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Name on Cover: \"${request.nameOnCover}\"",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = WaveNavyDark
            )
            if (!request.motivationalQuote.isNullOrBlank()) {
                Text(
                    text = "Quote: \"${request.motivationalQuote}\"",
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
            if (!request.preferredColors.isNullOrBlank()) {
                Text(
                    text = "Color Palette: ${request.preferredColors}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    emoji: String,
    title: String,
    description: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = WaveNavyDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

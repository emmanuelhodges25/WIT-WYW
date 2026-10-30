package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.ai.GeminiService
import com.example.data.model.Ambassador
import com.example.data.model.BulkOrder
import com.example.data.model.CartItem
import com.example.data.model.CustomizationRequest
import com.example.data.model.InventoryMovement
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.repository.WriteYourWaveRepository
import com.example.ui.components.AuthGateCard
import com.example.ui.components.WaveTopBar
import com.example.ui.components.attemptAutoSignIn
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AmbassadorPortalScreen
import com.example.ui.screens.AmbassadorsScreen
import com.example.ui.screens.BulkOrdersScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.CustomizeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalEditor
import com.example.ui.screens.MapsPartnerScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WaveGuideChatScreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.WaveBorderDark
import com.example.ui.theme.WaveLogoAzure
import com.example.ui.theme.WaveLogoRoyal
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WriteYourWaveTheme
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenDestination {
    HOME,
    JOURNAL,
    CATALOG,
    CUSTOMIZE,
    ORDERS,
    CHAT,
    BULK,
    AMBASSADORS,
    MAPS,
    CONTACT,
    ADMIN,
    PROFILE,
    AMBASSADOR_PORTAL
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: WriteYourWaveRepository
    private val geminiService = GeminiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        repository = WriteYourWaveRepository(applicationContext)

        setContent {
            WriteYourWaveTheme {
                WriteYourWaveApp(
                    repository = repository,
                    geminiService = geminiService
                )
            }
        }
    }
}

@Composable
fun WriteYourWaveApp(
    repository: WriteYourWaveRepository,
    geminiService: GeminiService
) {
    val coroutineScope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf<FirebaseUser?>(Firebase.auth.currentUser) }
    var currentScreen by remember { mutableStateOf(ScreenDestination.HOME) }
    var selectedProductForCustomize by remember { mutableStateOf<Product?>(null) }
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }

    val onAddToCart: (Product) -> Unit = { prod ->
        val existingIndex = cartItems.indexOfFirst { it.product.id == prod.id }
        cartItems = if (existingIndex >= 0) {
            cartItems.mapIndexed { idx, item ->
                if (idx == existingIndex) item.copy(quantity = item.quantity + 1) else item
            }
        } else {
            cartItems + CartItem(product = prod, quantity = 1)
        }
    }

    val onUpdateCartQuantity: (Product, Int) -> Unit = { prod, newQty ->
        cartItems = if (newQty <= 0) {
            cartItems.filterNot { it.product.id == prod.id }
        } else {
            cartItems.map { if (it.product.id == prod.id) it.copy(quantity = newQty) else it }
        }
    }

    val onClearCart: () -> Unit = {
        cartItems = emptyList()
    }

    // Listen to Firebase auth changes
    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(authListener)
        onDispose {
            Firebase.auth.removeAuthStateListener(authListener)
        }
    }

    // Attempt silent auto-sign-in on startup if unauthenticated
    val context = androidx.compose.ui.platform.LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            },
            onUnauthenticated = {
                // User remains guest until explicit sign in
            },
            scope = coroutineScope
        )

        // Seed initial products and ambassadors to avoid blank states
        repository.seedInitialDataIfEmpty()
    }

    // State collections
    val productsFlow = remember { repository.observeProducts() }
    val products by productsFlow.collectAsState(initial = emptyList())

    val userOrdersFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeUserOrders(currentUser!!.uid)
        else MutableStateFlow(emptyList())
    }
    val userOrders by userOrdersFlow.collectAsState(initial = emptyList())

    val allOrdersFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAllOrders()
        else MutableStateFlow(emptyList())
    }
    val allOrders by allOrdersFlow.collectAsState(initial = emptyList())

    val userCustomizationsFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeUserCustomizations(currentUser!!.uid)
        else MutableStateFlow(emptyList())
    }
    val userCustomizations by userCustomizationsFlow.collectAsState(initial = emptyList())

    val allCustomizationsFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAllCustomizations()
        else MutableStateFlow(emptyList())
    }
    val allCustomizations by allCustomizationsFlow.collectAsState(initial = emptyList())

    val bulkOrdersFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAllBulkOrders()
        else MutableStateFlow(emptyList())
    }
    val bulkOrders by bulkOrdersFlow.collectAsState(initial = emptyList())

    val movementsFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeMovements()
        else MutableStateFlow(emptyList())
    }
    val movements by movementsFlow.collectAsState(initial = emptyList())

    val ambassadorsFlow = remember { repository.observeAmbassadors() }
    val ambassadors by ambassadorsFlow.collectAsState(initial = emptyList())

    val userProfileFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeUserProfile(currentUser!!.uid)
        else MutableStateFlow(null)
    }
    val userProfile by userProfileFlow.collectAsState(initial = null)

    val savedAddressesFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeSavedAddresses(currentUser!!.uid)
        else MutableStateFlow(emptyList())
    }
    val savedAddresses by savedAddressesFlow.collectAsState(initial = emptyList())

    val tasksFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeTasks()
        else MutableStateFlow(emptyList())
    }
    val tasks by tasksFlow.collectAsState(initial = emptyList())

    val ambassadorProfilesFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAmbassadorProfiles()
        else MutableStateFlow(emptyList())
    }
    val ambassadorProfiles by ambassadorProfilesFlow.collectAsState(initial = emptyList())

    val myAmbassadorProfileFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAmbassadorProfile(currentUser!!.uid)
        else MutableStateFlow(null)
    }
    val myAmbassadorProfile by myAmbassadorProfileFlow.collectAsState(initial = null)

    val allReferralsFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAllReferrals()
        else MutableStateFlow(emptyList())
    }
    val allReferrals by allReferralsFlow.collectAsState(initial = emptyList())

    val myReferralsFlow = remember(currentUser?.uid, myAmbassadorProfile?.id) {
        val ambId = myAmbassadorProfile?.id
        if (ambId != null) repository.observeAmbassadorReferrals(ambId)
        else MutableStateFlow(emptyList())
    }
    val myReferrals by myReferralsFlow.collectAsState(initial = emptyList())

    val myPayoutsFlow = remember(currentUser?.uid, myAmbassadorProfile?.id) {
        val ambId = myAmbassadorProfile?.id
        if (ambId != null) repository.observeAmbassadorPayouts(ambId)
        else MutableStateFlow(emptyList())
    }
    val myPayouts by myPayoutsFlow.collectAsState(initial = emptyList())

    val auditLogsFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAuditLogs()
        else MutableStateFlow(emptyList())
    }
    val auditLogs by auditLogsFlow.collectAsState(initial = emptyList())

    val invitationsFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeAdminInvitations()
        else MutableStateFlow(emptyList())
    }
    val invitations by invitationsFlow.collectAsState(initial = emptyList())

    // BackHandler for custom screen transitions
    BackHandler(enabled = currentScreen != ScreenDestination.HOME) {
        currentScreen = ScreenDestination.HOME
    }

    val isAdmin = repository.isCurrentUserAdmin()

    Scaffold(
        topBar = {
            WaveTopBar(
                title = when (currentScreen) {
                    ScreenDestination.HOME -> "Write Your Wave"
                    ScreenDestination.JOURNAL -> "Wave Journal Editor"
                    ScreenDestination.CATALOG -> "Notebook Collections"
                    ScreenDestination.CUSTOMIZE -> "Live Cover Designer"
                    ScreenDestination.ORDERS -> "Orders & Shipments"
                    ScreenDestination.CHAT -> "WaveGuide AI"
                    ScreenDestination.BULK -> "Bulk & Schools"
                    ScreenDestination.AMBASSADORS -> "Ambassadors"
                    ScreenDestination.MAPS -> "Stores & Campuses"
                    ScreenDestination.CONTACT -> "Contact Movement"
                    ScreenDestination.ADMIN -> "Admin Dashboard"
                    ScreenDestination.PROFILE -> "Member Profile"
                    ScreenDestination.AMBASSADOR_PORTAL -> "Ambassador Portal"
                },
                canNavigateBack = currentScreen != ScreenDestination.HOME,
                onNavigateBack = { currentScreen = ScreenDestination.HOME },
                userEmail = currentUser?.email,
                isAdmin = isAdmin,
                onAdminClick = { currentScreen = ScreenDestination.ADMIN },
                onAmbassadorClick = { currentScreen = ScreenDestination.AMBASSADOR_PORTAL },
                onUserClick = { currentScreen = ScreenDestination.PROFILE }
            )
        },
        bottomBar = {
            Surface(
                color = WaveNavyDark,
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, WaveBorderDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wave_bottom_navigation")
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = WaveGold,
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == ScreenDestination.HOME,
                        onClick = { currentScreen = ScreenDestination.HOME },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = {
                            Text(
                                "Home",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == ScreenDestination.HOME) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = WaveGold,
                            indicatorColor = WaveLogoRoyal,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenDestination.JOURNAL,
                        onClick = { currentScreen = ScreenDestination.JOURNAL },
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Journal") },
                        label = {
                            Text(
                                "Journal",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == ScreenDestination.JOURNAL) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = WaveGold,
                            indicatorColor = WaveLogoRoyal,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_journal")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenDestination.CATALOG,
                        onClick = { currentScreen = ScreenDestination.CATALOG },
                        icon = {
                            val totalCart = cartItems.sumOf { it.quantity }
                            if (totalCart > 0) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = WaveGold,
                                        contentColor = WaveNavyDark
                                    ) { Text("$totalCart", fontWeight = FontWeight.Bold) }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Shop")
                                }
                            } else {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Shop")
                            }
                        },
                        label = {
                            Text(
                                "Shop",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == ScreenDestination.CATALOG) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = WaveGold,
                            indicatorColor = WaveLogoRoyal,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_catalog")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenDestination.CUSTOMIZE,
                        onClick = { currentScreen = ScreenDestination.CUSTOMIZE },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Customize") },
                        label = {
                            Text(
                                "Design",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == ScreenDestination.CUSTOMIZE) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = WaveGold,
                            indicatorColor = WaveLogoRoyal,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_customize")
                    )
                    NavigationBarItem(
                        selected = currentScreen == ScreenDestination.ORDERS,
                        onClick = { currentScreen = ScreenDestination.ORDERS },
                        icon = {
                            if (userOrders.isNotEmpty()) {
                                BadgedBox(badge = {
                                    Badge(
                                        containerColor = WaveGold,
                                        contentColor = WaveNavyDark
                                    ) { Text("${userOrders.size}", fontWeight = FontWeight.Bold) }
                                }) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = "Orders")
                                }
                            } else {
                                Icon(Icons.Default.LocalShipping, contentDescription = "Orders")
                            }
                        },
                        label = {
                            Text(
                                "Orders",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == ScreenDestination.ORDERS) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = WaveGold,
                            indicatorColor = WaveLogoRoyal,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_orders")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.HOME -> HomeScreen(
                    products = products,
                    isLoading = products.isEmpty(),
                    onNavigateToCatalog = { currentScreen = ScreenDestination.CATALOG },
                    onNavigateToCustomize = {
                        selectedProductForCustomize = null
                        currentScreen = ScreenDestination.CUSTOMIZE
                    },
                    onNavigateToBulkOrders = { currentScreen = ScreenDestination.BULK },
                    onNavigateToAmbassadors = { currentScreen = ScreenDestination.AMBASSADORS },
                    onNavigateToChat = { currentScreen = ScreenDestination.CHAT },
                    onNavigateToMaps = { currentScreen = ScreenDestination.MAPS },
                    onNavigateToOrders = { currentScreen = ScreenDestination.ORDERS },
                    onNavigateToJournalEditor = { currentScreen = ScreenDestination.JOURNAL },
                    onProductClick = { prod ->
                        selectedProductForCustomize = prod
                        currentScreen = ScreenDestination.CATALOG
                    },
                    onSubmitContactMessage = { name, email, msg ->
                        coroutineScope.launch {
                            try {
                                repository.submitContactMessage(
                                    name = name,
                                    email = email,
                                    subject = "Homepage Contact Inquiry",
                                    category = "General",
                                    message = msg
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Contact message error", e)
                            }
                        }
                    }
                )

                ScreenDestination.JOURNAL -> JournalEditor(
                    repository = repository,
                    geminiService = geminiService,
                    isSignedIn = currentUser != null,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = ScreenDestination.HOME }
                )

                ScreenDestination.CATALOG -> CatalogScreen(
                    products = products,
                    isLoading = products.isEmpty(),
                    isSignedIn = currentUser != null,
                    currentUserEmail = currentUser?.email,
                    cartItems = cartItems,
                    onAddToCart = onAddToCart,
                    onUpdateCartQuantity = onUpdateCartQuantity,
                    onClearCart = onClearCart,
                    onPlaceOrder = { pId, title, price, qty, name, email, phone, addr, notes ->
                        repository.createOrder(
                            productId = pId,
                            productTitle = title,
                            unitPrice = price,
                            quantity = qty,
                            customerName = name,
                            customerEmail = email,
                            customerPhone = phone,
                            deliveryAddress = addr,
                            customizationDetails = notes
                        )
                    },
                    onNavigateToCustomizeWithProduct = { prod ->
                        selectedProductForCustomize = prod
                        currentScreen = ScreenDestination.CUSTOMIZE
                    }
                )

                ScreenDestination.CUSTOMIZE -> CustomizeScreen(
                    products = products,
                    preselectedProduct = selectedProductForCustomize,
                    isSignedIn = currentUser != null,
                    currentUserEmail = currentUser?.email,
                    geminiService = geminiService,
                    onSubmitCustomization = { pId, name, email, phone, nameOnCover, colors, quote, instructions ->
                        repository.submitCustomization(
                            productId = pId,
                            customerName = name,
                            customerEmail = email,
                            customerPhone = phone,
                            nameOnCover = nameOnCover,
                            preferredColors = colors,
                            motivationalQuote = quote,
                            customInstructions = instructions
                        )
                    }
                )

                ScreenDestination.ORDERS -> OrdersScreen(
                    orders = userOrders,
                    isSignedIn = currentUser != null,
                    onLookupReference = { ref -> repository.getOrderByReference(ref) }
                )

                ScreenDestination.CHAT -> WaveGuideChatScreen(
                    geminiService = geminiService
                )

                ScreenDestination.BULK -> BulkOrdersScreen(
                    isSignedIn = currentUser != null,
                    currentUserEmail = currentUser?.email,
                    onSubmitBulkOrder = { org, contact, email, phone, qty, reqs, deadline ->
                        repository.submitBulkOrder(
                            organizationName = org,
                            contactPerson = contact,
                            email = email,
                            phone = phone,
                            estimatedQuantity = qty,
                            customizationRequirements = reqs,
                            deadlineDate = deadline
                        )
                    }
                )

                ScreenDestination.AMBASSADORS -> AmbassadorsScreen(
                    ambassadors = ambassadors,
                    isLoading = ambassadors.isEmpty(),
                    onNavigateToPortal = { currentScreen = ScreenDestination.AMBASSADOR_PORTAL }
                )

                ScreenDestination.MAPS -> MapsPartnerScreen(
                    geminiService = geminiService
                )

                ScreenDestination.CONTACT -> ContactScreen(
                    isSignedIn = currentUser != null,
                    currentUserEmail = currentUser?.email,
                    onSubmitMessage = { name, email, subj, cat, msg ->
                        repository.submitContactMessage(name, email, subj, cat, msg)
                    }
                )

                ScreenDestination.ADMIN -> AdminDashboardScreen(
                    orders = allOrders,
                    products = products,
                    customizations = allCustomizations,
                    bulkOrders = bulkOrders,
                    movements = movements,
                    tasks = tasks,
                    ambassadors = ambassadorProfiles,
                    referrals = allReferrals,
                    payouts = myPayouts,
                    auditLogs = auditLogs,
                    invitations = invitations,
                    currentUserEmail = currentUser?.email,
                    geminiService = geminiService,
                    onUpdateOrderStatus = { orderId, status, paymentStatus ->
                        repository.updateOrderStatus(orderId, status, paymentStatus)
                    },
                    onAdjustStock = { prodId, change, reason ->
                        repository.recordInventoryMovement(prodId, change, "ADJUSTMENT", reason)
                    },
                    onCreateTask = { title, desc, cat, prio, assignedTo, due ->
                        repository.createBusinessTask(title, desc, cat, prio, assignedTo, due)
                    },
                    onUpdateTaskStatus = { taskId, status ->
                        repository.updateTaskStatus(taskId, status)
                    },
                    onInviteAdmin = { email, role ->
                        repository.inviteAdministrator(email, role)
                    },
                    onUpdateAmbassadorStatus = { ambId, status ->
                        repository.updateAmbassadorStatus(ambId, status)
                    },
                    onRecordPayout = { ambId, email, amt, method ->
                        repository.recordAmbassadorPayout(ambId, email, amt, method, "Referral commission payout")
                    }
                )

                ScreenDestination.AMBASSADOR_PORTAL -> AmbassadorPortalScreen(
                    ambassadorProfile = myAmbassadorProfile,
                    referrals = myReferrals,
                    payouts = myPayouts,
                    isSignedIn = currentUser != null,
                    currentUserEmail = currentUser?.email,
                    onApply = { fullName, email, phone, campus, social, audience ->
                        repository.submitAmbassadorApplication(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            campus = campus,
                            socialChannel = social,
                            audienceDesc = audience
                        )
                    }
                )

                ScreenDestination.PROFILE -> ProfileScreen(
                    repository = repository,
                    currentUser = currentUser,
                    userProfile = userProfile,
                    orders = userOrders,
                    customizations = userCustomizations,
                    savedAddresses = savedAddresses,
                    onNavigateToCatalog = { currentScreen = ScreenDestination.CATALOG },
                    onNavigateToJournalEditor = { currentScreen = ScreenDestination.JOURNAL },
                    onNavigateToCustomize = { currentScreen = ScreenDestination.CUSTOMIZE },
                    onNavigateToStores = { currentScreen = ScreenDestination.MAPS }
                )
            }
        }
    }
}

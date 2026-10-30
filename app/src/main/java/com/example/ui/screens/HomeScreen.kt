package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.JournalInteriorPage
import com.example.data.model.Product
import com.example.ui.components.BrandFooter
import com.example.ui.components.JournalInteriorPreviewDialog
import com.example.ui.components.LiveCoverPreview
import com.example.ui.components.OfficialJournalCoverShowcase
import com.example.ui.theme.WaveAzureBg
import com.example.ui.theme.WaveAzureLight
import com.example.ui.theme.WaveBorderDark
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCrownBlack
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveLogoAzure
import com.example.ui.theme.WaveLogoRoyal
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment

@Composable
fun HomeScreen(
    products: List<Product>,
    isLoading: Boolean,
    onNavigateToCatalog: () -> Unit,
    onNavigateToCustomize: () -> Unit,
    onNavigateToBulkOrders: () -> Unit,
    onNavigateToAmbassadors: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToMaps: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onProductClick: (Product) -> Unit,
    onNavigateToJournalEditor: () -> Unit = {},
    onSubmitContactMessage: (name: String, email: String, message: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedInteriorPage by remember { mutableStateOf<JournalInteriorPage?>(null) }

    val sampleInteriorPages = listOf(
        JournalInteriorPage(
            title = "My Story & Roots",
            subtitle = "Where I came from shapes where I am going",
            description = "Structured journaling template for honoring ancestry, heritage, family milestones, and formative experiences.",
            prompt = "What core values did you inherit from your community? How do they anchor your pursuit of higher leadership today?",
            templateCategory = "Identity & Roots"
        ),
        JournalInteriorPage(
            title = "Daily Intentional Wave",
            subtitle = "Morning alignment and evening reflection",
            description = "Dual-entry daily system for university and secondary students to balance academic sprints with peace of mind.",
            prompt = "Morning: What 3 priorities will push my needle today?\nEvening: What lesson did I learn through adversity today?",
            templateCategory = "Daily Practice"
        ),
        JournalInteriorPage(
            title = "Vision 2030 Roadmap",
            subtitle = "Mapping long-term career and societal impact",
            description = "Milestone matrix dividing academic study, financial discipline, community service, and technical expertise.",
            prompt = "In 5 years, who must I become to solve the grandest challenge facing my generation?",
            templateCategory = "Future Vision"
        ),
        JournalInteriorPage(
            title = "Academic Syllabus & Research Grid",
            subtitle = "Syllabus mapping and research hypothesis ledger",
            description = "Specialized framework designed for university scholars, dissertation drafting, and deep STEM note-taking.",
            prompt = "Hypothesis & Key Source References:\nWhat questions remain unanswered following today's seminar?",
            templateCategory = "Academic Framework"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- 1. HERO SECTION & RESTRUCTURED OFFICIAL BRAND LOGO ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(WaveNavyDark, WaveLogoRoyal, WaveNavy)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Elevated Official Brand Logo Showcase Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, WaveGold),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("brand_hero_official_logo_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Crown movement badge
                            Surface(
                                color = WaveAzureBg,
                                border = BorderStroke(1.dp, WaveLogoAzure.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text("👑", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "OFFICIAL MOVEMENT STATIONERY",
                                        color = WaveLogoRoyal,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.2.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Official User-Provided Logo (Crown + WRITE YOUR in Royal Blue + WAVE in Azure Blue with radiance)
                            Image(
                                painter = painterResource(id = R.drawable.wyw_official_logo),
                                contentDescription = "Write Your Wave Crown and Brushstroke Brand Logo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .testTag("hero_official_logo_image"),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Core Brand Motto
                            Text(
                                text = "“Ride the wave of your purpose and leave an enduring shore.”",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = WaveLogoRoyal,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Premium Quality Specification Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = WaveParchment,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.8.dp, Color(0xFFE2E8F0))
                                ) {
                                    Text(
                                        text = "120 GSM Acid-Free",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WaveNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = WaveParchment,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.8.dp, Color(0xFFE2E8F0))
                                ) {
                                    Text(
                                        text = "Gold Foil Stamped",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WaveNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = WaveParchment,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.8.dp, Color(0xFFE2E8F0))
                                ) {
                                    Text(
                                        text = "5 Life Pillars",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WaveNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "More Than a Notebook.\nIt's a Movement.",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Crafted for students, scholars, dreamers, and future leaders. Define your purpose, master daily alignment, and turn vision into legacy on heirloom-quality bleed-proof paper.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCatalog,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WaveGold,
                                contentColor = WaveNavyDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_explore_button")
                        ) {
                            Text(
                                text = "Shop Stationery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToCustomize,
                            border = BorderStroke(1.5.dp, Color.White),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_customize_button")
                        ) {
                            Text(
                                text = "Design Cover",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 2. QUICK ACTION SHORTCUTS ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "DISCOVER & ENGAGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WaveNavyLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Custom Cover",
                        subtitle = "Gold foil stamp",
                        icon = Icons.Default.Edit,
                        color = WaveNavy,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCustomize
                    )
                    QuickActionCard(
                        title = "Bulk & School",
                        subtitle = "Tiered quotes",
                        icon = Icons.Default.CorporateFare,
                        color = WaveCardDark,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBulkOrders
                    )
                    QuickActionCard(
                        title = "WaveGuide AI",
                        subtitle = "Smart advisor",
                        icon = Icons.Default.AutoAwesome,
                        color = WaveGold,
                        contentColor = WaveNavyDark,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToChat
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Store Locator",
                        subtitle = "Maps Grounding",
                        icon = Icons.Default.LocationOn,
                        color = WaveNavyLight,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMaps
                    )
                    QuickActionCard(
                        title = "Track Order",
                        subtitle = "Order ref lookup",
                        icon = Icons.Default.LocalShipping,
                        color = WaveNavy,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                    QuickActionCard(
                        title = "Ambassadors",
                        subtitle = "Stories & Voices",
                        icon = Icons.Default.People,
                        color = WaveCardDark,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAmbassadors
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Digital Journal Quick Access Banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WaveNavyDark),
                    border = BorderStroke(1.5.dp, WaveGold.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToJournalEditor() }
                        .testTag("home_open_journal_editor_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(WaveGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = WaveNavyDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Write in Your Journal",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "5 Life Pillars • AI Reflection Prompts • Firestore Sync",
                                    fontSize = 11.sp,
                                    color = WaveGold.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Open Journal Editor",
                            tint = WaveGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // --- 3. FEATURED PRODUCTS PREVIEW ---
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Official Journals ($5.00 USD)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark
                        )
                        Text(
                            text = "Produced at Waterloop Institute of Technology (WIT) • Airfield Shortcut, Sinkor",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "Shop Journals →",
                        color = WaveNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { onNavigateToCatalog() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WaveNavy)
                    }
                } else if (products.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No products published yet. Check back soon!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(products) { product ->
                            ProductHeroCard(
                                product = product,
                                onClick = { onProductClick(product) }
                            )
                        }
                    }
                }
            }
        }

        // --- 3.5 AUTHENTIC PHYSICAL JOURNAL COVERS SHOWCASE ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OfficialJournalCoverShowcase(
                    onPersonalizeClick = onNavigateToCustomize
                )
            }
        }

        // --- 4. INTERACTIVE JOURNAL INTERIOR PREVIEWS ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Surface(
                    color = WaveGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "EXCLUSIVELY INSIDE",
                        color = WaveNavyDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "Inspect Journal Interior Pages",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WaveNavyDark
                )
                Text(
                    text = "Tap any layout to preview authentic page templates, reflection matrices, and syllabus layouts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(14.dp))

                sampleInteriorPages.forEach { page ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { selectedInteriorPage = page }
                            .testTag("preview_interior_${page.title.take(10)}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WaveNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "Journal Page",
                                    tint = WaveGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = page.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = WaveNavyDark
                                )
                                Text(
                                    text = page.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "View page",
                                tint = WaveNavy
                            )
                        }
                    }
                }
            }
        }

        // --- 5. LIVE CUSTOMIZER TEASER ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Live Cover Customization",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WaveNavyDark
                )
                Text(
                    text = "See how your personalized gold foil cover looks before ordering:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(14.dp))

                LiveCoverPreview(
                    notebookTitle = "Write Your Wave Signature Journal",
                    nameOnCover = "EMMANUEL HODGES",
                    quote = "Ride the wave of your purpose and leave an enduring shore.",
                    baseColor = WaveNavyDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onNavigateToCustomize,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveNavy,
                        contentColor = WaveGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("launch_customizer_button")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Design My Personalized Cover", fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 5.75 CONTACT SECTION ---
        item {
            HomeContactSection(
                onSubmitMessage = onSubmitContactMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }

        // --- 6. AUTHENTIC BRAND FOOTER ---
        item {
            BrandFooter(
                onNavigateToCatalog = onNavigateToCatalog,
                onNavigateToCustomize = onNavigateToCustomize,
                onNavigateToJournal = onNavigateToJournalEditor
            )
        }
    }

    selectedInteriorPage?.let { page ->
        JournalInteriorPreviewDialog(
            page = page,
            onDismiss = { selectedInteriorPage = null }
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() }
            .testTag("quick_action_${title.replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = contentColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ProductHeroCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("featured_product_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Cover graphic with authentic cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.resolvedImageUrl())
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = product.resolvedFrontCoverRes()),
                    error = painterResource(id = product.resolvedFrontCoverRes()),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = WaveNavyDark.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = product.category.take(16).uppercase(),
                        color = WaveGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Info section
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = WaveNavyDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = WaveNavy
                    )
                    Surface(
                        color = if (product.inStock) WaveCyan.copy(alpha = 0.15f) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (product.inStock) "In Stock" else "Preorder",
                            color = if (product.inStock) WaveCyan else Color.Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Contact Section for the Homepage featuring Name, Email, and Message fields,
 * styled to match the official Write Your Wave brand aesthetic.
 */
@Composable
fun HomeContactSection(
    onSubmitMessage: (name: String, email: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.testTag("home_contact_section_card")
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                color = WaveGold.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.6f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("✉️", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GET IN TOUCH",
                        color = WaveNavyDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 1.1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Connect with Write Your Wave",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WaveNavyDark
            )

            Text(
                text = "Have a question about our $5.00 journals, campus distribution at Starz University, or production at WIT? Send us a direct message.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isSubmitted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Message Sent Successfully!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = "Thank you for reaching out. A campus representative will reply shortly.",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }

            if (!isSubmitted) {
                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Your Name *") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = WaveNavy)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = WaveNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_contact_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email Address *") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = WaveNavy)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = WaveNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_contact_email_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        errorMessage = null
                    },
                    label = { Text("Your Message *") },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = WaveNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_contact_message_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Please enter your name."
                        } else if (email.isBlank() || !email.contains("@")) {
                            errorMessage = "Please enter a valid email address."
                        } else if (message.isBlank()) {
                            errorMessage = "Please enter your message."
                        } else {
                            errorMessage = null
                            onSubmitMessage(name.trim(), email.trim(), message.trim())
                            isSubmitted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveNavy,
                        contentColor = WaveGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("home_contact_submit_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Message", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                OutlinedButton(
                    onClick = {
                        name = ""
                        email = ""
                        message = ""
                        isSubmitted = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, WaveNavy),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Another Message", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

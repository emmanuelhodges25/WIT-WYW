package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.JournalInteriorPage
import com.example.ui.theme.WaveBorderDark
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCrownBlack
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveError
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveLogoAzure
import com.example.ui.theme.WaveLogoRoyal
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveSuccess
import com.example.ui.theme.WaveWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaveTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    userEmail: String? = null,
    isAdmin: Boolean = false,
    onAdminClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    onAmbassadorClick: () -> Unit = {}
) {
    Surface(
        color = WaveNavyDark,
        shadowElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = WaveBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    // Official Logo Badge with Crown & Wave Brushwork
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WaveGold.copy(alpha = 0.6f)),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .height(34.dp)
                            .clickable(enabled = canNavigateBack, onClick = onNavigateBack)
                            .testTag("top_bar_official_logo")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.wyw_official_logo),
                            contentDescription = "Write Your Wave Official Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(34.dp)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(WaveLogoAzure)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "STATIONERY & JOURNALS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaveGold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            },
            actions = {
                // Ambassador Portal Shortcut
                IconButton(
                    onClick = onAmbassadorClick,
                    modifier = Modifier.testTag("ambassador_portal_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Ambassador Portal",
                        tint = WaveGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isAdmin) {
                    Surface(
                        color = WaveGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WaveGold.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable { onAdminClick() }
                            .padding(end = 4.dp)
                            .testTag("admin_dashboard_shortcut")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Admin Area",
                                tint = WaveGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaveGold
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("admin_dashboard_shortcut")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Admin Area",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onUserClick,
                    modifier = Modifier.testTag("user_account_shortcut")
                ) {
                    if (userEmail != null) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(WaveLogoAzure, WaveLogoRoyal)))
                                .border(1.5.dp, WaveGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userEmail.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Account",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = WaveNavyDark
            ),
            modifier = Modifier.testTag("wave_top_bar")
        )
    }
}

/**
 * Upgraded brand footer showcasing the authentic logo, mission statement,
 * and quick navigation links.
 */
@Composable
fun BrandFooter(
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToCustomize: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(WaveNavyDark, Color(0xFF040A14))
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Official Logo in Footer
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, WaveGold.copy(alpha = 0.6f)),
            shadowElevation = 6.dp,
            modifier = Modifier.testTag("footer_official_logo")
        ) {
            Image(
                painter = painterResource(id = R.drawable.wyw_official_logo),
                contentDescription = "Write Your Wave Brand Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "\"Ride the wave of your purpose and leave an enduring shore.\"",
            style = MaterialTheme.typography.bodyMedium,
            color = WaveGoldLight,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Write Your Wave is an authentic stationery and journal lifestyle movement designed for scholars, dreamers, creators, and future leaders.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Navigation Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shop Journals ($5.00)",
                color = WaveGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onNavigateToCatalog() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("footer_shop_link")
            )
            Text(text = "•", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            Text(
                text = "Design Cover",
                color = WaveGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onNavigateToCustomize() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("footer_design_link")
            )
            Text(text = "•", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            Text(
                text = "Daily Journal",
                color = WaveGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onNavigateToJournal() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("footer_journal_link")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Archival Paper Quality & Production Info Badge
        Surface(
            color = WaveNavy,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👑", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "120 GSM Acid-Free Bleed-Proof Paper • Gold Foil Stamping",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Serial verification number assigned to each journal after production",
                    fontSize = 9.sp,
                    color = WaveGoldLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Waterloop Institute of Technology acknowledgment
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, WaveGold.copy(alpha = 0.3f))
        ) {
            Text(
                text = "Platform Powered by Waterloop Institute of Technology (WIT)\nProduction Center: Airfield Shortcut, Sinkor",
                fontSize = 10.sp,
                color = WaveGold,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "© 2026 Write Your Wave. All Rights Reserved. Crafted with intentionality.",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "COMPLETED", "APPROVED", "PAID" -> WaveSuccess.copy(alpha = 0.15f) to WaveSuccess
        "CONFIRMED", "PROCESSING", "READY", "OUT_FOR_DELIVERY" -> WaveCyan.copy(alpha = 0.15f) to WaveCyan
        "PENDING", "SUBMITTED", "UNDER_REVIEW", "UNPAID" -> WaveWarning.copy(alpha = 0.15f) to WaveWarning
        "CANCELLED", "REJECTED", "FAILED" -> WaveError.copy(alpha = 0.15f) to WaveError
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status.replace("_", " "),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Full-screen high-resolution inspection modal for Front and Back covers
 */
@Composable
fun JournalCoverInspectionDialog(
    initialIsBackCover: Boolean = false,
    onDismiss: () -> Unit
) {
    var isBackCover by remember { mutableStateOf(initialIsBackCover) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f)),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top control bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (!isBackCover) "FRONT COVER INSPECTION" else "BACK COVER INSPECTION",
                            color = WaveGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = if (!isBackCover) "Write Your Wave Journal" else "2026/2027 Calendar & Series Guide",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .testTag("close_cover_inspection_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Front vs Back Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isBackCover = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isBackCover) WaveGold else Color.Transparent,
                            contentColor = if (!isBackCover) WaveNavyDark else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("dialog_front_cover_tab")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Front Cover", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { isBackCover = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBackCover) WaveGold else Color.Transparent,
                            contentColor = if (isBackCover) WaveNavyDark else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("dialog_back_cover_tab")
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back Cover", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image Viewer Container
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = WaveNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, WaveGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                ) {
                    Crossfade(targetState = isBackCover, label = "cover_crossfade") { backSelected ->
                        Image(
                            painter = painterResource(
                                id = if (backSelected) R.drawable.journal_back_cover else R.drawable.journal_front_cover
                            ),
                            contentDescription = if (backSelected) "Journal Back Cover" else "Journal Front Cover",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Information Panel
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!isBackCover) {
                            Text(
                                text = "✨ OFFICIAL FRONT COVER DETAILS",
                                color = WaveGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Movement Mantra: 'More Than A Notebook — It's A Movement. Plan. Learn. Create. Achieve.'\n" +
                                        "• Life Pillar Modules: STUDY • CAREER • FINANCIAL FREEDOM • SELF CARE • SUCCESS\n" +
                                        "• Dedication Banner: Gold-foiled personalized name plate (custom stamped for each order)\n" +
                                        "• Guiding Quotes: 'Big Dreams. Real Plans.' & 'Your Story Matters'",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        } else {
                            Text(
                                text = "📅 OFFICIAL BACK COVER & CALENDAR DETAILS",
                                color = WaveGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Reference Calendars: Complete 2026 & 2027 Year-at-a-Glance grids\n" +
                                        "• Liberia National Holidays: New Year's Day (Jan 1), Armed Forces Day (Feb 11), Decoration Day (Mar 11), J.J. Roberts Birthday (Mar 15), Fast & Prayer Day (Apr 10), National Unification Day (May 14), Independence Day (Jul 26), Flag Day (Aug 24), Thanksgiving (Nov 5), Tubman Birthday (Nov 29), Christmas Day (Dec 25)\n" +
                                        "• 4 Notebook Series: University Student, Student, Journal, Premium Journal\n" +
                                        "• Order Hotlines: WhatsApp (+231 77 046 5174) | Call (+231 88 646 5174)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveGold,
                        contentColor = WaveNavyDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("dialog_close_button")
                ) {
                    Text("Close High-Res Viewer", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Official Journal Cover Showcase Card with Front/Back Flip Toggle
 */
@Composable
fun OfficialJournalCoverShowcase(
    onPersonalizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBackCover by remember { mutableStateOf(false) }
    var showFullDialog by remember { mutableStateOf(false) }

    if (showFullDialog) {
        JournalCoverInspectionDialog(
            initialIsBackCover = isBackCover,
            onDismiss = { showFullDialog = false }
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WaveGold.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("official_journal_cover_showcase")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Surface(
                        color = WaveGold.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AUTHENTIC PHYSICAL JOURNAL",
                            color = WaveNavyDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (!isBackCover) "Official Front Cover" else "Official Back Cover & Calendar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                }

                // Flip button
                OutlinedButton(
                    onClick = { isBackCover = !isBackCover },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaveNavyLight),
                    modifier = Modifier.testTag("flip_cover_side_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (!isBackCover) "View Back" else "View Front",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cover Image with interactive click-to-zoom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    .clickable { showFullDialog = true }
                    .testTag("cover_showcase_image_box"),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(targetState = isBackCover, label = "showcase_crossfade") { backSelected ->
                    Image(
                        painter = painterResource(
                            id = if (backSelected) R.drawable.journal_back_cover else R.drawable.journal_front_cover
                        ),
                        contentDescription = if (backSelected) "Write Your Wave Back Cover" else "Write Your Wave Front Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Tap to zoom pill badge
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Inspect",
                            tint = WaveGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tap to Inspect High-Res",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Informative metadata row
            if (!isBackCover) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("✨ Gold Foil Banner", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                            Text("Personalized with your name", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("📚 5 Life Modules", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                            Text("Study, Career, Freedom, Care", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("📅 2026/2027 Calendar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                            Text("Liberia national holidays list", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("📞 Direct Order Desk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                            Text("+231 77 046 5174 (WhatsApp)", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showFullDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavyDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WaveNavyDark),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("showcase_inspect_button")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inspect Full-Screen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onPersonalizeClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveNavyDark,
                        contentColor = WaveGold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("showcase_personalize_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Personalize Mine", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Live 3D-like Notebook Cover visualizer with personalized text,
 * supporting toggling between the Authentic Front Cover (with custom nameplate overlay),
 * Authentic Back Cover (with 2026/2027 calendar), and the 3D leather prototype.
 */
@Composable
fun LiveCoverPreview(
    notebookTitle: String,
    nameOnCover: String,
    quote: String,
    baseColor: Color = WaveNavyDark,
    modifier: Modifier = Modifier
) {
    var previewMode by remember { mutableStateOf(0) } // 0: Authentic Front, 1: Authentic Back, 2: 3D Leather
    var showFullDialog by remember { mutableStateOf(false) }

    if (showFullDialog) {
        JournalCoverInspectionDialog(
            initialIsBackCover = (previewMode == 1),
            onDismiss = { showFullDialog = false }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Mode Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = (previewMode == 0),
                onClick = { previewMode = 0 },
                label = { Text("Authentic Front", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WaveNavyDark,
                    selectedLabelColor = WaveGold
                ),
                modifier = Modifier.testTag("preview_mode_authentic_front")
            )
            FilterChip(
                selected = (previewMode == 1),
                onClick = { previewMode = 1 },
                label = { Text("Calendar Back", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WaveNavyDark,
                    selectedLabelColor = WaveGold
                ),
                modifier = Modifier.testTag("preview_mode_authentic_back")
            )
            FilterChip(
                selected = (previewMode == 2),
                onClick = { previewMode = 2 },
                label = { Text("Leather Prototype", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WaveNavyDark,
                    selectedLabelColor = WaveGold
                ),
                modifier = Modifier.testTag("preview_mode_leather_prototype")
            )
        }

        when (previewMode) {
            0 -> {
                // Authentic Front Cover with Live Nameplate Overlay
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showFullDialog = true }
                        .testTag("authentic_front_preview_card")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.journal_front_cover),
                            contentDescription = "Authentic Write Your Wave Front Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Live Gold Foil Nameplate Overlay if name entered
                        if (nameOnCover.isNotBlank()) {
                            Surface(
                                color = WaveNavyDark.copy(alpha = 0.92f),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, WaveGold),
                                shape = RoundedCornerShape(20.dp),
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WaveGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = nameOnCover.uppercase(),
                                        color = WaveGoldLight,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WaveGold, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Bottom banner note
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(topStart = 8.dp),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = if (nameOnCover.isNotBlank()) "✨ Your Name Gold Stamped Live" else "Tap to inspect full cover",
                                color = WaveGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            1 -> {
                // Authentic Back Cover (2026/2027 Calendar & Series)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showFullDialog = true }
                        .testTag("authentic_back_preview_card")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.journal_back_cover),
                            contentDescription = "Authentic Write Your Wave Back Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(topStart = 8.dp),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = "📅 2026/2027 Calendar & Series Guide",
                                color = WaveGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            else -> {
                // 3D Leather Custom Mockup
                Card(
                    shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp, topEnd = 16.dp, bottomEnd = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = baseColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .shadow(12.dp, RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                        .testTag("live_cover_preview")
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Book Spine effect
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(260.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.6f),
                                            Color.White.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Book Front Cover content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "WRITE YOUR WAVE",
                                    color = WaveGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gold foil emblem",
                                    tint = WaveGoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Middle: Personalized name & title
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .border(1.5.dp, WaveGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🌊",
                                        fontSize = 26.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (nameOnCover.isNotBlank()) nameOnCover else "YOUR NAME HERE",
                                    color = WaveGoldLight,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notebookTitle,
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Bottom: Motivational Quote Foil
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (quote.isNotBlank()) "“$quote”" else "“Ride the wave of your purpose.”",
                                    color = WaveGold,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Journal Interior Preview Dialog showing internal pages
 */
@Composable
fun JournalInteriorPreviewDialog(
    page: JournalInteriorPage,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F0)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("interior_page_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = page.templateCategory.uppercase(),
                        color = WaveNavyLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "120 GSM LINED",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = WaveNavyDark
                )
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = WaveNavyLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notebook Ruled Sheet simulation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "GUIDED INTENTION PROMPT:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = WaveGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = page.prompt,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = WaveNavyDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    // Ruled lines
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xFF90CAF9).copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("dismiss_interior_preview_button")
                ) {
                    Text(
                        text = "Close Preview",
                        color = WaveNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

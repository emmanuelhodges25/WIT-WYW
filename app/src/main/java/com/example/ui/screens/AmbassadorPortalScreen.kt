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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmbassadorPayout
import com.example.data.model.AmbassadorProfile
import com.example.data.model.AmbassadorReferral
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WaveSuccess
import kotlinx.coroutines.launch

@Composable
fun AmbassadorPortalScreen(
    ambassadorProfile: AmbassadorProfile?,
    referrals: List<AmbassadorReferral>,
    payouts: List<AmbassadorPayout>,
    isSignedIn: Boolean,
    currentUserEmail: String?,
    onApply: suspend (name: String, email: String, phone: String, campus: String, social: String, audience: String) -> Result<String>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Referrals, 2: Payouts, 3: Resources

    // Form states if applying
    var applicantName by remember { mutableStateOf("") }
    var applicantEmail by remember { mutableStateOf(currentUserEmail ?: "") }
    var applicantPhone by remember { mutableStateOf("") }
    var applicantCampus by remember { mutableStateOf("Starz University (Airfield)") }
    var applicantSocial by remember { mutableStateOf("") }
    var applicantAudience by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var applicationSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("ambassador_portal_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- Header Banner ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WaveNavyDark)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Surface(
                    color = WaveGold,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "PARTNER & AMBASSADOR PORTAL",
                        color = WaveNavyDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Wave Ambassador Hub",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Champion youth purpose, inspire student scholars, and earn 10% on every verified journal referral.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        // --- Check Ambassador Application Status ---
        if (ambassadorProfile == null && !applicationSubmitted) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = WaveGold, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Join as a Brand Ambassador",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Represent Write Your Wave at Starz University, Waterloop Institute, or across your creative network. Earn $0.50 USD commission for each $5.00 journal purchased using your custom code.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = applicantName,
                            onValueChange = { applicantName = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("ambassador_apply_name")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = applicantEmail,
                            onValueChange = { applicantEmail = it },
                            label = { Text("Email Address *") },
                            modifier = Modifier.fillMaxWidth().testTag("ambassador_apply_email")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = applicantPhone,
                            onValueChange = { applicantPhone = it },
                            label = { Text("Phone Number / WhatsApp *") },
                            modifier = Modifier.fillMaxWidth().testTag("ambassador_apply_phone")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = applicantCampus,
                            onValueChange = { applicantCampus = it },
                            label = { Text("University / Campus Affiliation *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = applicantSocial,
                            onValueChange = { applicantSocial = it },
                            label = { Text("Social Handles (Instagram / TikTok / LinkedIn)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = applicantAudience,
                            onValueChange = { applicantAudience = it },
                            label = { Text("Audience / Why do you want to represent Write Your Wave? *") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (applicantName.isNotBlank() && applicantEmail.isNotBlank()) {
                                    isSubmitting = true
                                    coroutineScope.launch {
                                        onApply(
                                            applicantName,
                                            applicantEmail,
                                            applicantPhone,
                                            applicantCampus,
                                            applicantSocial,
                                            applicantAudience
                                        )
                                        isSubmitting = false
                                        applicationSubmitted = true
                                    }
                                }
                            },
                            enabled = !isSubmitting && applicantName.isNotBlank() && applicantEmail.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("ambassador_apply_submit")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Submit Ambassador Application", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else if (applicationSubmitted || (ambassadorProfile != null && ambassadorProfile.status == "PENDING")) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Application Under Review",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Thank you for applying to the Write Your Wave Ambassador Program! Our team at Waterloop Institute and Starz University is reviewing your profile. You will receive an email upon approval.",
                            fontSize = 13.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }
        } else if (ambassadorProfile != null) {
            // --- Approved Ambassador Dashboard ---
            item {
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
                        text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Referrals (${referrals.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Payouts (${payouts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Resources", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedTab == 0) {
                // Referral Link Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "YOUR EXCLUSIVE REFERRAL CODE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaveNavyLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ambassadorProfile.referralCode,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = WaveNavyDark
                                )
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(ambassadorProfile.referralCode))
                                        copiedNotice = "Code copied!"
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Code", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            val referralUrl = "https://write-your-wave.vercel.app/?ref=${ambassadorProfile.referralCode}"
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = referralUrl,
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(referralUrl))
                                            copiedNotice = "Referral link copied!"
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = WaveNavy, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Financial & Performance KPI Cards
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "COMMISSION & EARNINGS OVERVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AmbassadorMetricCard(
                                title = "Total Referrals",
                                value = "${ambassadorProfile.totalReferrals}",
                                subtitle = "$${String.format("%.2f", ambassadorProfile.totalSalesAmount)} gross",
                                modifier = Modifier.weight(1f)
                            )
                            AmbassadorMetricCard(
                                title = "Pending Commission",
                                value = "$${String.format("%.2f", ambassadorProfile.pendingCommissions)}",
                                subtitle = "Ready for batch payout",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AmbassadorMetricCard(
                                title = "Paid Out Commissions",
                                value = "$${String.format("%.2f", ambassadorProfile.paidCommissions)}",
                                subtitle = "Disbursed to date",
                                modifier = Modifier.weight(1f)
                            )
                            AmbassadorMetricCard(
                                title = "Commission Rate",
                                value = "${(ambassadorProfile.commissionRate * 100).toInt()}%",
                                subtitle = "$0.50 / journal",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // Referrals List
                if (referrals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = WaveNavyLight, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Referrals Yet", fontWeight = FontWeight.Bold, color = WaveNavyDark)
                                Text("Share your referral code '${ambassadorProfile.referralCode}' with fellow students to earn commissions.", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                } else {
                    items(referrals) { ref ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ref.productTitle.ifEmpty { "Write Your Wave Journal" }, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Order #${ref.orderReference} • ${ref.customerName}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Ref ID: ${ref.referralReference}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("+$${String.format("%.2f", ref.commissionAmount)}", fontWeight = FontWeight.Black, color = WaveSuccess, fontSize = 14.sp)
                                    Surface(
                                        color = if (ref.status == "PAID") WaveSuccess.copy(alpha = 0.15f) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = ref.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (ref.status == "PAID") WaveSuccess else Color(0xFF92400E),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (selectedTab == 2) {
                // Payouts List
                if (payouts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LocalAtm, contentDescription = null, tint = WaveNavyLight, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Payouts Disbursed Yet", fontWeight = FontWeight.Bold, color = WaveNavyDark)
                                Text("Commissions are paid out automatically via Mobile Money or student campus desk upon reaching $10.00.", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                } else {
                    items(payouts) { pay ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Payout #${pay.payoutReference}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Method: ${pay.payoutMethod}", fontSize = 11.sp, color = Color.Gray)
                                    if (pay.notes.isNotBlank()) {
                                        Text(pay.notes, fontSize = 10.sp, color = Color(0xFF64748B))
                                    }
                                }
                                Text("$${String.format("%.2f", pay.amount)}", fontWeight = FontWeight.Black, color = WaveNavyDark, fontSize = 15.sp)
                            }
                        }
                    }
                }
            } else if (selectedTab == 3) {
                // Promotional Resources
                item {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ResourceCard(
                            title = "Official Journal Cover Pack (1080x1920)",
                            description = "High-resolution mockups of Abiola Ocean Blue, Noir Gold, Purple & Gold, and Radiant Pink editions.",
                            badge = "Social Story Assets"
                        )
                        ResourceCard(
                            title = "Write Your Wave Pitch Guide & Talking Points",
                            description = "Syllabus mapping, 120gsm acid-free paper specs, and campus distribution details for Starz and WIT.",
                            badge = "Sales Playbook"
                        )
                        ResourceCard(
                            title = "Official Brand Logos & Badges (PNG)",
                            description = "Transparent high-resolution crown logo and verified movement emblems.",
                            badge = "Vector & PNG"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmbassadorMetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = WaveNavyDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun ResourceCard(
    title: String,
    description: String,
    badge: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFEEF2F6),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WaveNavy, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WaveNavyDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, fontSize = 11.sp, color = Color(0xFF64748B))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Download, contentDescription = "Download Asset", tint = WaveNavy)
            }
        }
    }
}

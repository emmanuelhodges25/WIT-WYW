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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.ui.components.AuthGateCard
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.example.ui.theme.WaveSuccess
import kotlinx.coroutines.launch

@Composable
fun BulkOrdersScreen(
    isSignedIn: Boolean,
    currentUserEmail: String?,
    onSubmitBulkOrder: suspend (
        orgName: String,
        contact: String,
        email: String,
        phone: String,
        quantity: Long,
        requirements: String?,
        deadline: String?
    ) -> Result<String>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var organizationName by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUserEmail ?: "") }
    var phone by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("100") }
    var customizationRequirements by remember { mutableStateOf("") }
    var deadlineDate by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var submittedId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val qty = quantityText.toLongOrNull() ?: 50L
    val discountPercent = when {
        qty >= 500 -> 35
        qty >= 100 -> 25
        qty >= 50 -> 15
        else -> 0
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("bulk_orders_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- Header ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WaveNavyDark)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Surface(
                    color = WaveGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "INSTITUTIONAL & BULK PROGRAM",
                        color = WaveGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Schools, Universities & Organizations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Equip student cohorts, leadership summits, and corporate teams with custom debossed Wave journals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        // --- Tiered Discounts Banner ---
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Volume Discount Tiers:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DiscountBadge(label = "50+ Copies", discount = "15% OFF", active = qty in 50..99)
                        DiscountBadge(label = "100+ Copies", discount = "25% OFF", active = qty in 100..499)
                        DiscountBadge(label = "500+ Copies", discount = "35% OFF", active = qty >= 500)
                    }
                }
            }
        }

        // --- Form ---
        item {
            if (!isSignedIn) {
                AuthGateCard(
                    title = "Sign in to Request Quotation",
                    subtitle = "Sign in with Google to register your institution's quotation directly into our administrative queue."
                )
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Quotation Request Form",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = organizationName,
                            onValueChange = { organizationName = it },
                            label = { Text("School / University / Organization Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("bulk_org_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Contact Person / Department Coordinator *") },
                            modifier = Modifier.fillMaxWidth().testTag("bulk_contact_person_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email *") },
                                modifier = Modifier.weight(1f).testTag("bulk_email_input"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone *") },
                                modifier = Modifier.weight(1f).testTag("bulk_phone_input"),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Estimated Quantity (e.g. 100) *") },
                            modifier = Modifier.fillMaxWidth().testTag("bulk_quantity_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = deadlineDate,
                            onValueChange = { deadlineDate = it },
                            label = { Text("Required Delivery Deadline (e.g. Fall Orientation, Nov 15)") },
                            modifier = Modifier.fillMaxWidth().testTag("bulk_deadline_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customizationRequirements,
                            onValueChange = { customizationRequirements = it },
                            label = { Text("Customization Specs (Campus crest debossing, custom syllabus insert)") },
                            modifier = Modifier.fillMaxWidth().testTag("bulk_customization_input")
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (organizationName.isBlank() || contactPerson.isBlank() || email.isBlank() || phone.isBlank()) {
                                    errorMessage = "Please fill in Organization Name, Contact Person, Email and Phone."
                                    return@Button
                                }
                                isSubmitting = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val res = onSubmitBulkOrder(
                                        organizationName.trim(),
                                        contactPerson.trim(),
                                        email.trim(),
                                        phone.trim(),
                                        qty,
                                        customizationRequirements.trim().ifEmpty { null },
                                        deadlineDate.trim().ifEmpty { null }
                                    )
                                    isSubmitting = false
                                    if (res.isSuccess) {
                                        submittedId = res.getOrThrow()
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Submission failed"
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_bulk_order_button")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Submit Bulk Quotation Request", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    submittedId?.let {
        androidx.compose.ui.window.Dialog(onDismissRequest = { submittedId = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("bulk_success_dialog")
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
                        Icon(Icons.Default.Check, contentDescription = "Success", tint = WaveSuccess, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Quotation Request Submitted!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We have received the inquiry for $organizationName ($qty units). Our institutional partnerships team will prepare your formal quotation within 24 hours.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { submittedId = null },
                        colors = ButtonDefaults.buttonColors(containerColor = WaveNavy),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DiscountBadge(label: String, discount: String, active: Boolean) {
    Surface(
        color = if (active) WaveGold else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) WaveNavyDark else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (active) WaveNavyDark else Color.DarkGray
            )
            Text(
                text = discount,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (active) WaveNavyDark else WaveNavy
            )
        }
    }
}

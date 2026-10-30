package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.components.JournalCoverInspectionDialog
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
fun ContactScreen(
    isSignedIn: Boolean,
    currentUserEmail: String?,
    onSubmitMessage: suspend (name: String, email: String, subject: String, category: String, message: String) -> Result<String>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUserEmail ?: "") }
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General inquiry") }
    var message by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var submittedId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showInspectionDialog by remember { mutableStateOf(false) }

    if (showInspectionDialog) {
        JournalCoverInspectionDialog(
            initialIsBackCover = true,
            onDismiss = { showInspectionDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("contact_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
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
                        text = "COMMUNICATION & INQUIRIES",
                        color = WaveGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Contact Write Your Wave",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Inquiries, campus book fairs, ambassador applications, and feedback.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        // --- Official Printed Back Cover Channels ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, WaveGold.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("official_contact_channels_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "OFFICIAL ORDER DESK",
                                color = WaveNavyDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Directly from the Journal Back Cover",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showInspectionDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("inspect_back_cover_contact_button")
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Cover", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = WaveNavy, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("WhatsApp", fontSize = 10.sp, color = Color.Gray)
                                    Text("+231 77 046 5174", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                                }
                            }
                        }

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = WaveNavy, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Direct Line", fontSize = 10.sp, color = Color.Gray)
                                    Text("+231 88 646 5174", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WaveNavyDark)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            if (!isSignedIn) {
                AuthGateCard(
                    title = "Sign in to Send Message",
                    subtitle = "Sign in with Google to transmit your validated inquiry directly to our customer operations desk."
                )
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Send a Direct Inquiry",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Your Name *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("contact_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("contact_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("contact_subject_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Message *") },
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth().testTag("contact_message_input")
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
                                if (name.isBlank() || email.isBlank() || subject.isBlank() || message.isBlank()) {
                                    errorMessage = "Please fill in all fields."
                                    return@Button
                                }
                                isSubmitting = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val res = onSubmitMessage(
                                        name.trim(),
                                        email.trim(),
                                        subject.trim(),
                                        category,
                                        message.trim()
                                    )
                                    isSubmitting = false
                                    if (res.isSuccess) {
                                        submittedId = res.getOrThrow()
                                        subject = ""
                                        message = ""
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Failed to submit message"
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = WaveNavy, contentColor = WaveGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_contact_button")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Transmit Inquiry", fontWeight = FontWeight.Bold)
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
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(WaveSuccess.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = WaveSuccess, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Inquiry Received", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WaveNavyDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Thank you for reaching out to Write Your Wave. We will respond promptly.", fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { submittedId = null }, colors = ButtonDefaults.buttonColors(containerColor = WaveNavy)) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

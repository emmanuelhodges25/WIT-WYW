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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Palette
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
import com.example.data.ai.GeminiService
import com.example.data.model.Product
import com.example.ui.components.AuthGateCard
import com.example.ui.components.LiveCoverPreview
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

data class CoverColorOption(
    val name: String,
    val color: Color
)

@Composable
fun CustomizeScreen(
    products: List<Product>,
    preselectedProduct: Product?,
    isSignedIn: Boolean,
    currentUserEmail: String?,
    geminiService: GeminiService,
    onSubmitCustomization: suspend (
        productId: String,
        name: String,
        email: String,
        phone: String?,
        nameOnCover: String,
        colors: String?,
        quote: String?,
        instructions: String?
    ) -> Result<String>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val colorOptions = listOf(
        CoverColorOption("Midnight Navy", WaveNavyDark),
        CoverColorOption("Oceanic Wave", WaveNavyLight),
        CoverColorOption("Deep Emerald", Color(0xFF0F3A2B)),
        CoverColorOption("Obsidian Black", Color(0xFF1A1A1A)),
        CoverColorOption("Imperial Gold", Color(0xFF5A440E))
    )

    var selectedColor by remember { mutableStateOf(colorOptions[0]) }
    var selectedProduct by remember { mutableStateOf(preselectedProduct ?: products.firstOrNull()) }
    var nameOnCover by remember { mutableStateOf("") }
    var motivationalQuote by remember { mutableStateOf("Ride the wave of your purpose") }
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf(currentUserEmail ?: "") }
    var customerPhone by remember { mutableStateOf("") }
    var customInstructions by remember { mutableStateOf("") }

    // AI Quote suggestions state
    var isGeneratingQuotes by remember { mutableStateOf(false) }
    var aiQuoteSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    var isSubmitting by remember { mutableStateOf(false) }
    var submittedRequestId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("customize_screen_lazy_column"),
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
                        text = "GOLD FOIL PERSONALIZATION",
                        color = WaveGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Craft Your Signature Cover",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Your name, custom quote, and bespoke color combination stamped in shimmering gold foil.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        // --- Live 3D Cover Preview ---
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "REAL-TIME PREVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = WaveNavyLight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LiveCoverPreview(
                    notebookTitle = selectedProduct?.title ?: "Write Your Wave Signature Journal",
                    nameOnCover = nameOnCover,
                    quote = motivationalQuote,
                    baseColor = selectedColor.color
                )
            }
        }

        // --- Color Selection ---
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = WaveNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Select Cover Palette: ${selectedColor.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = WaveNavyDark
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorOptions.forEach { opt ->
                        val isChosen = selectedColor == opt
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(opt.color)
                                .border(
                                    width = if (isChosen) 3.dp else 1.dp,
                                    color = if (isChosen) WaveGold else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = opt }
                                .testTag("color_option_${opt.name.replace(" ", "_")}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChosen) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = WaveGold, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // --- Text Customization Form ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Customization Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameOnCover,
                        onValueChange = { nameOnCover = it },
                        label = { Text("Name to Appear on Cover (Uppercase Gold Foil) *") },
                        placeholder = { Text("e.g. KOFI MENSAH") },
                        modifier = Modifier.fillMaxWidth().testTag("name_on_cover_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = motivationalQuote,
                        onValueChange = { motivationalQuote = it },
                        label = { Text("Motivational Quote / Dedication *") },
                        placeholder = { Text("e.g. Lead tomorrow, write today.") },
                        modifier = Modifier.fillMaxWidth().testTag("motivational_quote_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // AI Quote suggestions button using gemini-3.1-flash-lite
                    OutlinedButton(
                        onClick = {
                            isGeneratingQuotes = true
                            coroutineScope.launch {
                                val res = geminiService.generateCoverQuotes("Student leadership, growth mindset, ambition")
                                isGeneratingQuotes = false
                                if (res.isSuccess) {
                                    aiQuoteSuggestions = res.getOrThrow()
                                }
                            }
                        },
                        enabled = !isGeneratingQuotes,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_quote_generator_button")
                    ) {
                        if (isGeneratingQuotes) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating with Gemini...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WaveGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate AI Inspiration Quotes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (aiQuoteSuggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tap to apply quote:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        aiQuoteSuggestions.forEach { suggestion ->
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { motivationalQuote = suggestion }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.FormatQuote, contentDescription = null, tint = WaveGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = suggestion,
                                        fontSize = 12.sp,
                                        color = WaveNavyDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = customInstructions,
                        onValueChange = { customInstructions = it },
                        label = { Text("Special Design Instructions (Font style, layout notes)") },
                        modifier = Modifier.fillMaxWidth().testTag("custom_instructions_input")
                    )
                }
            }
        }

        // --- Contact / Auth & Submit ---
        item {
            if (!isSignedIn) {
                AuthGateCard(
                    title = "Sign in to Submit Customization",
                    subtitle = "Sign in with Google to associate this bespoke design with your verified account in Firestore."
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
                            text = "Submit Your Personalization Proof",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Your Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_customer_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Email Address *") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_customer_email_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone / WhatsApp (Optional)") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_customer_phone_input"),
                            singleLine = true
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
                                if (nameOnCover.isBlank() || customerName.isBlank() || customerEmail.isBlank()) {
                                    errorMessage = "Please enter Name on Cover, Full Name, and Email."
                                    return@Button
                                }
                                isSubmitting = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val prodId = selectedProduct?.id ?: "prod_daily_wave"
                                    val res = onSubmitCustomization(
                                        prodId,
                                        customerName.trim(),
                                        customerEmail.trim(),
                                        customerPhone.trim().ifEmpty { null },
                                        nameOnCover.trim().uppercase(),
                                        selectedColor.name,
                                        motivationalQuote.trim().ifEmpty { null },
                                        customInstructions.trim().ifEmpty { null }
                                    )
                                    isSubmitting = false
                                    if (res.isSuccess) {
                                        submittedRequestId = res.getOrThrow()
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Submission failed"
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
                                .testTag("submit_customization_request_button")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = WaveGold, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Submit Customization Request", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Success dialog
    submittedRequestId?.let { id ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { submittedRequestId = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("customization_success_dialog")
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
                        text = "Customization Proof Submitted!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaveNavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Our master bookbinders have received your personalization request for '$nameOnCover'. You will be notified in your orders tab.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { submittedRequestId = null },
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

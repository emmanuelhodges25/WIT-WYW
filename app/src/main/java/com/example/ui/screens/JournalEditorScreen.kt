package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiService
import com.example.data.model.JournalEntry
import com.example.data.repository.WriteYourWaveRepository
import com.example.ui.components.AuthGateCard
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PillarInfo(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val description: String,
    val accentColor: Color
)

val LIFE_PILLARS = listOf(
    PillarInfo(
        id = "Study",
        name = "Study & Academics",
        icon = Icons.Default.School,
        description = "GPA milestones, lecture breakthroughs & intellectual mastery",
        accentColor = WaveGold
    ),
    PillarInfo(
        id = "Career",
        name = "Career & Purpose",
        icon = Icons.Default.Work,
        description = "Internships, networking, resume evolution & visionary leadership",
        accentColor = WaveCyan
    ),
    PillarInfo(
        id = "Financial Freedom",
        name = "Financial Freedom",
        icon = Icons.Default.WorkspacePremium,
        description = "Budget discipline, compounding wealth & emergency buffer",
        accentColor = Color(0xFF66BB6A)
    ),
    PillarInfo(
        id = "Self Care",
        name = "Self Care & Wellness",
        icon = Icons.Default.SelfImprovement,
        description = "Mental serenity, physical vitality, sleep & sacred boundaries",
        accentColor = Color(0xFFAB47BC)
    ),
    PillarInfo(
        id = "Success",
        name = "Success & Milestones",
        icon = Icons.Default.Star,
        description = "Quiet victories, habit streaks, gratitude & generational legacy",
        accentColor = Color(0xFFFFB300)
    )
)

val MOOD_TAGS = listOf(
    "🎯 Focused",
    "💡 Inspired",
    "🙏 Grateful",
    "🌊 Calm",
    "⚡ Ambitious",
    "🔍 Reflective"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditor(
    repository: WriteYourWaveRepository,
    geminiService: GeminiService,
    isSignedIn: Boolean,
    currentUser: FirebaseUser?,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Form state
    var currentEntryId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf("Study") }
    var selectedMood by remember { mutableStateOf("Focused") }
    var activePrompt by remember { mutableStateOf("What intellectual or personal breakthrough are you documenting today?") }
    var isFavorite by remember { mutableStateOf(false) }

    // UI statuses
    var isSaving by remember { mutableStateOf(false) }
    var isGeneratingPrompt by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<JournalEntry?>(null) }

    // Real-time metrics
    val wordCount by remember(content) {
        derivedStateOf {
            content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
        }
    }
    val readingTimeMinutes by remember(wordCount) {
        derivedStateOf {
            maxOf(1, (wordCount + 199) / 200)
        }
    }

    // Observe user entries
    val userEntriesFlow = remember(currentUser?.uid) {
        if (currentUser != null) repository.observeUserJournalEntries(currentUser.uid)
        else MutableStateFlow(emptyList())
    }
    val userEntries by userEntriesFlow.collectAsState(initial = emptyList())

    val activePillarInfo = remember(selectedPillar) {
        LIFE_PILLARS.firstOrNull { it.id.equals(selectedPillar, ignoreCase = true) } ?: LIFE_PILLARS[0]
    }

    fun loadEntryForEditing(entry: JournalEntry) {
        currentEntryId = entry.id
        title = entry.title
        content = entry.content
        selectedPillar = entry.pillar
        selectedMood = entry.mood
        activePrompt = entry.prompt
        isFavorite = entry.isFavorite
        showHistorySheet = false
        statusMessage = "Loaded: \"${entry.title}\""
    }

    fun resetToFreshEntry() {
        currentEntryId = ""
        title = ""
        content = ""
        isFavorite = false
        statusMessage = "New blank canvas ready"
    }

    fun handleSave() {
        if (!isSignedIn) {
            Toast.makeText(context, "Please sign in to save your entries to Firestore", Toast.LENGTH_SHORT).show()
            return
        }
        if (title.isBlank() && content.isBlank()) {
            Toast.makeText(context, "Please write a title or reflection before saving", Toast.LENGTH_SHORT).show()
            return
        }

        isSaving = true
        scope.launch {
            val entryToSave = JournalEntry(
                id = currentEntryId,
                userId = currentUser?.uid ?: "",
                title = title.ifBlank { "Wave Reflection • ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())}" },
                content = content,
                pillar = selectedPillar,
                prompt = activePrompt,
                mood = selectedMood,
                wordCount = wordCount.toLong(),
                readingTimeMinutes = readingTimeMinutes.toLong(),
                isFavorite = isFavorite
            )

            val result = repository.saveJournalEntry(entryToSave)
            isSaving = false

            result.onSuccess { saved ->
                currentEntryId = saved.id
                statusMessage = "Saved to cloud • ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
                Toast.makeText(context, "Journal entry saved to Firestore!", Toast.LENGTH_SHORT).show()
            }.onFailure { err ->
                statusMessage = "Error saving: ${err.localizedMessage}"
                Toast.makeText(context, "Failed to save: ${err.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun requestAIPrompt() {
        isGeneratingPrompt = true
        scope.launch {
            val result = geminiService.generateWritingPrompt(selectedPillar, title)
            isGeneratingPrompt = false
            result.onSuccess { prompt ->
                activePrompt = prompt
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WaveNavyDark)
            .testTag("journal_editor_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header with navigation, title & action pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("journal_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WaveGold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Journal Editor",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = if (currentEntryId.isBlank()) "New Reflection" else "Editing Entry",
                            fontSize = 12.sp,
                            color = WaveGold.copy(alpha = 0.85f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // History button
                    OutlinedButton(
                        onClick = { showHistorySheet = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WaveGold),
                        border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("journal_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Entries History",
                            modifier = Modifier.size(16.dp),
                            tint = WaveGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Past (${userEntries.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // New entry button
                    IconButton(
                        onClick = { resetToFreshEntry() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(WaveNavyLight, CircleShape)
                            .testTag("journal_new_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Start New Blank Entry",
                            tint = WaveGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auth gate notification banner if guest
            if (!isSignedIn) {
                AuthGateCard(
                    title = "Sign in to Save to Cloud",
                    subtitle = "Sign in with your Google account to sync your journal entries across devices and securely back them up in Firestore."
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Life Pillars Selector Chips
            Text(
                text = "SELECT LIFE PILLAR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WaveGold.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LIFE_PILLARS.forEach { pillar ->
                    val isSelected = selectedPillar.equals(pillar.id, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedPillar = pillar.id
                            // Generate a fitting prompt for this pillar
                            scope.launch {
                                val p = geminiService.generateWritingPrompt(pillar.id, title)
                                p.onSuccess { activePrompt = it }
                            }
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = pillar.icon,
                                    contentDescription = pillar.name,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) WaveNavyDark else pillar.accentColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = pillar.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = pillar.accentColor,
                            selectedLabelColor = WaveNavyDark,
                            containerColor = WaveCardDark,
                            labelColor = Color.White.copy(alpha = 0.85f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = pillar.accentColor.copy(alpha = 0.4f),
                            selectedBorderColor = pillar.accentColor
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("pillar_chip_${pillar.id.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inspirational Prompt Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WaveNavy),
                border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("journal_prompt_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = WaveGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GUIDED REFLECTION PROMPT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaveGold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // AI prompt refresh button
                        TextButton(
                            onClick = { requestAIPrompt() },
                            enabled = !isGeneratingPrompt,
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("journal_refresh_prompt_button")
                        ) {
                            if (isGeneratingPrompt) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = WaveGold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Inspire Prompt",
                                    tint = WaveGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Inspire",
                                    fontSize = 11.sp,
                                    color = WaveGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activePrompt,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.95f),
                        lineHeight = 20.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tactile Notebook Canvas Area
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WaveParchment),
                border = BorderStroke(1.5.dp, WaveGold.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("journal_notebook_canvas")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Margin header decoration with Gold wave bar & Mood selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Pillar Badge & Notebook Date
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(24.dp)
                                    .background(activePillarInfo.accentColor, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${activePillarInfo.name} • ${SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(Date())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WaveNavyDark.copy(alpha = 0.75f)
                            )
                        }

                        // Right: Favorite toggle
                        IconButton(
                            onClick = { isFavorite = !isFavorite },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("journal_favorite_toggle")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isFavorite) "Bookmarked" else "Not bookmarked",
                                tint = if (isFavorite) WaveNavyDark else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title Input Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = {
                            Text(
                                text = "Entry title (e.g. Navigating My Finals Strategy...)",
                                color = WaveNavyDark.copy(alpha = 0.45f),
                                fontStyle = FontStyle.Italic
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaveNavyDark,
                            fontFamily = FontFamily.Serif
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaveNavy,
                            unfocusedBorderColor = WaveNavy.copy(alpha = 0.15f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("journal_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mood selector chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MOOD_TAGS.forEach { mood ->
                            val isSelected = selectedMood == mood.substringAfter(" ")
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) WaveNavy else Color.White.copy(alpha = 0.8f),
                                border = BorderStroke(1.dp, if (isSelected) WaveNavy else Color.LightGray.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clickable { selectedMood = mood.substringAfter(" ") }
                                    .testTag("journal_mood_${mood.substringAfter(" ").lowercase()}")
                            ) {
                                Text(
                                    text = mood,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) WaveGold else WaveNavyDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = WaveNavyDark.copy(alpha = 0.1f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Multiline Content TextArea
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = {
                            Text(
                                text = "Pour your thoughts into the page. Reflect on your progress, document your milestones, express gratitude, or draft your upcoming action plan...",
                                color = WaveNavyDark.copy(alpha = 0.45f),
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 15.sp,
                            color = WaveNavyDark,
                            lineHeight = 24.sp
                        ),
                        minLines = 12,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaveNavy.copy(alpha = 0.2f),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 280.dp)
                            .testTag("journal_content_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = WaveNavyDark.copy(alpha = 0.1f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Notebook Footer Metrics (Word count, read time, character counter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = WaveNavyDark.copy(alpha = 0.6f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$wordCount words",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = WaveNavyDark.copy(alpha = 0.7f),
                                modifier = Modifier.testTag("journal_word_count_text")
                            )

                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = WaveNavyDark.copy(alpha = 0.6f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "~$readingTimeMinutes min read",
                                fontSize = 12.sp,
                                color = WaveNavyDark.copy(alpha = 0.7f)
                            )
                        }

                        Text(
                            text = "${content.length} chars",
                            fontSize = 11.sp,
                            color = WaveNavyDark.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Save & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save Button (Primary)
                Button(
                    onClick = { handleSave() },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WaveGold,
                        contentColor = WaveNavyDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("journal_save_button")
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = WaveNavyDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saving to Firestore...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Entry",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentEntryId.isBlank()) "Save Entry" else "Update Entry",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Status message
            if (statusMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = WaveGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusMessage!!,
                        fontSize = 12.sp,
                        color = WaveGold,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Recent Entries Showcase
            if (userEntries.isNotEmpty()) {
                Text(
                    text = "YOUR RECENT ENTRIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WaveGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                userEntries.take(3).forEach { entry ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WaveNavy),
                        border = BorderStroke(1.dp, WaveGold.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { loadEntryForEditing(entry) }
                            .testTag("recent_entry_${entry.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = WaveGold.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = entry.pillar,
                                            fontSize = 10.sp,
                                            color = WaveGold,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (entry.mood.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = entry.mood,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = entry.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = entry.content,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.65f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${entry.wordCount} words",
                                    fontSize = 11.sp,
                                    color = WaveGold.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = WaveGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Modal Bottom Sheet: Full History and Management
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            containerColor = WaveNavyDark,
            contentColor = Color.White,
            modifier = Modifier.testTag("journal_history_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Wave Reflections",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "${userEntries.size} entries stored in Firestore",
                            fontSize = 12.sp,
                            color = WaveGold
                        )
                    }

                    IconButton(
                        onClick = { showHistorySheet = false },
                        modifier = Modifier.testTag("close_history_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (userEntries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = WaveGold.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No entries yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Begin writing above and click Save to store your reflections.",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(userEntries, key = { it.id }) { entry ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = WaveNavy),
                                border = BorderStroke(
                                    1.dp,
                                    if (entry.id == currentEntryId) WaveGold else WaveGold.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { loadEntryForEditing(entry) }
                                    .testTag("history_item_${entry.id}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = WaveGold.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = entry.pillar,
                                                fontSize = 10.sp,
                                                color = WaveGold,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${entry.wordCount} words",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { entryToDelete = entry },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .testTag("delete_entry_${entry.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete entry",
                                                    tint = Color(0xFFEF5350),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = entry.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = entry.content,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = {
                Text(
                    text = "Delete Reflection?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${entryToDelete?.title}\"? This action cannot be undone.",
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val entry = entryToDelete
                        entryToDelete = null
                        if (entry != null) {
                            scope.launch {
                                repository.deleteJournalEntry(entry.id)
                                if (currentEntryId == entry.id) {
                                    resetToFreshEntry()
                                }
                                Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { entryToDelete = null },
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text("Cancel", color = WaveGold)
                }
            },
            containerColor = WaveNavyDark
        )
    }
}

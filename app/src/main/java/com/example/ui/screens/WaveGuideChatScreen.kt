package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiService
import com.example.ui.theme.WaveCardDark
import com.example.ui.theme.WaveCyan
import com.example.ui.theme.WaveGold
import com.example.ui.theme.WaveGoldLight
import com.example.ui.theme.WaveNavy
import com.example.ui.theme.WaveNavyDark
import com.example.ui.theme.WaveNavyLight
import com.example.ui.theme.WaveParchment
import kotlinx.coroutines.launch

@Composable
fun WaveGuideChatScreen(
    geminiService: GeminiService,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "assistant",
                text = "Welcome to Write Your Wave. I am WaveGuide, your dedicated stationery curator and reflective journaling advisor. Are you looking for the ideal university lecture notebook, custom gold-foil cover quotes, or guided reflection frameworks?"
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isThinkingModeEnabled by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    val quickPrompts = listOf(
        "Which notebook is best for STEM college lectures?",
        "Give me 3 guided reflection prompts for tonight",
        "How does the custom gold-foil debossing process work?",
        "Recommend a bulk program for our university club"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WaveParchment)
            .testTag("waveguide_chat_screen_root")
    ) {
        // --- Header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WaveNavyDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WaveGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "WaveGuide AI",
                            tint = WaveNavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WaveGuide AI Assistant",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isThinkingModeEnabled) "Model: gemini-4-argon (Thinking: HIGH)" else "Model: gemini-3.8-flash (Real-time)",
                            fontSize = 11.sp,
                            color = WaveGold
                        )
                    }
                }

                // High Thinking Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Thinking Mode",
                        tint = if (isThinkingModeEnabled) WaveGold else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = isThinkingModeEnabled,
                        onCheckedChange = { isThinkingModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WaveNavyDark,
                            checkedTrackColor = WaveGold,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("high_thinking_toggle")
                    )
                }
            }
        }

        // --- Quick Prompts ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(WaveNavy)
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, WaveGold.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable {
                        inputText = prompt
                    }
                ) {
                    Text(
                        text = prompt,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Messages Thread ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }

            if (isSending) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = WaveNavy
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isThinkingModeEnabled) "Deep thinking with gemini-4-argon..." else "WaveGuide is composing...",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Input Bar ---
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(bottom = 70.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask about notebooks, quotes, guidance...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WaveNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val prompt = inputText.trim()
                        if (prompt.isNotBlank() && !isSending) {
                            val userMsg = ChatMessage(sender = "user", text = prompt)
                            messages.add(userMsg)
                            inputText = ""
                            isSending = true

                            coroutineScope.launch {
                                val result = geminiService.chatWithWaveGuide(
                                    history = messages.toList(),
                                    userMessage = prompt,
                                    enableHighThinking = isThinkingModeEnabled
                                )
                                isSending = false
                                if (result.isSuccess) {
                                    messages.add(result.getOrThrow())
                                } else {
                                    messages.add(
                                        ChatMessage(
                                            sender = "assistant",
                                            text = "I encountered an error connecting to WaveGuide AI: ${result.exceptionOrNull()?.localizedMessage}. Please verify GEMINI_API_KEY is configured."
                                        )
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(WaveNavy)
                        .testTag("send_chat_message_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = WaveGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(WaveNavyDark),
                contentAlignment = Alignment.Center
            ) {
                Text("🌊", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) WaveNavy else Color.White
            ),
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser && message.isThinking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "High Thinking",
                            tint = WaveGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HIGH THINKING REASONED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaveGold
                        )
                    }
                }

                Text(
                    text = message.text,
                    fontSize = 13.5.sp,
                    color = if (isUser) Color.White else WaveNavyDark,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

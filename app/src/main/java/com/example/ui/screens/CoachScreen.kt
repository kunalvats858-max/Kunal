package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.ConvictionViewModel
import com.example.ui.theme.ConvictionCardBg
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed
import com.example.ui.theme.ConvictionTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScreen(
    viewModel: ConvictionViewModel,
    innerPadding: PaddingValues
) {
    val messages by viewModel.allMessages.collectAsState()
    val isLoading by viewModel.isModelLoading.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    // Interactive Pre-Set Prompts to get rapid value
    val presets = listOf(
        PresetPrompt("🥊 MMA Plan", "Construct an MMA weekly training schedule blending Muay Thai strikes and Wrestling takedowns."),
        PresetPrompt("🧘 Break Weakness", "Suggest a daily mental discipline ritual to break procrastination and build mental toughness."),
        PresetPrompt("🥋 BJJ Drills", "Can you guide me through a solo BJJ solo movement sequence for beginners?")
    )

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title and clear history button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Coach Mind",
                    tint = ConvictionGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SENSEI AI",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Personal Grandmaster & Discipline Coach",
                        color = ConvictionTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(onClick = { viewModel.clearChat() }) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Session",
                    tint = ConvictionPrimaryRed
                )
            }
        }

        // Quick suggestions
        if (messages.size <= 1) { // Only show presets in first loads or clean chats
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    "Select a topic to query Sensei:",
                    fontSize = 11.sp,
                    color = ConvictionTextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ConvictionCardBg)
                                .clickable {
                                    viewModel.sendMessage(preset.prompt)
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.title,
                                color = ConvictionGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("preset_" + preset.title.replace(" ", "_"))
                            )
                        }
                    }
                }
            }
        }

        // Chat Conversation Stream
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Placeholder",
                            tint = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            "Bow before your master. Submit your first training query.",
                            color = ConvictionTextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }
                }
            }
        }

        // Loading indicator
        AnimatedVisibility(visible = isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = ConvictionGold,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sensei is analyzing your query...",
                    color = ConvictionGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Message input controllers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Consult with Sensei...", color = ConvictionTextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendMessage(textInput.trim())
                            textInput = ""
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = ConvictionGold,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = ConvictionCardBg,
                    unfocusedContainerColor = ConvictionCardBg
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("sensei_chat_input"),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendMessage(textInput.trim())
                        textInput = ""
                    }
                },
                containerColor = ConvictionGold,
                contentColor = Color.Black,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .size(46.dp)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Transmit query",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isSensei = message.sender == "sensei"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isSensei) 0.dp else 40.dp,
                end = if (isSensei) 40.dp else 0.dp
            ),
        contentAlignment = if (isSensei) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Column {
            Text(
                text = if (isSensei) "SENSEI" else "YOU",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSensei) ConvictionGold else ConvictionPrimaryRed,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
            
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isSensei) 0.dp else 16.dp,
                            bottomEnd = if (isSensei) 16.dp else 0.dp
                        )
                    )
                    .background(
                        if (isSensei) ConvictionCardBg else ConvictionPrimaryRed.copy(alpha = 0.15f)
                    )
                    .then(
                        if (!isSensei) Modifier.border(1.dp, ConvictionPrimaryRed.copy(alpha = 0.3f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 0.dp))
                        else Modifier.border(1.dp, ConvictionGold.copy(alpha = 0.15f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 16.dp))
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

data class PresetPrompt(val title: String, val prompt: String)

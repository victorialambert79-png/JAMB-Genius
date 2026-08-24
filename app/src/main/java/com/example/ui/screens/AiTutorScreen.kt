package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiChatMessage
import com.example.data.model.UserProfile
import com.example.data.repository.JambRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiTutorScreen(
    subjectId: String,
    topicId: String,
    repository: JambRepository,
    onUpgradeClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val chatMessages by repository.getChatMessagesForTopic(topicId).collectAsState(initial = emptyList())

    val topic by repository.getTopicById(topicId).collectAsState(initial = null)
    val lesson by repository.getLessonForTopic(topicId).collectAsState(initial = null)

    var inputQuery by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val profile = userProfile ?: UserProfile()
    val isPro = profile.subscriptionTier == "pro"

    val quickPrompts = listOf(
        Pair("💡 Explain simply", "SIMPLIFY"),
        Pair("📝 Worked example", "EXAMPLE"),
        Pair("🎯 Test my understanding", "TEST_ME"),
        Pair("🔍 Give me a hint", "HINT"),
        Pair("📋 Summarize key points", "SUMMARIZE")
    )

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Topic Context & Subscription Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalBlue50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = topic?.title ?: "JAMB Socratic AI Tutor",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Text(
                                text = if (isPro) "PRO Active • Unlimited Socratic Reasoning ⚡" else "Free Plan • 5 Daily Queries Limit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isPro) Emerald600 else Amber700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    if (!isPro) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Amber50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { onUpgradeClick() }
                        ) {
                            Text(
                                text = "Get Pro",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Amber700
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Initial Welcome Message if empty
            if (chatMessages.isEmpty()) {
                item {
                    AiMessageBubble(
                        message = AiChatMessage(
                            senderRole = "assistant",
                            messageText = "Hello ${profile.name.split(" ").firstOrNull() ?: "there"}! I'm your JAMB Genius AI Personal Tutor.\n\nI can explain complex concepts using simple everyday Nigerian examples, break down difficult formulas step-by-step, give you hints without giving away answers, or test your understanding with authentic JAMB-style questions.\n\nHow can I help you excel in **${topic?.title ?: "your JAMB subjects"}** today?"
                        ),
                        onUpgradeClick = onUpgradeClick
                    )
                }
            }

            items(chatMessages) { msg ->
                AiMessageBubble(message = msg, onUpgradeClick = onUpgradeClick)
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = RoyalBlue600
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tutor is analyzing syllabus concepts...",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                        )
                    }
                }
            }
        }

        // Quick Prompt Action Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { (label, actionType) ->
                SuggestionChip(
                    onClick = {
                        if (!isThinking) {
                            isThinking = true
                            coroutineScope.launch {
                                repository.askAiTutor(
                                    userQuery = label,
                                    subjectName = "JAMB Subject",
                                    topicName = topic?.title ?: "General Syllabus",
                                    lessonContext = lesson?.contentMarkdown ?: "",
                                    actionType = actionType,
                                    topicId = topicId
                                )
                                isThinking = false
                            }
                        }
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Slate100,
                        labelColor = Slate800
                    ),
                    border = null
                )
            }
        }

        // Bottom Input Field
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("Ask your personal tutor anything...") },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_tutor_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank() && !isThinking) {
                            val q = inputQuery.trim()
                            inputQuery = ""
                            isThinking = true
                            coroutineScope.launch {
                                repository.askAiTutor(
                                    userQuery = q,
                                    subjectName = "JAMB Subject",
                                    topicName = topic?.title ?: "General Syllabus",
                                    lessonContext = lesson?.contentMarkdown ?: "",
                                    actionType = "CHAT",
                                    topicId = topicId
                                )
                                isThinking = false
                            }
                        }
                    },
                    enabled = inputQuery.isNotBlank() && !isThinking,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (inputQuery.isNotBlank()) RoyalBlue600 else Slate200)
                        .testTag("ai_tutor_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputQuery.isNotBlank()) PureWhite else Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiMessageBubble(
    message: AiChatMessage,
    onUpgradeClick: () -> Unit
) {
    val isUser = message.senderRole == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(RoyalBlue600),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) RoyalBlue600 else MaterialTheme.colorScheme.surface,
            border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            tonalElevation = if (isUser) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.messageText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUser) PureWhite else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                )

                if (message.suggestedAction == "UPGRADE_PRO") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upgrade to JAMB Genius PRO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate950))
                    }
                }
            }
        }
    }
}

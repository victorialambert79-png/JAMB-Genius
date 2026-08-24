package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Question
import com.example.data.repository.JambRepository
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun UnderstandingCheckScreen(
    topicId: String,
    repository: JambRepository,
    onNavigateBackToCurriculum: () -> Unit,
    onNavigateToAiTutor: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val topic by repository.getTopicById(topicId).collectAsState(initial = null)
    val questions by repository.getQuestionsForTopic(topicId).collectAsState(initial = emptyList())

    val activeQuestions = remember(questions) {
        if (questions.isNotEmpty()) questions.take(3) else emptyList()
    }

    val userAnswers = remember { mutableStateMapOf<String, String>() }
    var isSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Amber50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "UNDERSTANDING CHECK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Amber700,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${activeQuestions.size} Questions",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate600)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Topic Mastery Quiz: ${topic?.title ?: "Syllabus Check"}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isSubmitted) {
                activeQuestions.forEachIndexed { index, question ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Question ${index + 1}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue600)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = question.questionText,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val options = listOf(
                                Pair("A", question.optionA),
                                Pair("B", question.optionB),
                                Pair("C", question.optionC),
                                Pair("D", question.optionD)
                            )

                            options.forEach { (key, optText) ->
                                val selected = userAnswers[question.id] == key
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) RoyalBlue50 else Slate50,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (selected) RoyalBlue600 else Slate200
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            userAnswers[question.id] = key
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$key)",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (selected) RoyalBlue600 else Slate700
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = optText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Results Summary
                val correctCount = activeQuestions.count { q ->
                    userAnswers[q.id].equals(q.correctOption, ignoreCase = true)
                }
                val percentage = if (activeQuestions.isNotEmpty()) (correctCount * 100) / activeQuestions.size else 0

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (percentage >= 70) Emerald50 else Crimson50
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (percentage >= 70) Emerald500.copy(alpha = 0.4f) else Crimson500.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (percentage >= 70) Icons.Default.EmojiEvents else Icons.Default.Replay,
                            contentDescription = null,
                            tint = if (percentage >= 70) Emerald600 else Crimson600,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (percentage >= 70) "Mastery Attained! 🎉" else "Keep Practicing!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (percentage >= 70) Emerald900 else Crimson900
                            )
                        )
                        Text(
                            text = "Score: $correctCount / ${activeQuestions.size} ($percentage%)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (percentage >= 70) Emerald800 else Crimson800
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = if (percentage >= 70) "You have mastered the core syllabus objectives for this topic. +50 XP awarded!"
                            else "Review the questions below or ask your AI Tutor to clarify tough concepts.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                                color = Slate700
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Review
                activeQuestions.forEachIndexed { index, question ->
                    val userAns = userAnswers[question.id]
                    val isCorrect = userAns.equals(question.correctOption, ignoreCase = true)

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Question ${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isCorrect) "✓ Correct" else "✗ Incorrect (Selected: ${userAns ?: "None"})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) Emerald600 else Crimson600
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = question.questionText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Correct Answer (${question.correctOption}): ${question.explanation}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Action Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isSubmitted) {
                    PrimaryButton(
                        text = "Submit Quiz & Grade",
                        onClick = {
                            isSubmitted = true
                            val correctCount = activeQuestions.count { q ->
                                userAnswers[q.id].equals(q.correctOption, ignoreCase = true)
                            }
                            coroutineScope.launch {
                                val passed = (correctCount * 100) / maxOf(1, activeQuestions.size) >= 70
                                if (passed) {
                                    repository.addXp(50)
                                }
                            }
                        },
                        enabled = userAnswers.size == activeQuestions.size,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedButton(
                        onClick = { topic?.let { onNavigateToAiTutor(it.subjectId, it.id) } },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ask AI Tutor")
                    }

                    Button(
                        onClick = onNavigateBackToCurriculum,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back to Syllabus")
                    }
                }
            }
        }
    }
}

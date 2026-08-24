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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Question
import com.example.data.repository.JambRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PracticeScreen(
    subjectId: String,
    topicId: String,
    repository: JambRepository,
    onNavigateToAiTutor: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allQuestions by repository.allQuestions.collectAsState(initial = emptyList())

    val filteredQuestions = remember(allQuestions, subjectId, topicId) {
        allQuestions.filter {
            (subjectId.isBlank() || it.subjectId == subjectId) &&
            (topicId.isBlank() || it.topicId == topicId)
        }.ifEmpty { allQuestions }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    val currentQuestion = filteredQuestions.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (currentQuestion == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No questions found for this topic.", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Bar with Question Number and Difficulty
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of ${filteredQuestions.size}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue600
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Amber50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = currentQuestion.difficulty,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Amber700,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 26.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Source: ${currentQuestion.yearMetadata}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Options A, B, C, D
                val options = listOf(
                    Pair("A", currentQuestion.optionA),
                    Pair("B", currentQuestion.optionB),
                    Pair("C", currentQuestion.optionC),
                    Pair("D", currentQuestion.optionD)
                )

                options.forEach { (optKey, optText) ->
                    val isSelected = selectedOption == optKey
                    val isCorrectOption = currentQuestion.correctOption.equals(optKey, ignoreCase = true)

                    val cardBackground = when {
                        !hasAnswered -> if (isSelected) RoyalBlue50 else MaterialTheme.colorScheme.surface
                        isCorrectOption -> Emerald50
                        isSelected && !isCorrectOption -> Crimson50
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        !hasAnswered -> if (isSelected) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                        isCorrectOption -> Emerald600
                        isSelected && !isCorrectOption -> Crimson600
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(enabled = !hasAnswered) {
                                selectedOption = optKey
                                hasAnswered = true
                                val isCorrect = isCorrectOption
                                coroutineScope.launch {
                                    repository.recordQuestionAttempt(
                                        questionId = currentQuestion.id,
                                        subjectId = currentQuestion.subjectId,
                                        topicId = currentQuestion.topicId,
                                        selectedOption = optKey,
                                        isCorrect = isCorrect,
                                        timeSpentSecs = 15
                                    )
                                }
                            }
                            .testTag("option_$optKey")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            !hasAnswered -> if (isSelected) RoyalBlue600 else Slate100
                                            isCorrectOption -> Emerald600
                                            isSelected && !isCorrectOption -> Crimson600
                                            else -> Slate100
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optKey,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected || (hasAnswered && isCorrectOption)) PureWhite else Slate700
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Explanation & AI Tutor Box
                AnimatedVisibility(visible = hasAnswered) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedOption == currentQuestion.correctOption) Emerald50.copy(alpha = 0.5f) else Amber50.copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedOption == currentQuestion.correctOption) Emerald500.copy(alpha = 0.4f) else Amber500.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (selectedOption == currentQuestion.correctOption) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (selectedOption == currentQuestion.correctOption) Emerald600 else Crimson600,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (selectedOption == currentQuestion.correctOption) "Correct! (+10 XP)" else "Incorrect. Review Solution",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (selectedOption == currentQuestion.correctOption) Emerald800 else Crimson800
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = currentQuestion.explanation,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Slate900,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { onNavigateToAiTutor(currentQuestion.subjectId, currentQuestion.topicId) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ask AI Tutor to Explain this Step-by-Step")
                        }
                    }
                }
            }

            // Bottom Navigation Controls
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                selectedOption = null
                                hasAnswered = false
                            }
                        },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            if (currentIndex < filteredQuestions.size - 1) {
                                currentIndex++
                                selectedOption = null
                                hasAnswered = false
                            }
                        },
                        enabled = currentIndex < filteredQuestions.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Next Question")
                    }
                }
            }
        }
    }
}

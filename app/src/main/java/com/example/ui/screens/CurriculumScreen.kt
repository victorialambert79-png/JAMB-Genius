package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Topic
import com.example.data.repository.JambRepository
import com.example.ui.theme.*

@Composable
fun CurriculumScreen(
    subjectId: String,
    repository: JambRepository,
    onTopicLessonClick: (String) -> Unit,
    onTopicPracticeClick: (String, String) -> Unit,
    onTopicAiTutorClick: (String, String) -> Unit,
    onTopicQuizClick: (String) -> Unit
) {
    val subject by repository.getSubjectById(subjectId).collectAsState(initial = null)
    val topics by repository.getTopicsForSubject(subjectId).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        // Subject Header Info
        subject?.let { subj ->
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subj.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Emerald50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Emerald600,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Official JAMB IBASS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Emerald700,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subj.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${topics.size} Official Syllabus Topics",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600
                            )
                        )
                        Text(
                            text = "Reference: ${subj.code} 2026/27",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Topic List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(topics) { topic ->
                TopicCard(
                    topic = topic,
                    onLessonClick = { onTopicLessonClick(topic.id) },
                    onPracticeClick = { onTopicPracticeClick(topic.subjectId, topic.id) },
                    onAiTutorClick = { onTopicAiTutorClick(topic.subjectId, topic.id) },
                    onQuizClick = { onTopicQuizClick(topic.id) }
                )
            }
        }
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    onLessonClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onAiTutorClick: () -> Unit,
    onQuizClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("topic_card_${topic.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (topic.isCompleted) Emerald50 else RoyalBlue50),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${topic.orderIndex}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (topic.isCompleted) Emerald700 else RoyalBlue700
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (topic.difficulty) {
                                "High-Yield" -> Amber50
                                "Advanced" -> Crimson50
                                else -> RoyalBlue50
                            }
                        ) {
                            Text(
                                text = topic.difficulty,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (topic.difficulty) {
                                        "High-Yield" -> Amber700
                                        "Advanced" -> Crimson700
                                        else -> RoyalBlue700
                                    }
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${topic.masteryLevel}% Mastery",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (topic.masteryLevel >= 70) Emerald600 else Slate600
                            )
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Slate500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { topic.masteryLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(2.5.dp)),
                color = if (topic.masteryLevel >= 70) Emerald600 else RoyalBlue600,
                trackColor = Slate100
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(
                        text = topic.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onLessonClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("Lesson", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        OutlinedButton(
                            onClick = onPracticeClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("Practice", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        FilledTonalButton(
                            onClick = onAiTutorClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Amber50, contentColor = Amber700),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("AI Tutor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

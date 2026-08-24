package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lesson
import com.example.data.model.Topic
import com.example.data.repository.JambRepository
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(
    topicId: String,
    repository: JambRepository,
    onNavigateToAiTutor: (String, String) -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    onFinishLesson: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val topic by repository.getTopicById(topicId).collectAsState(initial = null)
    val lesson by repository.getLessonForTopic(topicId).collectAsState(initial = null)
    val objectives by repository.getObjectivesForTopic(topicId).collectAsState(initial = emptyList())

    var isCompletedLocally by remember { mutableStateOf(false) }

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
            // Topic & Time Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalBlue50
                ) {
                    Text(
                        text = "TOPIC ${topic?.orderIndex ?: 1} • JAMB SYLLABUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue700,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate100
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${lesson?.readTimeMins ?: 7} mins read",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate700,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lesson?.title ?: topic?.title ?: "Syllabus Lesson",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Learning Objectives Card
            if (objectives.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "JAMB Learning Objectives",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        objectives.forEach { obj ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Emerald600, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = obj.objectiveText,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Main Lesson Body
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = lesson?.contentMarkdown ?: "Loading verified lesson notes...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Worked Nigerian JAMB Examples Card
            lesson?.examples?.let { ex ->
                if (ex.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Emerald50.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Emerald600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Worked JAMB Example",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ex,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Emerald900,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            // Key Points Summary Card
            lesson?.keyPoints?.let { kp ->
                if (kp.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = RoyalBlue50.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue600.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = RoyalBlue600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "High-Yield Summary",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalBlue800
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = kp,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = RoyalBlue900,
                                    lineHeight = 24.sp
                                )
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
                OutlinedButton(
                    onClick = {
                        topic?.let { t ->
                            onNavigateToAiTutor(t.subjectId, t.id)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask AI Tutor")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            repository.markLessonCompleted(topicId)
                            isCompletedLocally = true
                            onNavigateToQuiz(topicId)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Quiz (+25 XP)")
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyPlanTask
import com.example.data.model.UserProfile
import com.example.data.repository.JambRepository
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StudyPlannerScreen(
    repository: JambRepository,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToPractice: (String, String) -> Unit,
    onNavigateToQuiz: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val enrolledSubjects by repository.enrolledSubjects.collectAsState(initial = emptyList())
    val studyTasks by repository.studyPlanTasks.collectAsState(initial = emptyList())

    val profile = userProfile ?: UserProfile()
    val daysRemaining = maxOf(1, ((profile.examDateMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt())

    var filterStatus by remember { mutableStateOf("All") }

    val filteredTasks = studyTasks.filter {
        when (filterStatus) {
            "Pending" -> !it.isCompleted
            "Completed" -> it.isCompleted
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Planner Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalBlue600.copy(alpha = 0.08f), Emerald500.copy(alpha = 0.06f))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Personalized Adaptive Plan",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "Target: ${profile.targetScore} / 400 • ${profile.dailyStudyTimeMins} mins/day",
                                style = MaterialTheme.typography.bodySmall.copy(color = RoyalBlue700, fontWeight = FontWeight.Bold)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RoyalBlue50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = RoyalBlue600)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your daily schedule automatically adjusts based on your quiz accuracy, weak subject areas, and the $daysRemaining days left until your JAMB UTME exam.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate700, lineHeight = 20.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.generateAdaptiveStudyPlan(profile, enrolledSubjects)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate Adaptive Schedule", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Pending", "Completed").forEach { st ->
                val selected = filterStatus == st
                FilterChip(
                    selected = selected,
                    onClick = { filterStatus = st },
                    label = { Text(st, style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue600,
                        selectedLabelColor = PureWhite
                    )
                )
            }
        }

        // Tasks List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTasks) { task ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (task.isCompleted) Slate50 else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (task.isCompleted) Slate200 else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { completed ->
                                    coroutineScope.launch {
                                        repository.setTaskCompleted(task.id, completed)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Emerald600)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (task.isCompleted) Slate500 else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "${task.taskType} • ${task.durationMins} mins • Priority: ${task.priority}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        if (!task.isCompleted) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onNavigateToLesson(task.topicId) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Lesson", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                OutlinedButton(
                                    onClick = { onNavigateToPractice(task.subjectId, task.topicId) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Practice", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                FilledTonalButton(
                                    onClick = { onNavigateToQuiz(task.topicId) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Amber50, contentColor = Amber700),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Quiz", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

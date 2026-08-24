package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyPlanTask
import com.example.data.model.Subject
import com.example.data.model.UserProfile
import com.example.data.repository.JambRepository
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    repository: JambRepository,
    onNavigateToSubject: (String) -> Unit,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToAiTutor: (String, String) -> Unit,
    onNavigateToPractice: (String, String) -> Unit,
    onNavigateToUnderstandingCheck: (String) -> Unit,
    onNavigateToCbtMock: () -> Unit,
    onNavigateToMistakes: () -> Unit,
    onNavigateToStudyPlanner: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val enrolledSubjects by repository.enrolledSubjects.collectAsState(initial = emptyList())
    val studyTasks by repository.studyPlanTasks.collectAsState(initial = emptyList())
    val unresolvedMistakes by repository.unresolvedMistakes.collectAsState(initial = emptyList())

    val profile = userProfile ?: UserProfile()

    // Calculate days remaining to exam
    val daysRemaining = maxOf(1, ((profile.examDateMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Welcome Banner & Countdown
        item {
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
                                listOf(RoyalBlue800.copy(alpha = 0.04f), Emerald500.copy(alpha = 0.06f))
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
                                    text = "Good morning, ${profile.name.split(" ").firstOrNull() ?: "Scholar"} 👋",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Text(
                                    text = "Target: ${profile.targetScore} / 400 • Projected: 285+",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = RoyalBlue700,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Amber50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = Amber600,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${profile.streakDays} Days",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Amber700
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Countdown Progress Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RoyalBlue600,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = PureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Official JAMB UTME Countdown",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = PureWhite,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                Text(
                                    text = "$daysRemaining Days Left",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Mastery",
                    value = "74%",
                    subtitle = "+6% this week",
                    icon = Icons.Default.MilitaryTech,
                    accentColor = Emerald600,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Questions",
                    value = "${profile.completedQuestionsCount}",
                    subtitle = "18 today",
                    icon = Icons.Default.Quiz,
                    accentColor = RoyalBlue600,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Hub Grid
        item {
            Column {
                SectionHeader(title = "Smart Learning Tools")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "AI Personal Tutor",
                        subtitle = "Instant Socratic Help",
                        icon = Icons.Default.SmartToy,
                        gradient = listOf(RoyalBlue600, RoyalBlue800),
                        onClick = { onNavigateToAiTutor("", "") },
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "JAMB CBT Mock",
                        subtitle = "Full Timed Exam",
                        icon = Icons.Default.Speed,
                        gradient = listOf(Emerald600, Emerald900),
                        onClick = onNavigateToCbtMock,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "Weak Areas",
                        subtitle = "${unresolvedMistakes.size} Topics to Fix",
                        icon = Icons.Default.WarningAmber,
                        gradient = listOf(Crimson600, Crimson700),
                        onClick = onNavigateToMistakes,
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "Adaptive Plan",
                        subtitle = "Auto-Paced Revision",
                        icon = Icons.Default.Schedule,
                        gradient = listOf(Amber600, Amber700),
                        onClick = onNavigateToStudyPlanner,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Today's Study Plan Section
        item {
            SectionHeader(
                title = "Today's Study Plan",
                actionText = "Full Planner",
                onActionClick = onNavigateToStudyPlanner
            )
        }

        if (studyTasks.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No scheduled tasks yet",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.generateAdaptiveStudyPlan(profile, enrolledSubjects)
                                }
                            }
                        ) {
                            Text("Generate Today's Study Plan")
                        }
                    }
                }
            }
        } else {
            items(studyTasks) { task ->
                StudyTaskCard(
                    task = task,
                    onToggleComplete = { completed ->
                        coroutineScope.launch {
                            repository.setTaskCompleted(task.id, completed)
                        }
                    },
                    onLearnClick = { onNavigateToLesson(task.topicId) },
                    onPracticeClick = { onNavigateToPractice(task.subjectId, task.topicId) },
                    onQuizClick = { onNavigateToUnderstandingCheck(task.topicId) }
                )
            }
        }

        // Enrolled Subjects Overview
        item {
            SectionHeader(title = "My JAMB Subjects")
        }

        items(enrolledSubjects) { subject ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSubject(subject.id) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoyalBlue50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = subject.name,
                            tint = RoyalBlue600,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (subject.isCompulsory) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = RoyalBlue600
                                ) {
                                    Text(
                                        text = "CORE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Confidence: ${"★".repeat(subject.confidenceLevel)}${"☆".repeat(5 - subject.confidenceLevel)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Amber600)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Slate400
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PureWhite.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PureWhite.copy(alpha = 0.85f),
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun StudyTaskCard(
    task: StudyPlanTask,
    onToggleComplete: (Boolean) -> Unit,
    onLearnClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onQuizClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Slate50 else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isCompleted) Slate200 else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_${task.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggleComplete,
                    colors = CheckboxDefaults.colors(checkedColor = Emerald600)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (task.isCompleted) Slate500 else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (task.taskType) {
                                "LEARN" -> RoyalBlue50
                                "PRACTICE" -> Emerald50
                                "QUIZ" -> Amber50
                                else -> Crimson50
                            }
                        ) {
                            Text(
                                text = task.taskType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (task.taskType) {
                                        "LEARN" -> RoyalBlue700
                                        "PRACTICE" -> Emerald700
                                        "QUIZ" -> Amber700
                                        else -> Crimson700
                                    }
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "${task.durationMins} mins",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
                        )
                    }
                }
            }

            if (!task.isCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onLearnClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Learn", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = onPracticeClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Practice", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    FilledTonalButton(
                        onClick = onQuizClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Amber50, contentColor = Amber700),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quiz", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

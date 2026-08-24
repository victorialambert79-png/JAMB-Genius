package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.data.model.UserProfile
import com.example.data.repository.JambRepository
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    repository: JambRepository,
    onComplete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allSubjects by repository.allSubjects.collectAsState(initial = emptyList())

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 5

    // State
    var studentName by remember { mutableStateOf("Chinedu Okafor") }
    var targetScore by remember { mutableFloatStateOf(320f) }
    var daysUntilExam by remember { mutableIntStateOf(58) }
    val selectedSubjectIds = remember { mutableStateListOf("subj_eng", "subj_bio", "subj_chem", "subj_phy") }
    val subjectConfidences = remember { mutableStateMapOf("subj_eng" to 4, "subj_bio" to 3, "subj_chem" to 3, "subj_phy" to 2) }
    var dailyStudyTimeMins by remember { mutableIntStateOf(90) }
    var preferredLearningStyle by remember { mutableStateOf("Interactive AI Tutor & CBT Practice") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        IconButton(onClick = { currentStep-- }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Text(
                        text = "Step $currentStep of $totalSteps",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue600
                        )
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }

                LinearProgressIndicator(
                    progress = { currentStep / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = RoyalBlue600,
                    trackColor = Slate200
                )
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = if (currentStep == totalSteps) "Launch My Dashboard 🚀" else "Continue",
                        onClick = {
                            if (currentStep < totalSteps) {
                                currentStep++
                            } else {
                                // Save profile & selected subjects to Room DB
                                coroutineScope.launch {
                                    val profile = UserProfile(
                                        id = "primary_student",
                                        name = studentName.ifBlank { "Chinedu Okafor" },
                                        targetScore = targetScore.toInt(),
                                        examDateMillis = System.currentTimeMillis() + (daysUntilExam.toLong() * 24 * 60 * 60 * 1000),
                                        dailyStudyTimeMins = dailyStudyTimeMins,
                                        learningStyle = preferredLearningStyle,
                                        subscriptionTier = "free",
                                        xp = 500,
                                        streakDays = 1,
                                        completedQuestionsCount = 0,
                                        cbtMocksTaken = 0,
                                        isAdmin = false
                                    )
                                    repository.updateProfile(profile)

                                    // Update enrollment
                                    allSubjects.forEach { subject ->
                                        val isEnrolled = selectedSubjectIds.contains(subject.id)
                                        repository.toggleSubjectEnrollment(subject.id, isEnrolled)
                                        if (isEnrolled && subjectConfidences.containsKey(subject.id)) {
                                            repository.saveSubject(
                                                subject.copy(
                                                    isEnrolled = true,
                                                    confidenceLevel = subjectConfidences[subject.id] ?: 3
                                                )
                                            )
                                        }
                                    }

                                    // Generate adaptive study plan tasks
                                    val enrolled = allSubjects.filter { selectedSubjectIds.contains(it.id) }
                                    repository.generateAdaptiveStudyPlan(profile, enrolled)

                                    onComplete()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboarding_steps",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) { step ->
            when (step) {
                1 -> StepProfileAndTarget(
                    name = studentName,
                    onNameChange = { studentName = it },
                    target = targetScore,
                    onTargetChange = { targetScore = it }
                )
                2 -> StepExamDate(
                    days = daysUntilExam,
                    onDaysChange = { daysUntilExam = it }
                )
                3 -> StepSubjectsSelection(
                    allSubjects = allSubjects,
                    selectedIds = selectedSubjectIds,
                    onToggleSubject = { subjId ->
                        if (subjId != "subj_eng") {
                            if (selectedSubjectIds.contains(subjId)) {
                                selectedSubjectIds.remove(subjId)
                            } else {
                                if (selectedSubjectIds.size < 4) {
                                    selectedSubjectIds.add(subjId)
                                }
                            }
                        }
                    }
                )
                4 -> StepSubjectConfidence(
                    selectedSubjectIds = selectedSubjectIds,
                    allSubjects = allSubjects,
                    confidences = subjectConfidences,
                    onConfidenceChange = { subjId, rating ->
                        subjectConfidences[subjId] = rating
                    }
                )
                5 -> StepStudyHabits(
                    dailyMins = dailyStudyTimeMins,
                    onDailyMinsChange = { dailyStudyTimeMins = it },
                    style = preferredLearningStyle,
                    onStyleChange = { preferredLearningStyle = it }
                )
            }
        }
    }
}

@Composable
private fun StepProfileAndTarget(
    name: String,
    onNameChange: (String) -> Unit,
    target: Float,
    onTargetChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to JAMB Genius",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Let's personalize your AI study partner for top UTME performance.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "What is your name?",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = { Text("e.g. Chinedu Okafor") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target JAMB Score",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalBlue50
                    ) {
                        Text(
                            text = "${target.toInt()} / 400",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Slider(
                    value = target,
                    onValueChange = onTargetChange,
                    valueRange = 200f..400f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = RoyalBlue600,
                        activeTrackColor = RoyalBlue600
                    ),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = if (target >= 300) "🌟 Ambitious Top 1% Goal! Eligible for Medicine, Law, Engineering at premier universities."
                    else "🎯 Solid Target! Excellent for competitive degree programs.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (target >= 300) Emerald700 else Slate600,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun StepExamDate(
    days: Int,
    onDaysChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "When are you writing JAMB?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Text(
            text = "We use this to construct an adaptive countdown and daily topic revision pacing.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = RoyalBlue600,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$days Days Remaining",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue800
                    )
                )

                Text(
                    text = "Official JAMB UTME Examination Window",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                val options = listOf(30, 45, 58, 75, 90)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { d ->
                        val selected = days == d
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) RoyalBlue600 else Slate100,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDaysChange(d) }
                        ) {
                            Text(
                                text = "${d}d",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) PureWhite else Slate800
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepSubjectsSelection(
    allSubjects: List<Subject>,
    selectedIds: List<String>,
    onToggleSubject: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Select Your 4 JAMB Subjects",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Use of English is compulsory. Select 3 other subjects according to your course of study (${selectedIds.size}/4 chosen).",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(allSubjects) { subject ->
                val isSelected = selectedIds.contains(subject.id)
                val isEnglish = subject.isCompulsory

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) RoyalBlue50 else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isEnglish) {
                            onToggleSubject(subject.id)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSubject(subject.id) },
                            enabled = !isEnglish,
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBlue600)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                if (isEnglish) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = RoyalBlue600
                                    ) {
                                        Text(
                                            text = "MANDATORY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = PureWhite,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = subject.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepSubjectConfidence(
    selectedSubjectIds: List<String>,
    allSubjects: List<Subject>,
    confidences: Map<String, Int>,
    onConfidenceChange: (String, Int) -> Unit
) {
    val enrolled = allSubjects.filter { selectedSubjectIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Current Confidence Level",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Rate your comfort in each subject so the AI planner can allocate more time to challenging topics.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(enrolled) { subject ->
                val rating = confidences[subject.id] ?: 3
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                                text = subject.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = when (rating) {
                                    1 -> "Needs Heavy Revision"
                                    2 -> "Fair"
                                    3 -> "Moderate"
                                    4 -> "Confident"
                                    else -> "Very Strong"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (rating <= 2) Crimson600 else if (rating == 3) Amber600 else Emerald600
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = { onConfidenceChange(subject.id, star) },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = "Star $star",
                                        tint = if (star <= rating) Amber500 else Slate400,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepStudyHabits(
    dailyMins: Int,
    onDailyMinsChange: (Int) -> Unit,
    style: String,
    onStyleChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Study Habits & Learning Style",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Customize your daily study duration and preferred educational methodology.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        Text(
            text = "Daily Study Target: $dailyMins minutes",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val times = listOf(45, 60, 90, 120, 180)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            times.forEach { t ->
                val selected = dailyMins == t
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) RoyalBlue600 else Slate100,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDailyMinsChange(t) }
                ) {
                    Text(
                        text = if (t >= 60) "${t / 60}h" + if (t % 60 != 0) " ${t % 60}m" else "" else "${t}m",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selected) PureWhite else Slate800
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Preferred Learning Style",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        val styles = listOf(
            Pair("Interactive AI Tutor & CBT Practice", "Deep conversational tutor explanations followed by instant test drills."),
            Pair("Verified Syllabus Concept Breakdown", "Step-by-step textbook-style mastery of learning objectives first."),
            Pair("High-Speed Timed CBT Drills", "Test-driven learning with post-exam mistake analysis.")
        )

        styles.forEach { (st, desc) ->
            val selected = style == st
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) RoyalBlue50 else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (selected) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onStyleChange(st) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = st,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

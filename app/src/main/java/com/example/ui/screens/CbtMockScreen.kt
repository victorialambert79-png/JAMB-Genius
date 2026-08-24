package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Question
import com.example.data.model.Subject
import com.example.data.repository.JambRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CbtMockScreen(
    repository: JambRepository,
    onExamSubmitted: (Long) -> Unit,
    onExitExam: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val enrolledSubjects by repository.enrolledSubjects.collectAsState(initial = emptyList())
    val allQuestions by repository.allQuestions.collectAsState(initial = emptyList())

    val subjects = remember(enrolledSubjects) {
        if (enrolledSubjects.isNotEmpty()) enrolledSubjects else listOf(
            Subject("subj_eng", "Use of English", "ENG", "General", "Compulsory", "menu_book", "#2563EB", true, true, 4),
            Subject("subj_bio", "Biology", "BIO", "Science", "Cell, Genetics", "biotech", "#059669", false, true, 3),
            Subject("subj_chem", "Chemistry", "CHM", "Science", "Bonding, Acids", "science", "#D97706", false, true, 3),
            Subject("subj_phy", "Physics", "PHY", "Science", "Mechanics, Heat", "bolt", "#7C3AED", false, true, 2)
        )
    }

    var selectedSubjectIndex by remember { mutableIntStateOf(0) }
    val currentSubject = subjects.getOrNull(selectedSubjectIndex) ?: subjects.first()

    // Filter questions per subject
    val questionsForCurrentSubject = remember(allQuestions, currentSubject) {
        allQuestions.filter { it.subjectId == currentSubject.id }.ifEmpty {
            allQuestions.take(4)
        }
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questionsForCurrentSubject.getOrNull(currentQuestionIndex)

    // User Answers Map: QuestionId -> SelectedOption
    val userAnswers = remember { mutableStateMapOf<String, String>() }
    val markedForReview = remember { mutableStateListOf<String>() }

    // Timer (2 hours = 7200 seconds)
    var secondsRemaining by remember { mutableIntStateOf(7200) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showPaletteSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        // Auto-submit when time expires
        showSubmitDialog = true
    }

    val hours = secondsRemaining / 3600
    val minutes = (secondsRemaining % 3600) / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "JAMB CBT Official Simulation",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "4 Subjects • 180 Questions Total",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                            )
                        }

                        // Timer Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (secondsRemaining < 600) Crimson50 else RoyalBlue50,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (secondsRemaining < 600) Crimson500 else RoyalBlue500
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (secondsRemaining < 600) Crimson600 else RoyalBlue600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (secondsRemaining < 600) Crimson700 else RoyalBlue700
                                    )
                                )
                            }
                        }
                    }

                    // Subjects Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedSubjectIndex,
                        edgePadding = 16.dp,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        subjects.forEachIndexed { index, subj ->
                            Tab(
                                selected = selectedSubjectIndex == index,
                                onClick = {
                                    selectedSubjectIndex = index
                                    currentQuestionIndex = 0
                                },
                                text = {
                                    Text(
                                        text = subj.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selectedSubjectIndex == index) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
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
                            if (currentQuestionIndex > 0) {
                                currentQuestionIndex--
                            }
                        },
                        enabled = currentQuestionIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Prev")
                    }

                    OutlinedButton(
                        onClick = { showPaletteSheet = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Palette (${userAnswers.size})")
                    }

                    if (currentQuestionIndex < questionsForCurrentSubject.size - 1) {
                        Button(
                            onClick = { currentQuestionIndex++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = { showSubmitDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (currentQuestion != null) {
                // Question Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questionsForCurrentSubject.size}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue600)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = markedForReview.contains(currentQuestion.id),
                            onCheckedChange = {
                                if (it) markedForReview.add(currentQuestion.id) else markedForReview.remove(currentQuestion.id)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Amber500)
                        )
                        Text(
                            text = "Mark for Review",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = if (markedForReview.contains(currentQuestion.id)) Amber700 else Slate600
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Box
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options A, B, C, D
                val options = listOf(
                    Pair("A", currentQuestion.optionA),
                    Pair("B", currentQuestion.optionB),
                    Pair("C", currentQuestion.optionC),
                    Pair("D", currentQuestion.optionD)
                )

                val selected = userAnswers[currentQuestion.id]

                options.forEach { (optKey, optText) ->
                    val isChosen = selected == optKey
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) RoyalBlue50 else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isChosen) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                userAnswers[currentQuestion.id] = optKey
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isChosen) RoyalBlue600 else Slate100),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optKey,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChosen) PureWhite else Slate700
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isChosen) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit CBT Mock Examination?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column {
                    Text("You have answered ${userAnswers.size} of 180 questions across 4 subjects.")
                    if (markedForReview.isNotEmpty()) {
                        Text(
                            "You still have ${markedForReview.size} questions marked for review.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Amber700, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                    Text(
                        "Are you sure you want to end the examination and view your diagnostic score report?",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        coroutineScope.launch {
                            // Compute diagnostic results
                            val correctCount = allQuestions.count { q ->
                                userAnswers[q.id].equals(q.correctOption, ignoreCase = true)
                            }
                            val estimatedScore = if (allQuestions.isNotEmpty()) (correctCount * 400) / allQuestions.size else 280
                            val timeUsed = 7200 - secondsRemaining

                            val mockId = repository.submitCbtMock(
                                examName = "JAMB UTME Timed Mock Exam",
                                subjectIdsCsv = subjects.joinToString(",") { it.id },
                                totalScore = estimatedScore,
                                maxScore = 400,
                                timeUsedSecs = timeUsed,
                                totalQuestions = 180,
                                correctAnswersCount = correctCount,
                                subjectsBreakdownJson = """[{"name":"Use of English","score":72,"max":100},{"name":"Biology","score":78,"max":100},{"name":"Chemistry","score":64,"max":100},{"name":"Physics","score":66,"max":100}]""",
                                weakTopicsJson = """["Chemical Bonding & Acids","Electric DC Circuits","Lexis Concord"]""",
                                strongTopicsJson = """["Cell Structure & Organization","Separation of Mixtures","Motion Mechanics"]""",
                                recommendationsJson = """["Review Chemical Bonding lone pairs and dative bond concepts","Solve 20 DC current circuit Kirchhoff questions","Read the grammar concord parenthetical rules"]"""
                            )
                            onExamSubmitted(mockId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                ) {
                    Text("Yes, Submit Exam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Continue Exam")
                }
            }
        )
    }

    // Question Palette Bottom Sheet / Dialog
    if (showPaletteSheet) {
        AlertDialog(
            onDismissRequest = { showPaletteSheet = false },
            title = { Text("Question Palette", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PaletteLegendItem("Answered", Emerald600)
                        PaletteLegendItem("Unanswered", Slate300)
                        PaletteLegendItem("Review", Amber500)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(questionsForCurrentSubject) { index, q ->
                            val answered = userAnswers.containsKey(q.id)
                            val review = markedForReview.contains(q.id)

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            review -> Amber500
                                            answered -> Emerald600
                                            else -> Slate200
                                        }
                                    )
                                    .clickable {
                                        currentQuestionIndex = index
                                        showPaletteSheet = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (answered || review) PureWhite else Slate800
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPaletteSheet = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PaletteLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
    }
}

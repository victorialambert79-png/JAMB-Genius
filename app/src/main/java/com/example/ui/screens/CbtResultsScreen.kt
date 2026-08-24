package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CbtMockResult
import com.example.data.repository.JambRepository
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun CbtResultsScreen(
    resultId: Long,
    repository: JambRepository,
    onNavigateToMistakes: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToAiTutor: (String, String) -> Unit
) {
    val mockResult by repository.getMockResultById(resultId).collectAsState(initial = null)

    val res = mockResult ?: CbtMockResult(
        id = resultId,
        examName = "JAMB Diagnostic CBT Mock Exam",
        subjectIdsCsv = "subj_eng,subj_bio,subj_chem,subj_phy",
        totalScore = 280,
        maxScore = 400,
        timeUsedSecs = 6120,
        totalQuestions = 180,
        correctAnswersCount = 126,
        subjectsBreakdownJson = "",
        weakTopicsJson = "",
        strongTopicsJson = "",
        recommendationsJson = ""
    )

    val timeMins = res.timeUsedSecs / 60
    val scorePercentage = (res.totalScore * 100) / res.maxScore

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
            // Hero Score Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (res.totalScore >= 280) Emerald50 else Amber50,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (res.totalScore >= 280) Emerald500.copy(alpha = 0.4f) else Amber500.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(
                                text = if (res.totalScore >= 300) "🌟 ELITE 300+ SCORER" else if (res.totalScore >= 260) "🎯 COMPETITIVE MERIT BAND" else "📚 REVISION REQUIRED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.totalScore >= 280) Emerald700 else Amber700,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${res.totalScore}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalBlue800,
                                fontSize = 48.sp
                            )
                        )

                        Text(
                            text = "out of 400 Total JAMB Marks ($scorePercentage%)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ScoreMetricChip("Correct Answers", "${res.correctAnswersCount} / 180", Emerald600, Modifier.weight(1f))
                            ScoreMetricChip("Time Used", "${timeMins} mins", RoyalBlue600, Modifier.weight(1f))
                            ScoreMetricChip("XP Earned", "+100 XP", Amber600, Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subject Breakdown
            SectionHeader(title = "Subject Score Breakdown")

            val subjectsScores = listOf(
                Pair("Use of English", 72),
                Pair("Biology", 78),
                Pair("Chemistry", 64),
                Pair("Physics", 66)
            )

            subjectsScores.forEach { (name, score) ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "$score / 100",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (score >= 70) Emerald600 else if (score >= 60) RoyalBlue600 else Crimson600
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (score >= 70) Emerald600 else if (score >= 60) RoyalBlue600 else Crimson600,
                            trackColor = Slate100
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weak Topics Diagnostic Alert
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Crimson50.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Crimson500.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, tint = Crimson600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Weak Areas Requiring Immediate Revision",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Crimson900
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val weakTopics = listOf(
                        "Chemistry: Chemical Bonding & Redox Reactions",
                        "Physics: DC Current Electricity & Ohm's Law",
                        "English: Lexis Concord & Stress Patterns"
                    )

                    weakTopics.forEach { topic ->
                        Text(
                            text = "• $topic",
                            style = MaterialTheme.typography.bodySmall.copy(color = Crimson900, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Strong Areas
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald50.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Top Mastery Areas",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald900
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val strongTopics = listOf(
                        "Biology: Cell Structure & Organization (88%)",
                        "Chemistry: Separation of Mixtures & Purity (84%)",
                        "English: Reading Comprehension & Summary (76%)"
                    )

                    strongTopics.forEach { topic ->
                        Text(
                            text = "• $topic",
                            style = MaterialTheme.typography.bodySmall.copy(color = Emerald900, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
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
                    onClick = onNavigateToMistakes,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fix Weak Areas")
                }

                Button(
                    onClick = onNavigateToDashboard,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back to Dashboard")
                }
            }
        }
    }
}

@Composable
private fun ScoreMetricChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = color))
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate600))
        }
    }
}

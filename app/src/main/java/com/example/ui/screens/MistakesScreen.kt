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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MistakeRecord
import com.example.data.repository.JambRepository
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MistakesScreen(
    repository: JambRepository,
    onNavigateToAiTutor: (String, String) -> Unit,
    onNavigateToPractice: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val mistakes by repository.unresolvedMistakes.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Diagnostic Overview Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Smart Mistake Analyzer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "${mistakes.size} high-yield errors detected",
                            style = MaterialTheme.typography.bodySmall.copy(color = Crimson600, fontWeight = FontWeight.Bold)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Crimson50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = Crimson600)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "The AI continuously tracks question categories you missed during practice and CBT mocks so you can turn weaknesses into strengths before exam day.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600, lineHeight = 20.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        SectionHeader(title = "Recorded Mistake Patterns")

        if (mistakes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Emerald600, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Zero Unresolved Mistakes!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Great job! Keep practicing questions to maintain high accuracy.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mistakes) { mistake ->
                    MistakeCard(
                        mistake = mistake,
                        onAskTutor = { onNavigateToAiTutor(mistake.subjectId, mistake.topicId) },
                        onPractice = { onNavigateToPractice(mistake.subjectId, mistake.topicId) },
                        onResolve = {
                            coroutineScope.launch {
                                repository.resolveMistake(mistake.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MistakeCard(
    mistake: MistakeRecord,
    onAskTutor: () -> Unit,
    onPractice: () -> Unit,
    onResolve: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Crimson500.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mistake_card_${mistake.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Crimson50
                ) {
                    Text(
                        text = "MISSED ${mistake.mistakeCount}X",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Crimson700
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                TextButton(
                    onClick = onResolve,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Mark as Mastered ✓", style = MaterialTheme.typography.labelSmall.copy(color = Emerald700, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Topic: ${mistake.topicId.replace("_", " ").replace("topic", "Topic").replace("chem", "Chemistry:").replace("phy", "Physics:").replace("bio", "Biology:").replace("eng", "English:")}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "Subject Category: ${mistake.subjectId.replace("subj_", "").uppercase()} • Last error recorded recently",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPractice,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Practice Similar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                Button(
                    onClick = onAskTutor,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Diagnose with AI", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

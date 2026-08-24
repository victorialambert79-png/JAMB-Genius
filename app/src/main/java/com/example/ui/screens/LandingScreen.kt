package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onStartFree: () -> Unit,
    onLoginClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(listOf(RoyalBlue600, RoyalBlue800))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "JAMB Genius",
                            tint = PureWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "JAMB Genius",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalBlue800
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onLoginClick,
                        modifier = Modifier.testTag("landing_login_button")
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600
                            )
                        )
                    }
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("landing_admin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin Mode",
                            tint = Slate600
                        )
                    }
                }
            }
        }

        // Hero Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Emerald50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VERIFIED JAMB IBASS SYLLABUS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald700,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                Text(
                    text = "Your AI-Powered JAMB Study Partner",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Learn every topic, practice JAMB-style questions, discover your weak areas, and build a personalized study plan.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "“Learn smarter. Practice better. Score higher.”",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = RoyalBlue600,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onStartFree,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalBlue600,
                            contentColor = PureWhite
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("landing_start_free_button")
                    ) {
                        Text(
                            text = "Start Studying Free",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Live Dashboard Preview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STUDENT DASHBOARD PREVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue600,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "Target: 320 / 400 | Exam in 58 Days",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Amber50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "🔥 4-Day Streak",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Amber700
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardMetricChip("Overall Mastery", "74%", Emerald600, Modifier.weight(1f))
                        DashboardMetricChip("Questions Solved", "92", RoyalBlue600, Modifier.weight(1f))
                        DashboardMetricChip("CBT Mocks", "2 Taken", Amber600, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Today's Study Plan (Adaptive)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PreviewTaskRow("Biology: Cell Structure", "Completed", Emerald600)
                    PreviewTaskRow("Chemistry: Chemical Bonding", "In Progress", RoyalBlue600)
                    PreviewTaskRow("English: Concord & Grammatical Rules", "Scheduled", Slate500)
                }
            }
        }

        // Complete Study System Cycle
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Complete Study System",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "A proven scientific methodology for scoring 300+ in JAMB UTME.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val steps = listOf(
                    Triple("1. Learn", "Follow verified JAMB IBASS syllabus lessons with interactive concepts.", Icons.Default.MenuBook),
                    Triple("2. Practice", "Solve syllabus-tagged questions with comprehensive explanations.", Icons.Default.Quiz),
                    Triple("3. Test", "Timed CBT simulations matching exact JAMB software layout.", Icons.Default.Timer),
                    Triple("4. Analyze", "Smart mistake detection spots weak topics automatically.", Icons.Default.Analytics),
                    Triple("5. Revise", "Adaptive AI daily planner continuously drills low-mastery topics.", Icons.Default.Loop)
                )

                steps.forEach { (step, desc, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RoyalBlue50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = RoyalBlue600, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = step, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }
                }
            }
        }

        // Free vs Pro Comparison
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Free vs. Pro Plans",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Choose the plan that matches your UTME score ambition.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ComparisonRow("Verified Syllabus & Lessons", true, true)
                    ComparisonRow("Daily AI Tutor Queries", "5 / day", "Unlimited ⚡")
                    ComparisonRow("Question Practice Bank", "20 / day", "Unlimited ⚡")
                    ComparisonRow("CBT Timed Mocks", "1 / week", "Unlimited Full Mocks")
                    ComparisonRow("Smart Mistake Analyzer", "Basic", "Deep Diagnostics")
                    ComparisonRow("Adaptive AI Study Planner", "Standard", "Personalized Real-time")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStartFree,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get Started Free Today", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // FAQs
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                FaqItem(
                    "Is the syllabus updated for the 2026/2027 JAMB UTME?",
                    "Yes. JAMB Genius strictly models the official JAMB IBASS syllabus documents for all subjects, ensuring you only study what will actually appear in your examination."
                )
                FaqItem(
                    "How does the AI Personal Tutor work?",
                    "The AI Tutor acts as a dedicated teacher. It can simplify complex topics with relatable Nigerian analogies, provide step-by-step worked solutions, give hints, and test your understanding."
                )
                FaqItem(
                    "Can I practice with CBT timing?",
                    "Yes. Our CBT Mock Exam simulator exactly reproduces the multi-subject layout, countdown timer, question palette, and mark-for-review features of real JAMB software."
                )
            }
        }

        // Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "JAMB Genius",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalBlue800)
                )
                Text(
                    text = "Built for Nigerian secondary school students and UTME candidates.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Terms of Service • Privacy Policy • Support • Verified IBASS Sources",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DashboardMetricChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = color))
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate600))
        }
    }
}

@Composable
private fun PreviewTaskRow(title: String, status: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1
            )
        }
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color)
        )
    }
}

@Composable
private fun ComparisonRow(feature: String, free: Any, pro: Any) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier.width(160.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (free is Boolean) if (free) "✓" else "—" else free.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = if (pro is Boolean) if (pro) "✓" else "—" else pro.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue600
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Slate500
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Achievement
import com.example.data.model.UserProfile
import com.example.data.repository.JambRepository
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: JambRepository,
    onUpgradeClick: () -> Unit,
    onAdminClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val achievements by repository.achievements.collectAsState(initial = emptyList())
    val transactions by repository.transactions.collectAsState(initial = emptyList())

    val profile = userProfile ?: UserProfile()
    val isPro = profile.subscriptionTier == "pro"
    val daysRemaining = maxOf(1, ((profile.examDateMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue600),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Target: ${profile.targetScore} • $daysRemaining Days to UTME",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPro) RoyalBlue600 else Slate200,
                            modifier = Modifier.clickable { onUpgradeClick() }
                        ) {
                            Text(
                                text = if (isPro) "PRO ⚡" else "FREE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPro) PureWhite else Slate700
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileStatChip("Total XP", "${profile.xp} XP", Amber600, Modifier.weight(1f))
                        ProfileStatChip("Study Streak", "${profile.streakDays} Days 🔥", Emerald600, Modifier.weight(1f))
                        ProfileStatChip("CBT Mocks", "${profile.cbtMocksTaken} Taken", RoyalBlue600, Modifier.weight(1f))
                    }
                }
            }
        }

        // Pro Upgrade Prompt (if Free)
        if (!isPro) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Amber50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUpgradeClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = Amber600, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Upgrade to JAMB Genius PRO", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Amber900))
                            Text("Unlimited AI Tutor, full CBT mocks & score prediction.", style = MaterialTheme.typography.bodySmall.copy(color = Amber800))
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Amber700)
                    }
                }
            }
        }

        // Achievements & Gamification
        item {
            SectionHeader(title = "Badges & Achievements (${achievements.count { it.isUnlocked }}/${achievements.size})")
        }

        items(achievements) { ach ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (ach.isUnlocked) Emerald50.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (ach.isUnlocked) Emerald500.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (ach.isUnlocked) Emerald100 else Slate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = if (ach.isUnlocked) Emerald700 else Slate400,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ach.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (ach.isUnlocked) MaterialTheme.colorScheme.onSurface else Slate500
                            )
                        )
                        Text(
                            text = ach.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                            maxLines = 2
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (ach.isUnlocked) Emerald600 else Slate200
                    ) {
                        Text(
                            text = "+${ach.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (ach.isUnlocked) PureWhite else Slate600
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Transactions History
        if (transactions.isNotEmpty()) {
            item {
                SectionHeader(title = "Payment Receipts")
            }
            items(transactions) { tx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = tx.planName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Ref: ${tx.reference} • ${tx.gateway}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                        }
                        Text(
                            text = "₦${tx.amountNgn}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Emerald600)
                        )
                    }
                }
            }
        }

        // Settings & Actions
        item {
            SectionHeader(title = "Account Settings")
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuRow("Admin Control Panel", Icons.Outlined.AdminPanelSettings, onAdminClick)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileMenuRow("Log Out & Reset Session", Icons.Outlined.Logout, onLogoutClick)
                }
            }
        }
    }
}

@Composable
private fun ProfileStatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
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

@Composable
private fun ProfileMenuRow(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Slate600, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
    }
}

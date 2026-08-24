package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JambTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    userProfile: UserProfile? = null,
    onUpgradeClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    userProfile?.let { profile ->
                        // XP Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Amber50,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "XP",
                                    tint = Amber600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${profile.xp} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Amber700
                                    )
                                )
                            }
                        }

                        // Plan Pill
                        val isPro = profile.subscriptionTier == "pro"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isPro) RoyalBlue600 else Slate200,
                            modifier = Modifier
                                .clickable { onUpgradeClick() }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isPro) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Pro",
                                        tint = PureWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "PRO",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PureWhite
                                        )
                                    )
                                } else {
                                    Text(
                                        text = "FREE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Slate700
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Admin icon button
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("top_bar_admin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin Mode",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun JambBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val items = listOf(
            NavigationItemData("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home),
            NavigationItemData("subjects", "Syllabus", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
            NavigationItemData("ai_tutor", "AI Tutor", Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
            NavigationItemData("cbt_mock", "CBT Mock", Icons.Filled.Timer, Icons.Outlined.Timer),
            NavigationItemData("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
        )

        items.forEach { item ->
            val selected = currentRoute.startsWith(item.route)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RoyalBlue600,
                    selectedTextColor = RoyalBlue600,
                    indicatorColor = RoyalBlue100
                ),
                modifier = Modifier.testTag("nav_${item.route}")
            )
        }
    }
}

data class NavigationItemData(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        if (actionText != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = RoyalBlue600
                    )
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RoyalBlue600,
            contentColor = PureWhite
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("btn_${text.lowercase().replace(" ", "_")}")
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

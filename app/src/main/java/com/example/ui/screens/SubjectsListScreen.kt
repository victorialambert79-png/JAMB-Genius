package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.data.repository.JambRepository
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SubjectsListScreen(
    repository: JambRepository,
    onSubjectClick: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allSubjects by repository.allSubjects.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Science", "General", "Commercial", "Arts")

    val filteredSubjects = allSubjects.filter { subject ->
        val matchesCategory = selectedCategory == "All" || subject.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || subject.name.contains(searchQuery, ignoreCase = true) || subject.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        // Search Bar
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search subjects, e.g. Biology, Physics...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val selected = selectedCategory == cat
                FilterChip(
                    selected = selected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue600,
                        selectedLabelColor = PureWhite
                    )
                )
            }
        }

        // Subject List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredSubjects) { subject ->
                SubjectCard(
                    subject = subject,
                    onClick = { onSubjectClick(subject.id) },
                    onToggleEnrollment = {
                        coroutineScope.launch {
                            repository.toggleSubjectEnrollment(subject.id, !subject.isEnrolled)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
    onToggleEnrollment: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (subject.isEnrolled) RoyalBlue500.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("subject_card_${subject.code}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoyalBlue50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (subject.code) {
                            "ENG" -> Icons.Default.MenuBook
                            "BIO" -> Icons.Default.Biotech
                            "CHM" -> Icons.Default.Science
                            "PHY" -> Icons.Default.Bolt
                            "MTH" -> Icons.Default.Calculate
                            "ECN" -> Icons.Default.TrendingUp
                            "GOV" -> Icons.Default.AccountBalance
                            else -> Icons.Default.School
                        },
                        contentDescription = subject.name,
                        tint = RoyalBlue600,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Slate100
                        ) {
                            Text(
                                text = subject.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = Slate700,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = subject.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 2
                    )
                }

                IconButton(onClick = onToggleEnrollment) {
                    Icon(
                        imageVector = if (subject.isEnrolled) Icons.Filled.BookmarkAdded else Icons.Outlined.BookmarkAdd,
                        contentDescription = "Enroll",
                        tint = if (subject.isEnrolled) RoyalBlue600 else Slate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Official IBASS",
                        tint = Emerald600,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Official IBASS Syllabus",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald700
                        )
                    )
                }

                Text(
                    text = "Explore Topics →",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue600
                    )
                )
            }
        }
    }
}

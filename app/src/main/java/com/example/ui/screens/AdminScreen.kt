package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Question
import com.example.data.model.Subject
import com.example.data.model.Topic
import com.example.data.model.UserProfile
import com.example.data.repository.JambRepository
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    repository: JambRepository,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val allSubjects by repository.allSubjects.collectAsState(initial = emptyList())
    val allQuestions by repository.allQuestions.collectAsState(initial = emptyList())
    val allTransactions by repository.transactions.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var apiTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingApi by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Admin Control Panel",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Crimson50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Crimson500.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "ADMINISTRATOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Crimson700,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Questions (${allQuestions.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Curriculum (${allSubjects.size})") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("User & System") }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> AdminQuestionsTab(
                questions = allQuestions,
                onAddQuestionClick = { showAddQuestionDialog = true },
                onDeleteQuestion = { qId ->
                    coroutineScope.launch { repository.deleteQuestion(qId) }
                }
            )
            1 -> AdminCurriculumTab(
                subjects = allSubjects,
                onAddTopicClick = { showAddTopicDialog = true }
            )
            2 -> AdminSystemTab(
                userProfile = userProfile,
                transactions = allTransactions,
                onTogglePro = {
                    coroutineScope.launch {
                        val current = userProfile ?: UserProfile()
                        val newTier = if (current.subscriptionTier == "pro") "free" else "pro"
                        repository.updateProfile(current.copy(subscriptionTier = newTier))
                    }
                },
                onAddXp = {
                    coroutineScope.launch { repository.addXp(250) }
                },
                onTestGemini = {
                    isTestingApi = true
                    apiTestResult = null
                    coroutineScope.launch {
                        val res = repository.askAiTutor(
                            userQuery = "Confirm system status and summarize the primary rule of subject-verb concord in JAMB Use of English.",
                            subjectName = "Use of English",
                            topicName = "Grammatical Concord",
                            lessonContext = "Official JAMB syllabus concord rules",
                            actionType = "TEST_ME"
                        )
                        apiTestResult = res
                        isTestingApi = false
                    }
                },
                isTestingApi = isTestingApi,
                apiTestResult = apiTestResult
            )
        }
    }

    // Add Question Dialog
    if (showAddQuestionDialog) {
        var subjectId by remember { mutableStateOf("subj_bio") }
        var topicId by remember { mutableStateOf("bio_topic_1") }
        var questionText by remember { mutableStateOf("") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var correctOption by remember { mutableStateOf("A") }
        var explanation by remember { mutableStateOf("") }
        var difficulty by remember { mutableStateOf("JAMB-Standard") }

        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = false },
            title = { Text("Add JAMB Question", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Question Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = correctOption, onValueChange = { correctOption = it }, label = { Text("Correct Option (A, B, C, D)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Detailed Explanation") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            val newQ = Question(
                                id = "q_admin_${System.currentTimeMillis()}",
                                subjectId = subjectId,
                                topicId = topicId,
                                questionText = questionText,
                                optionA = optA.ifBlank { "Option A text" },
                                optionB = optB.ifBlank { "Option B text" },
                                optionC = optC.ifBlank { "Option C text" },
                                optionD = optD.ifBlank { "Option D text" },
                                correctOption = correctOption.uppercase().take(1).ifBlank { "A" },
                                explanation = explanation.ifBlank { "Verified syllabus explanation." },
                                difficulty = difficulty
                            )
                            coroutineScope.launch {
                                repository.saveQuestion(newQ)
                                showAddQuestionDialog = false
                            }
                        }
                    }
                ) {
                    Text("Save Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQuestionDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminQuestionsTab(
    questions: List<Question>,
    onAddQuestionClick: () -> Unit,
    onDeleteQuestion: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Question Repository", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Button(onClick = onAddQuestionClick, shape = RoundedCornerShape(8.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Question")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(questions) { q ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${q.subjectId.replace("subj_", "").uppercase()} • ${q.difficulty}", style = MaterialTheme.typography.labelSmall.copy(color = RoyalBlue600, fontWeight = FontWeight.Bold))
                            IconButton(onClick = { onDeleteQuestion(q.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Crimson600, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(text = q.questionText, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), maxLines = 2)
                        Text(text = "Correct: Option ${q.correctOption}", style = MaterialTheme.typography.labelSmall.copy(color = Emerald700, fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCurriculumTab(
    subjects: List<Subject>,
    onAddTopicClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "Syllabus Subjects & Topics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        items(subjects) { subj ->
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
                        Text(text = "${subj.name} (${subj.code})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Category: ${subj.category} • Official IBASS Verified", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                    }
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600)
                }
            }
        }
    }
}

@Composable
private fun AdminSystemTab(
    userProfile: UserProfile?,
    transactions: List<com.example.data.model.PaymentTransaction>,
    onTogglePro: () -> Unit,
    onAddXp: () -> Unit,
    onTestGemini: () -> Unit,
    isTestingApi: Boolean,
    apiTestResult: String?
) {
    val profile = userProfile ?: UserProfile()
    val isPro = profile.subscriptionTier == "pro"

    // Settlement state
    var bankName by remember { mutableStateOf("OPay") }
    var accountNumber by remember { mutableStateOf("9067344958") }
    var accountName by remember { mutableStateOf("FLORENCE IFEOMA LAMBERT") }
    var isEditingBank by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Settlement Account Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Emerald500.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = Emerald600, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Settlement Bank Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    IconButton(onClick = { isEditingBank = !isEditingBank }) {
                        Icon(
                            imageVector = if (isEditingBank) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = "Edit Bank Details",
                            tint = Emerald700
                        )
                    }
                }

                Text(
                    text = "Subscription payments from Nigerian students settle directly to this account.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isEditingBank) {
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = { Text("NUBAN Account Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Account Holder Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { isEditingBank = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Settlement Account Details")
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Emerald50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bank:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                Text(bankName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Emerald900))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Account Number:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                Text(accountNumber, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = RoyalBlue800))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Beneficiary Name:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                Text(accountName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Emerald900))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Settlement Gateway:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                Text("Direct Transfer / Paystack / Flutterwave", style = MaterialTheme.typography.bodySmall.copy(color = Slate700, fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            }
        }

        // Transactions History Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Payment Transactions (${transactions.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))

                if (transactions.isEmpty()) {
                    Text(
                        text = "No subscription payments processed yet.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        transactions.take(5).forEach { tx ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(tx.planName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(
                                            "Ref: ${tx.reference} • ${tx.gateway}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Slate600)
                                        )
                                    }
                                    Text(
                                        "₦${tx.amountNgn}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Emerald700)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Subscription Simulation Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Student Subscription Simulation", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Current Tier: ${if (isPro) "PRO ⚡" else "FREE"} (${profile.xp} XP)", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onTogglePro, modifier = Modifier.weight(1f)) {
                        Text(if (isPro) "Switch to Free" else "Grant Pro Tier ⚡")
                    }
                    OutlinedButton(onClick = onAddXp, modifier = Modifier.weight(1f)) {
                        Text("+250 XP")
                    }
                }
            }
        }

        // Gemini AI Tutor Connectivity Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Gemini AI Tutor Connectivity", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Runs a live diagnostic test against Gemini 3.5 Flash with Nigerian Socratic tutor prompt rules.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onTestGemini,
                    enabled = !isTestingApi,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isTestingApi) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PureWhite, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Querying Tutor...")
                    } else {
                        Text("Run AI Tutor Diagnostic Test")
                    }
                }

                if (apiTestResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = apiTestResult ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate900),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

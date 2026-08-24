package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.repository.JambRepository
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProUpgradeScreen(
    repository: JambRepository,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPlan by remember { mutableStateOf("season") } // "monthly" or "season"
    var selectedMethod by remember { mutableStateOf("DirectTransfer") } // "DirectTransfer", "Paystack", "Flutterwave"
    var senderNameInput by remember { mutableStateOf("") }
    var copiedAccount by remember { mutableStateOf(false) }

    var isProcessing by remember { mutableStateOf(false) }
    var successReference by remember { mutableStateOf<String?>(null) }

    // Account details
    val bankName = "OPay"
    val accountNumber = "9067344958"
    val accountName = "FLORENCE IFEOMA LAMBERT"

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalBlue600
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("JAMB GENIUS PRO", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                    }
                }
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Unlock Your 300+ JAMB Score Guarantee",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Get unlimited AI Tutor sessions, full-length CBT mock exams, advanced mistake diagnostics, and continuous score prediction.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            // Plan 1: Full JAMB Season Pass (Most Popular)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPlan == "season") RoyalBlue50 else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (selectedPlan == "season") RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "season" }
                    .testTag("plan_season")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Amber500
                        ) {
                            Text(
                                text = "🔥 BEST VALUE — UNTIL EXAM DAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = Slate950,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        RadioButton(
                            selected = selectedPlan == "season",
                            onClick = { selectedPlan = "season" },
                            colors = RadioButtonDefaults.colors(selectedColor = RoyalBlue600)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "JAMB Season Pass",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₦12,000",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalBlue800
                            )
                        )
                        Text(
                            text = " / one-time payment",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }

                    Text(
                        text = "Access all features until the official conclusion of this year's JAMB UTME examination window.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate700),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Plan 2: 1-Month Pro Sprint
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPlan == "monthly") RoyalBlue50 else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (selectedPlan == "monthly") RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "monthly" }
                    .testTag("plan_monthly")
            ) {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1-Month Pro Sprint",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₦3,500",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalBlue800
                                )
                            )
                            Text(
                                text = " / 30 days",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                                modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                            )
                        }
                    }

                    RadioButton(
                        selected = selectedPlan == "monthly",
                        onClick = { selectedPlan = "monthly" },
                        colors = RadioButtonDefaults.colors(selectedColor = RoyalBlue600)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Gateway & Method Selection
            Text(
                text = "Select Payment Method",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option 1: Direct OPay Transfer
                val isDirect = selectedMethod == "DirectTransfer"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDirect) RoyalBlue50 else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isDirect) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1.1f)
                        .clickable { selectedMethod = "DirectTransfer" }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = if (isDirect) RoyalBlue600 else Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "OPay Transfer",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDirect) RoyalBlue700 else Slate800
                                )
                            )
                        }
                        Text(
                            text = "Direct Bank",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDirect) Emerald700 else Slate500,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Option 2: Paystack
                val isPaystack = selectedMethod == "Paystack"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPaystack) RoyalBlue50 else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isPaystack) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMethod = "Paystack" }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = if (isPaystack) RoyalBlue600 else Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Paystack",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPaystack) RoyalBlue700 else Slate800
                                )
                            )
                        }
                        Text(
                            text = "Card / USSD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate500,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Option 3: Flutterwave
                val isFlutterwave = selectedMethod == "Flutterwave"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isFlutterwave) RoyalBlue50 else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isFlutterwave) RoyalBlue600 else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMethod = "Flutterwave" }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isFlutterwave) RoyalBlue600 else Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Flutterwave",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFlutterwave) RoyalBlue700 else Slate800
                                )
                            )
                        }
                        Text(
                            text = "Online",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate500,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Transfer Details Box
            if (selectedMethod == "DirectTransfer") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue600.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = RoyalBlue600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Official Payout Account",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Emerald50,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "VERIFIED BENEFICIARY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Emerald800,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bank Name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bank Name:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                            Text(bankName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Account Number with Copy Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite, RoundedCornerShape(10.dp))
                                .border(1.dp, RoyalBlue600.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Account Number", style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp))
                                Text(
                                    text = accountNumber,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = RoyalBlue800,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(accountNumber))
                                    copiedAccount = true
                                    Toast.makeText(context, "OPay Account Number 9067344958 copied!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (copiedAccount) Emerald600 else RoyalBlue600),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (copiedAccount) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = PureWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (copiedAccount) "Copied!" else "Copy",
                                        style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Account Name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Account Name:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                            Text(
                                text = accountName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Amount to transfer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Exact Amount:", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                            Text(
                                text = if (selectedPlan == "season") "₦12,000" else "₦3,500",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Emerald700)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = senderNameInput,
                            onValueChange = { senderNameInput = it },
                            label = { Text("Sender's Name / Transfer Reference (Optional)") },
                            placeholder = { Text("e.g., Chinedu Okafor") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Settlement Bank: OPay (FLORENCE IFEOMA LAMBERT)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate800)
                            )
                        }
                        Text(
                            text = "Card, Bank App, and USSD payments via $selectedMethod are securely processed and deposited directly into the designated OPay account.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success Card if upgraded
            if (successReference != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Payment Verified! PRO Activated ⚡",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Emerald900)
                        )
                        Text(
                            text = "Reference: $successReference\nRecipient: $accountName (OPay: $accountNumber)\nBonus +500 XP added to your account.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Emerald800, textAlign = TextAlign.Center),
                            modifier = Modifier.padding(top = 4.dp)
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
            Column(modifier = Modifier.padding(16.dp)) {
                if (isProcessing) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = RoyalBlue600)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (selectedMethod == "DirectTransfer") "Verifying OPay transfer..." else "Connecting to $selectedMethod...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate700)
                        )
                    }
                } else if (successReference != null) {
                    Button(
                        onClick = onSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Studying with PRO Now 🚀", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                } else {
                    val amountText = if (selectedPlan == "season") "₦12,000" else "₦3,500"
                    val buttonText = when (selectedMethod) {
                        "DirectTransfer" -> "I Have Sent $amountText via OPay ⚡"
                        "Paystack" -> "Pay $amountText with Paystack"
                        else -> "Pay $amountText with Flutterwave"
                    }

                    PrimaryButton(
                        text = buttonText,
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                delay(1200) // Realistic processing simulation
                                val amount = if (selectedPlan == "season") 12000 else 3500
                                val planTitle = if (selectedPlan == "season") "JAMB Season Pass" else "1-Month Pro Sprint"
                                val gatewayTag = if (selectedMethod == "DirectTransfer") "OPay Direct Transfer ($accountNumber)" else selectedMethod
                                val ref = repository.upgradeToPro(planTitle, amount, gatewayTag)
                                successReference = ref
                                isProcessing = false
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Slate500, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "256-bit encrypted • Direct Nigerian bank settlement",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                    )
                }
            }
        }
    }
}

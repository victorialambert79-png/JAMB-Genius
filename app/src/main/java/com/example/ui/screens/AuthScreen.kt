package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onSkipToOnboarding: () -> Unit,
    onBackToLanding: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(true) }
    var isForgotPassword by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("Chinedu Okafor") }
    var email by remember { mutableStateOf("chinedu@jambgenius.ng") }
    var password by remember { mutableStateOf("StudentPass2026!") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resetSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackToLanding,
                modifier = Modifier.testTag("auth_back_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Brand Logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(listOf(RoyalBlue600, RoyalBlue800))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isForgotPassword) "Reset Your Password" else if (isSignUp) "Create Your Account" else "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Text(
            text = if (isForgotPassword) "Enter your registered email address to receive recovery instructions."
            else if (isSignUp) "Join thousands of Nigerian candidates scoring 300+ in JAMB UTME."
            else "Sign in to continue your personalized daily study schedule.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        // Auth Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isSignUp && !isForgotPassword) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Chinedu Okafor") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_fullname")
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("e.g. student@gmail.com") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Email, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email")
                )

                if (!isForgotPassword) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_password")
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = Crimson600),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (resetSent) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Emerald50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "A password reset link has been dispatched to your email address.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Emerald700, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (!isSignUp && !isForgotPassword) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { isForgotPassword = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RoyalBlue600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                PrimaryButton(
                    text = if (isForgotPassword) "Send Reset Link" else if (isSignUp) "Create Account & Continue" else "Sign In",
                    onClick = {
                        if (isForgotPassword) {
                            resetSent = true
                        } else {
                            if (isSignUp) {
                                onSkipToOnboarding()
                            } else {
                                onAuthSuccess()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Switch between Login and Signup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isForgotPassword) "Remember your password?" else if (isSignUp) "Already have an account?" else "Don't have an account?",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    TextButton(
                        onClick = {
                            if (isForgotPassword) {
                                isForgotPassword = false
                            } else {
                                isSignUp = !isSignUp
                            }
                        }
                    ) {
                        Text(
                            text = if (isForgotPassword) "Back to Login" else if (isSignUp) "Sign In" else "Sign Up",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo Quick Access
        OutlinedButton(
            onClick = onSkipToOnboarding,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quick Guest Access / Onboarding Demo")
        }
    }
}

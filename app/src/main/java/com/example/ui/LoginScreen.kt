package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.data.AuthManager
import com.example.data.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit,
    onLoginSuccess: (intendedEmail: String, role: UserRole) -> Unit
) {
    val context = LocalContext.current
    var showGoogleDialog by remember { mutableStateOf(false) }
    var selectedRoleForAuth by remember { mutableStateOf<UserRole?>(null) }
    var customEmailInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LuxeBurgundy,
                        Color(0xFF3B0014),
                        Color(0xFF1E000A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative ambient sparkles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingForSparkles())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Application Branding
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(LuxeGold.copy(alpha = 0.15f))
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TS",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxeGold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TS LuxeWear",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Exclusive Multitenant Boutique Collection",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = LuxeGold.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Public Login Options Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("login_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, LuxeGold.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "SECURE PLATFORM GATEWAY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxeGold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Customer Login (Google)
                        Button(
                            onClick = {
                                selectedRoleForAuth = UserRole.CUSTOMER
                                customEmailInput = ""
                                errorMessage = null
                                showGoogleDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_customer_login"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Google Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Customer Login (Google)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Store Owner Login (Google)
                        Button(
                            onClick = {
                                selectedRoleForAuth = UserRole.STORE_OWNER
                                customEmailInput = ""
                                errorMessage = null
                                showGoogleDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_owner_login"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxeGold,
                                contentColor = Color.Black
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = "Storeowner Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Store Owner Login (Google)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Super Admin Login (Google)
                        Button(
                            onClick = {
                                selectedRoleForAuth = UserRole.SUPER_ADMIN
                                customEmailInput = ""
                                errorMessage = null
                                showGoogleDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_super_admin_login"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxeBurgundy,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, LuxeGold.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Super Admin Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Super Admin Login (Google)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Separator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.15f)
                            )
                            Text(
                                text = "OR",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.15f)
                            )
                        }

                        // Continue as Guest Option
                        OutlinedButton(
                            onClick = {
                                AuthManager.continueAsGuest()
                                onNavigateToRoute("customer_home")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_guest_mode"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Guest Icon",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Continue as Guest",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Bottom security footer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Secure lock",
                        tint = LuxeGold.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Verified Google and Firebase Auth Protection Layer",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Custom Google Sign-In Selection dialog representing real Firebase flow
        if (showGoogleDialog && selectedRoleForAuth != null) {
            Dialog(onDismissRequest = { showGoogleDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .testTag("google_auth_dialog"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Google Brand visual header matching official chooser style
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "oogle",
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Normal,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }

                        Text(
                            text = "Choose an account to continue to TS LuxeWear",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // If error occurred
                        if (errorMessage != null) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Use local state to toggle custom manual email entry mode
                        var showCustomInput by remember { mutableStateOf(false) }

                        if (!showCustomInput) {
                            // Present realistic, highly descriptive Google account suggestions based on role
                            val accounts = when (selectedRoleForAuth) {
                                UserRole.SUPER_ADMIN -> listOf(
                                    Triple("shakirsir2122@gmail.com", "Shakir Sir (Super Admin)", "S"),
                                    Triple("stalha2110@gmail.com", "Talha Admin (Super Admin)", "T")
                                )
                                UserRole.STORE_OWNER -> listOf(
                                    Triple("stalha.boutique@gmail.com", "Talha Boutique Owner", "B"),
                                    Triple("luxe.saree.owner@gmail.com", "Saree House Boutique Owner", "S"),
                                    Triple("wardrobe.owner@gmail.com", "Royal Wardrobe Boutique Owner", "W")
                                )
                                else -> listOf(
                                    Triple("shakir.customer@gmail.com", "Shakir Shopper", "S"),
                                    Triple("stalha.customer@gmail.com", "Talha Customer", "T"),
                                    Triple("shakirsir2122@gmail.com", "Shakir (Customer Mode)", "S")
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                accounts.forEach { (email, name, initial) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF8F9FA))
                                            .border(1.dp, Color(0xFFE8EAED), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val success = AuthManager.performGoogleLogin(email, selectedRoleForAuth!!, onError = { error ->
                                                    errorMessage = error
                                                })
                                                if (success) {
                                                    showGoogleDialog = false
                                                    val loggedIn = AuthManager.currentUserFlow.value
                                                    if (loggedIn != null) {
                                                        onLoginSuccess(loggedIn.email, loggedIn.role)
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (selectedRoleForAuth) {
                                                        UserRole.SUPER_ADMIN -> LuxeBurgundy
                                                        UserRole.STORE_OWNER -> LuxeGold
                                                        else -> Color(0xFF4285F4)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = initial,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray
                                            )
                                            Text(
                                                text = email,
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Identity",
                                            tint = Color(0xFF34A853).copy(alpha = 0.82f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Native '+ Use another account' option
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFDADCE0), RoundedCornerShape(8.dp))
                                        .clickable { showCustomInput = true }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Account",
                                        tint = Color(0xFF1A73E8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Use another Gmail account",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1A73E8)
                                    )
                                }
                            }
                        } else {
                            // Custom input layout for manual verification
                            Text(
                                text = "Enter custom Gmail account to verify identity:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                textAlign = TextAlign.Center
                            )

                            OutlinedTextField(
                                value = customEmailInput,
                                onValueChange = {
                                    customEmailInput = it
                                    errorMessage = null
                                },
                                label = { Text("Gmail Address") },
                                placeholder = { Text("your.name@gmail.com") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("google_email_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = LuxeBurgundy)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TextButton(
                                    onClick = { showCustomInput = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Back", color = Color.Gray)
                                }

                                Button(
                                    onClick = {
                                        val success = AuthManager.performGoogleLogin(customEmailInput, selectedRoleForAuth!!, onError = { error ->
                                            errorMessage = error
                                        })
                                        if (success) {
                                            showGoogleDialog = false
                                            val loggedIn = AuthManager.currentUserFlow.value
                                            if (loggedIn != null) {
                                                onLoginSuccess(loggedIn.email, loggedIn.role)
                                            }
                                        } else {
                                            if (selectedRoleForAuth == UserRole.SUPER_ADMIN && !AuthManager.isSuperAdminEmail(customEmailInput)) {
                                                showGoogleDialog = false
                                                AuthManager.continueAsGuest()
                                                Toast.makeText(context, "Unauthorized Access - You are not authorized to access Super Admin", Toast.LENGTH_LONG).show()
                                                onNavigateToRoute("customer_home")
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .testTag("submit_google_auth"),
                                    colors = ButtonDefaults.buttonColors(containerColor = LuxeBurgundy)
                                ) {
                                    Text("Sign In", color = Color.White)
                                }
                            }
                        }

                        // Google fine print disclaimer
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "To continue, Google will share your name, email address, and profile picture with TS LuxeWear.",
                            fontSize = 10.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { showGoogleDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSelectionRow(
    email: String,
    displayName: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = email.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4),
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = email,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = displayName,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun innerPaddingForSparkles() = PaddingValues(0.dp)

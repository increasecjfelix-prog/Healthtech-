package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: HealthTechViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Create Account

    // Sign In form fields
    var loginEmail by remember { mutableStateOf("increasecjfelix@gmail.com") }
    var loginPassword by remember { mutableStateOf("password123") }
    var loginRole by remember { mutableStateOf(UserRole.PATIENT) }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Sign Up form fields
    var signupFullName by remember { mutableStateOf("") }
    var signupEmail by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var signupPhone by remember { mutableStateOf("") }
    var signupRole by remember { mutableStateOf(UserRole.PATIENT) }
    var signupLicenseNumber by remember { mutableStateOf("") }
    var signupFacilityName by remember { mutableStateOf("") }
    var showSignupPassword by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(TealPrimary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "HealthTech Access",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onAuthSuccess) {
                        Text("Skip")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card with Verified Standard Notice
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Verified Health Network",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = TealPrimary
                        )
                        Text(
                            "Secure portal for patients, council-licensed doctors, pharmacists, and hospital emergency triage teams.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tab Selector: Sign In vs Sign Up
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign In", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_sign_in")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Account", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_create_account")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                // ==================== SIGN IN ====================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Sign in to your HealthTech profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Role selector for sign in
                    Text("Select your primary access role:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            FilterChip(
                                selected = loginRole == role,
                                onClick = { loginRole = role },
                                label = { Text(role.label, fontSize = 11.sp) },
                                modifier = Modifier.testTag("signin_role_${role.name}")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = loginEmail,
                        onValueChange = { loginEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                Icon(
                                    imageVector = if (showLoginPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    // Quick Demo Credentials Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💡 Quick Access Demo Accounts:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = {
                                    loginEmail = "felix.patient@healthtech.com"
                                    loginRole = UserRole.PATIENT
                                }) { Text("Patient", fontSize = 11.sp) }

                                TextButton(onClick = {
                                    loginEmail = "dr.alabi@stlukehospital.org"
                                    loginRole = UserRole.DOCTOR
                                }) { Text("Doctor", fontSize = 11.sp) }

                                TextButton(onClick = {
                                    loginEmail = "pharm.david@apexmed.com"
                                    loginRole = UserRole.PHARMACIST
                                }) { Text("Pharmacist", fontSize = 11.sp) }

                                TextButton(onClick = {
                                    loginEmail = "er.desk@centralhospital.org"
                                    loginRole = UserRole.HOSPITAL
                                }) { Text("ER Desk", fontSize = 11.sp) }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            viewModel.logIn(loginEmail, loginPassword, loginRole)
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_login_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In to HealthTech", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account yet? ", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Create One",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable { selectedTab = 1 }
                        )
                    }
                }
            } else {
                // ==================== CREATE ACCOUNT ====================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Register New HealthTech Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Select your professional or patient category:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            FilterChip(
                                selected = signupRole == role,
                                onClick = { signupRole = role },
                                label = { Text(role.label, fontSize = 11.sp) },
                                modifier = Modifier.testTag("signup_role_${role.name}")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = signupFullName,
                        onValueChange = { signupFullName = it },
                        label = { Text("Full Legal Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_fullname_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = signupEmail,
                        onValueChange = { signupEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = signupPhone,
                        onValueChange = { signupPhone = it },
                        label = { Text("Phone Number (with country code)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = signupPassword,
                        onValueChange = { signupPassword = it },
                        label = { Text("Create Secure Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showSignupPassword = !showSignupPassword }) {
                                Icon(
                                    imageVector = if (showSignupPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (showSignupPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    // Additional fields if Doctor, Pharmacist, or Hospital
                    if (signupRole != UserRole.PATIENT) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Professional Credential Info",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TealPrimary
                                    )
                                }

                                OutlinedTextField(
                                    value = signupLicenseNumber,
                                    onValueChange = { signupLicenseNumber = it },
                                    label = { Text("Council Practicing License #") },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_license_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    placeholder = { Text("e.g. MDCN/Reg/12345 or PCN/54321") }
                                )

                                OutlinedTextField(
                                    value = signupFacilityName,
                                    onValueChange = { signupFacilityName = it },
                                    label = { Text("Hospital / Pharmacy / Clinic Name") },
                                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_facility_input"),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            viewModel.signUp(
                                fullName = signupFullName,
                                email = signupEmail,
                                role = signupRole,
                                licenseNumber = signupLicenseNumber,
                                facilityName = signupFacilityName,
                                phone = signupPhone
                            )
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_signup_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Registration", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already registered? ", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Sign In",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable { selectedTab = 0 }
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HealthTechViewModel,
    onBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val settings by viewModel.appSettings.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings & Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Account Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(TealPrimary.copy(alpha = 0.15f), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentUser?.fullName ?: "Guest User",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        if (currentUser?.isVerified == true) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified",
                                                tint = VerifiedBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = currentUser?.email ?: "Not signed in",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VerifiedBadgeBg
                            ) {
                                Text(
                                    text = currentUser?.role?.label ?: "Visitor",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerifiedBlue,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (isLoggedIn && currentUser != null) {
                            val user = currentUser!!
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                            // Quick Medical/Professional details
                            if (user.licenseNumber.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Council License:", style = MaterialTheme.typography.labelSmall)
                                    Text(user.licenseNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            if (user.facilityName.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Affiliated Facility:", style = MaterialTheme.typography.labelSmall)
                                    Text(user.facilityName, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Emergency Contact:", style = MaterialTheme.typography.labelSmall)
                                Text("${user.emergencyContactName} (${user.emergencyContactPhone})", style = MaterialTheme.typography.labelSmall)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Blood Group & Allergies:", style = MaterialTheme.typography.labelSmall)
                                Text("${user.bloodGroup} • ${user.allergies}", style = MaterialTheme.typography.labelSmall, color = TealPrimary)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showEditProfileDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Details", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { showLogoutConfirmDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sign Out", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToAuth,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Sign In or Create Account")
                            }
                        }
                    }
                }
            }

            // 2. Clinical Emergency & Hospital Settings
            item {
                Text(
                    "Emergency & Hospital Dispatch Preferences",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingSwitchRow(
                            icon = Icons.Default.Emergency,
                            title = "Emergency SMS Pre-Alerts",
                            subtitle = "Notify next-of-kin automatically when ER triage dispatch is triggered",
                            checked = settings.emergencySmsAlerts,
                            onCheckedChange = { viewModel.updateSetting(emergencySmsAlerts = it) }
                        )

                        HorizontalDivider(thickness = 0.5.dp)

                        SettingSwitchRow(
                            icon = Icons.Default.LocalHospital,
                            title = "Share Vitals with Receiving ER",
                            subtitle = "Transmits blood group and known allergies directly to hospital triage team",
                            checked = settings.autoShareVitalsWithER,
                            onCheckedChange = { viewModel.updateSetting(autoShareVitalsWithER = it) }
                        )
                    }
                }
            }

            // 3. Pharmacy & Notifications
            item {
                Text(
                    "Pharmacy & Health Notifications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingSwitchRow(
                            icon = Icons.Default.Notifications,
                            title = "Push Notifications",
                            subtitle = "Receive real-time alerts for prescription refills and ER capacity status",
                            checked = settings.notificationsEnabled,
                            onCheckedChange = { viewModel.updateSetting(notificationsEnabled = it) }
                        )

                        HorizontalDivider(thickness = 0.5.dp)

                        SettingSwitchRow(
                            icon = Icons.Default.CheckCircle,
                            title = "Prescription Refill Reminders",
                            subtitle = "Automatic alerts when medication course reaches final 3 days",
                            checked = settings.prescriptionReminders,
                            onCheckedChange = { viewModel.updateSetting(prescriptionReminders = it) }
                        )
                    }
                }
            }

            // 4. Security & Offline Mode
            item {
                Text(
                    "Security, Sync & Data",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingSwitchRow(
                            icon = Icons.Default.Fingerprint,
                            title = "Biometric Authentication",
                            subtitle = "Require fingerprint or biometric scan before dispensing POS orders",
                            checked = settings.biometricAuthEnabled,
                            onCheckedChange = { viewModel.updateSetting(biometricAuthEnabled = it) }
                        )

                        HorizontalDivider(thickness = 0.5.dp)

                        SettingSwitchRow(
                            icon = Icons.Default.CloudSync,
                            title = "Offline Room Database Sync",
                            subtitle = "Allow full local inventory and POS operation when network is unavailable",
                            checked = settings.offlineSyncEnabled,
                            onCheckedChange = { viewModel.updateSetting(offlineSyncEnabled = it) }
                        )

                        HorizontalDivider(thickness = 0.5.dp)

                        SettingSwitchRow(
                            icon = Icons.Default.Speed,
                            title = "Data Saver Mode",
                            subtitle = "Reduces image loads for medical articles in low-bandwidth regions",
                            checked = settings.dataSaverMode,
                            onCheckedChange = { viewModel.updateSetting(dataSaverMode = it) }
                        )
                    }
                }
            }

            // 5. App Info & Standards
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("HealthTech Platform Standards", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                        Text("Version 1.0.0 (Production Build)", style = MaterialTheme.typography.labelSmall)
                        Text("Compliant with National Digital Health Architecture and Council Anti-Misinformation Guidelines.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        val user = currentUser
        var editName by remember { mutableStateOf(user?.fullName ?: "") }
        var editPhone by remember { mutableStateOf(user?.phone ?: "") }
        var editContactName by remember { mutableStateOf(user?.emergencyContactName ?: "") }
        var editContactPhone by remember { mutableStateOf(user?.emergencyContactPhone ?: "") }
        var editBloodGroup by remember { mutableStateOf(user?.bloodGroup ?: "O+") }
        var editAllergies by remember { mutableStateOf(user?.allergies ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Update Profile Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Legal Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editContactName,
                        onValueChange = { editContactName = it },
                        label = { Text("Emergency Contact Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editContactPhone,
                        onValueChange = { editContactPhone = it },
                        label = { Text("Emergency Contact Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editBloodGroup,
                            onValueChange = { editBloodGroup = it },
                            label = { Text("Blood Group") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = editAllergies,
                            onValueChange = { editAllergies = it },
                            label = { Text("Allergies") },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserProfile(
                            fullName = editName,
                            phone = editPhone,
                            emergencyContactName = editContactName,
                            emergencyContactPhone = editContactPhone,
                            bloodGroup = editBloodGroup,
                            allergies = editAllergies
                        )
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Sign Out of HealthTech?") },
            text = {
                Text("Are you sure you want to sign out? You will need to log back in to access licensed prescribing, POS sales, and emergency dispatch features.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logOut()
                        showLogoutConfirmDialog = false
                        onNavigateToAuth()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TealPrimary)
        )
    }
}

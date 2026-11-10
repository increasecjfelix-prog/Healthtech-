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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.VerificationRecord
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyAmberContainer
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyGreenContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@Composable
fun VerificationScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val verifications by viewModel.verifications.collectAsStateWithLifecycle()
    val lookupResult by viewModel.lookupResult.collectAsStateWithLifecycle()

    var showApplyDialog by remember { mutableStateOf(false) }
    var searchLicenseInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // System Purpose & Regulatory Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Certificate-Based Verification System",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Every doctor, pharmacist, and healthcare facility undergoes rigorous credential verification against official government registers and regulatory council databases before publishing clinical advice, managing pharmacy POS inventories, or updating hospital status.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Active Role & Simulation Selector
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Active Session Role",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VerifiedBadgeBg
                        ) {
                            Text(
                                text = currentRole.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = VerifiedBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Switch roles to experience how verified features unlock based on certified professional status:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(UserRole.values()) { role ->
                            FilterChip(
                                selected = currentRole == role,
                                onClick = {
                                    viewModel.setRole(role)
                                    viewModel.showMessage("Switched active profile to: ${role.label}")
                                },
                                label = { Text(role.label, fontSize = 11.sp) },
                                leadingIcon = if (currentRole == role) {
                                    { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                modifier = Modifier.testTag("role_switch_${role.name}")
                            )
                        }
                    }
                }
            }
        }

        // Apply for Verification Action Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Submit Professional Credentials",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Doctors, Pharmacists, and Health Facilities",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Upload your annual practicing license, certificate of registration, or facility operating permit to unlock full access to medical publishing, inventory management, and POS dispensing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showApplyDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_verification_application_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Certificate Verification Flow")
                    }
                }
            }
        }

        // Public Registry Lookup
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Public Regulatory Registry Lookup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Verify authenticity of any healthcare professional or facility:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchLicenseInput,
                            onValueChange = { searchLicenseInput = it },
                            placeholder = { Text("e.g., MDCN/Reg/39012 or PCN/R/55410") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("license_search_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (searchLicenseInput.isNotBlank()) {
                                    viewModel.lookupLicense(searchLicenseInput)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("search_license_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }

                    lookupResult?.let { record ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = EmergencyGreenContainer.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, EmergencyGreen)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        record.applicantName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    VerifiedBadge(licenseNumber = record.licenseNumber)
                                }
                                Text("Facility: ${record.facilityName}", style = MaterialTheme.typography.bodySmall)
                                Text("Council: ${record.regulatoryCouncil}", style = MaterialTheme.typography.labelSmall)
                                Text("Specialty: ${record.specialization}", style = MaterialTheme.typography.labelSmall)
                                Text("Document: ${record.certificateDocName}", style = MaterialTheme.typography.labelSmall, color = TealPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "✓ Identity & Council Practicing License Authenticated",
                                    color = EmergencyGreen,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Regulatory Verification Records Ledger
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Verification Applications & Registry Records (${verifications.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(verifications, key = { it.id }) { record ->
            VerificationRecordCard(
                record = record,
                onApproveDemo = { viewModel.approveVerificationDemo(record.id) }
            )
        }
    }

    // Apply Verification Dialog
    if (showApplyDialog) {
        ApplyVerificationDialog(
            onDismiss = { showApplyDialog = false },
            onSubmit = { name, facility, prof, council, licNo, spec, doc ->
                viewModel.submitVerification(name, facility, prof, council, licNo, spec, doc)
                showApplyDialog = false
            }
        )
    }
}

@Composable
fun VerificationRecordCard(
    record: VerificationRecord,
    onApproveDemo: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("verification_record_${record.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        record.applicantName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "${record.professionType} • ${record.facilityName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badge
                if (record.status == "VERIFIED") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmergencyGreenContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmergencyGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VERIFIED", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = EmergencyGreen)
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmergencyAmberContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = EmergencyAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PENDING REVIEW", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = EmergencyAmber)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Council: ${record.regulatoryCouncil}", style = MaterialTheme.typography.labelSmall)
                    Text("License: ${record.licenseNumber}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Specialty: ${record.specialization}", style = MaterialTheme.typography.labelSmall)
                    Text("Cert: ${record.certificateDocName}", style = MaterialTheme.typography.labelSmall, color = TealPrimary)
                }
            }

            if (record.status != "VERIFIED") {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onApproveDemo,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulate Regulatory Council Approval")
                }
            }
        }
    }
}

@Composable
fun ApplyVerificationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var facilityName by remember { mutableStateOf("") }
    var professionType by remember { mutableStateOf("DOCTOR") }
    var regulatoryCouncil by remember { mutableStateOf("Medical & Dental Council (MDCN)") }
    var licenseNumber by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var docName by remember { mutableStateOf("Official_Medical_Practicing_License_2026.pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = TealPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Professional Certificate Verification")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Profession / Entity Type:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("DOCTOR", "PHARMACIST", "HOSPITAL").forEach { prof ->
                        FilterChip(
                            selected = professionType == prof,
                            onClick = {
                                professionType = prof
                                regulatoryCouncil = when (prof) {
                                    "DOCTOR" -> "Medical & Dental Council (MDCN)"
                                    "PHARMACIST" -> "Pharmacy Council (PCN)"
                                    else -> "State Health Facility Regulatory Board"
                                }
                            },
                            label = { Text(prof, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Legal Name of Practitioner / Admin") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = facilityName,
                    onValueChange = { facilityName = it },
                    label = { Text("Hospital / Pharmacy Outlet / Clinic Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Practicing License / Registration Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = specialization,
                    onValueChange = { specialization = it },
                    label = { Text("Clinical Specialization / Department") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Simulated Document Upload Card
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Attached Credential Certificate:", style = MaterialTheme.typography.labelSmall)
                            Text(docName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = {
                            docName = "Scanned_Council_Certificate_${(100..999).random()}.pdf"
                        }) {
                            Text("Attach File", fontSize = 11.sp)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Text(
                        "🔒 I certify under penalty of regulatory sanctions that the credentials provided are accurate and issued by the authorized regulatory body.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && licenseNumber.isNotBlank()) {
                        onSubmit(name, facilityName, professionType, regulatoryCouncil, licenseNumber, specialization, docName)
                    }
                },
                enabled = name.isNotBlank() && licenseNumber.isNotBlank(),
                modifier = Modifier.testTag("submit_verification_application_button")
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

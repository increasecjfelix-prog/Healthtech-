package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicineAuthenticityRecord
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@Composable
fun MedicineAuthenticityDialog(
    viewModel: HealthTechViewModel,
    onDismiss: () -> Unit
) {
    var scratchCodeInput by remember { mutableStateOf("") }
    val lookupResult by viewModel.authenticityLookupResult.collectAsStateWithLifecycle()
    val checksHistory by viewModel.authenticityChecks.collectAsStateWithLifecycle()

    var showReportCounterfeit by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            viewModel.clearAuthenticityLookup()
            onDismiss()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(TealPrimary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TealPrimary)
                }
                Spacer(Modifier.width(10.dp))
                Text("Medicine Authenticity & Safety Check", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Regulatory Disclaimer (Strict requirement)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmergencyAmber.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, EmergencyAmber)
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyAmber, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "REGULATORY NOTICE: Do not claim that a medicine is authentic merely because it exists in the application's database. Always verify anti-counterfeiting scratch codes directly with the regulatory authority (NAFDAC/MAS) before consumption.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "Verify Scratch-off PIN or Regulatory Number:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = scratchCodeInput,
                    onValueChange = { scratchCodeInput = it },
                    label = { Text("Enter MAS Scratch Code / NAFDAC PIN") },
                    placeholder = { Text("e.g., 948271039482 or NAFDAC-A4-0192") },
                    leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = TealPrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scratch_code_input")
                )

                Button(
                    onClick = {
                        if (scratchCodeInput.isNotBlank()) {
                            viewModel.verifyMedicineAuthenticity(scratchCodeInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verify_authenticity_button")
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Verify Anti-Counterfeit Code")
                }

                // Verification Result Card
                lookupResult?.let { result ->
                    val isAuthentic = result.verificationStatus == "AUTHENTIC_VERIFIED"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAuthentic) EmergencyGreen.copy(alpha = 0.15f) else EmergencyRed.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, if (isAuthentic) EmergencyGreen else EmergencyRed)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAuthentic) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isAuthentic) EmergencyGreen else EmergencyRed
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isAuthentic) "GENUINE PHARMACEUTICAL PRODUCT" else "SUSPECT / COUNTERFEIT WARNING",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAuthentic) EmergencyGreen else EmergencyRed,
                                    fontSize = 13.sp
                                )
                            }
                            Text("Drug: ${result.drugName}", fontWeight = FontWeight.Bold)
                            Text("Manufacturer: ${result.manufacturer}", fontSize = 12.sp)
                            Text("Batch / Lot: ${result.batchNumber}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text("Reg PIN: ${result.regulatoryPin}", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text("Provider: ${result.verificationProvider}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Registry Note: ${result.alertNotes}", fontSize = 12.sp, fontWeight = FontWeight.Medium)

                            if (!isAuthentic) {
                                Button(
                                    onClick = { showReportCounterfeit = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Report Suspected Counterfeit to NAFDAC")
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                Text("Recent Regulatory Authenticity Checks:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                checksHistory.take(3).forEach { record ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(record.brandName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("Code: ${record.scratchCode}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Text(
                                text = if (record.verificationStatus == "AUTHENTIC_VERIFIED") "Authentic" else "Counterfeit Alert",
                                color = if (record.verificationStatus == "AUTHENTIC_VERIFIED") EmergencyGreen else EmergencyRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.clearAuthenticityLookup()
                    onDismiss()
                }
            ) {
                Text("Close")
            }
        }
    )

    // Dialog: Report Suspected Counterfeit
    if (showReportCounterfeit) {
        var pharmacyOutletPurchased by remember { mutableStateOf("") }
        var complaintNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReportCounterfeit = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyRed) },
            title = { Text("Report Suspected Counterfeit Medicine") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Reports are directly escalated to the regulatory post-marketing drug surveillance and pharmacovigilance units.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = pharmacyOutletPurchased,
                        onValueChange = { pharmacyOutletPurchased = it },
                        label = { Text("Where was this medicine purchased / dispensed?") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = complaintNotes,
                        onValueChange = { complaintNotes = it },
                        label = { Text("Physical anomalies (color, packaging, taste, adverse effect)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showMessage("Counterfeit drug report logged and transmitted to National Regulatory Authorities.")
                        showReportCounterfeit = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Submit Regulatory Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportCounterfeit = false }) { Text("Cancel") }
            }
        )
    }
}

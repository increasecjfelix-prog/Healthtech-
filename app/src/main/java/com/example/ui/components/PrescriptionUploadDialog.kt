package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PharmacyOutlet
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.HealthTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionUploadDialog(
    viewModel: HealthTechViewModel,
    initialPharmacy: PharmacyOutlet? = null,
    onDismiss: () -> Unit
) {
    val pharmacies by viewModel.pharmacies.collectAsStateWithLifecycle()
    var selectedPharmacy by remember { mutableStateOf(initialPharmacy ?: pharmacies.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }

    var prescribingDoctor by remember { mutableStateOf("Dr. Adaobi Okeke (MDCN/R/2016/39482)") }
    var diagnosis by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var documentName by remember { mutableStateOf("Signed_Prescription_Rx_${System.currentTimeMillis() % 1000}.pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TealPrimary) },
        title = { Text("Submit Patient Prescription") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Submit an official digital prescription to a verified registered pharmacy outlet for dispensary verification and order fulfillment.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Select Target Pharmacy
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedPharmacy?.name ?: "Select Pharmacy",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fulfilling Pharmacy") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        pharmacies.forEach { pharm ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(pharm.name, fontWeight = FontWeight.Bold)
                                        Text("${pharm.distanceKm} km away • ${pharm.operatingHours}", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedPharmacy = pharm
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = prescribingDoctor,
                    onValueChange = { prescribingDoctor = it },
                    label = { Text("Prescribing Clinician Name & License") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Clinical Diagnosis (e.g. Hypertension)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = medications,
                    onValueChange = { medications = it },
                    label = { Text("Medications & Dosage Directions") },
                    placeholder = { Text("e.g., Amlodipine 5mg OD x 30 days\nMetformin 500mg BD") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = TealPrimary)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Attached File:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(documentName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                documentName = "Rx_Scan_${System.currentTimeMillis() % 10000}.pdf"
                                viewModel.showMessage("New prescription file attached: $documentName")
                            }
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Re-upload")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pharm = selectedPharmacy
                    if (pharm != null && medications.isNotBlank()) {
                        viewModel.submitPrescriptionRequest(
                            pharmacyId = pharm.id,
                            pharmacyName = pharm.name,
                            prescribingDoctor = prescribingDoctor,
                            diagnosis = diagnosis,
                            medications = medications,
                            documentName = documentName
                        )
                        onDismiss()
                    } else {
                        viewModel.showMessage("Please fill in medications to prescribe.")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("submit_prescription_dialog_button")
            ) {
                Text("Transmit to Pharmacy")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

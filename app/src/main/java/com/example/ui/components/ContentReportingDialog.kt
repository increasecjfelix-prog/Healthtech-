package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.EmergencyRed
import com.example.ui.viewmodel.HealthTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentReportingDialog(
    contentId: String,
    contentType: String, // "ARTICLE" or "VIDEO"
    contentTitle: String,
    authorName: String,
    authorRole: String,
    viewModel: HealthTechViewModel,
    onDismiss: () -> Unit
) {
    val reportReasons = listOf(
        "Misleading Information",
        "Harmful Medical Advice",
        "Unlicensed Clinical Claims",
        "Outdated Treatment Protocol",
        "Commercial Bias / Unapproved Promotion",
        "Offensive Content"
    )

    var selectedReason by remember { mutableStateOf(reportReasons[0]) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Report, contentDescription = null, tint = EmergencyRed) },
        title = { Text("Report Inappropriate Medical Content") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "All flagged medical education content is immediately reviewed by our Clinical Review Board and Platform Compliance Administrators.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(contentTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Author: $authorName ($authorRole)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedReason,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Reason for Reporting") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        reportReasons.forEach { reason ->
                            DropdownMenuItem(
                                text = { Text(reason) },
                                onClick = {
                                    selectedReason = reason
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Specific clinical concerns or evidence") },
                    placeholder = { Text("Explain why this advice could be inaccurate or unsafe for patients...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.submitContentReport(
                        contentId = contentId,
                        contentType = contentType,
                        contentTitle = contentTitle,
                        authorName = authorName,
                        authorRole = authorRole,
                        reasonCategory = selectedReason,
                        details = details.ifBlank { "Flagged for regulatory compliance and safety inspection." }
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                modifier = Modifier.testTag("submit_report_button")
            ) {
                Text("Submit Regulatory Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

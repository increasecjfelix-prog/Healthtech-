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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AuditLogRecord
import com.example.data.model.HospitalProfile
import com.example.data.model.PharmacyOutlet
import com.example.data.model.ReportedContentRecord
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserRole
import com.example.data.model.VerificationRecord
import com.example.data.security.AuthSecurity
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // Enforce Role-Based Access Control (RBAC)
    if (currentRole != UserRole.ADMIN) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Access Restricted",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Administrator Access Required",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This regulatory dashboard is restricted to authorized platform compliance administrators under national health information safety guidelines.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { viewModel.switchRole(UserRole.ADMIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.testTag("switch_to_admin_button")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Switch to Administrator View")
                    }
                }
            }
        }
        return
    }

    var selectedAdminTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Verifications", "Accreditations", "Content Moderation", "Users", "Audit Trail")

    val verifications by viewModel.verifications.collectAsStateWithLifecycle()
    val pharmacies by viewModel.pharmacies.collectAsStateWithLifecycle()
    val hospitals by viewModel.hospitals.collectAsStateWithLifecycle()
    val reports by viewModel.allReportedContent.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

    var reviewRecordDialog by remember { mutableStateOf<VerificationRecord?>(null) }
    var reviewReportDialog by remember { mutableStateOf<ReportedContentRecord?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Admin Header Banner
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TealPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Regulatory Oversight Console",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Admin: ${currentUser?.fullName ?: "Super Admin"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        color = EmergencyGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmergencyGreen, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("SECURE RBAC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmergencyGreen)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedAdminTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedAdminTab == index,
                            onClick = { selectedAdminTab = index },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(title, fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal)
                                    if (index == 0) {
                                        val pendingCount = verifications.count { it.status == "PENDING" }
                                        if (pendingCount > 0) {
                                            Spacer(Modifier.width(6.dp))
                                            Surface(
                                                color = EmergencyAmber,
                                                shape = CircleShape
                                            ) {
                                                Text(
                                                    text = pendingCount.toString(),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    } else if (index == 2) {
                                        val pendingReports = reports.count { it.status == "PENDING_REVIEW" }
                                        if (pendingReports > 0) {
                                            Spacer(Modifier.width(6.dp))
                                            Surface(
                                                color = EmergencyRed,
                                                shape = CircleShape
                                            ) {
                                                Text(
                                                    text = pendingReports.toString(),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Tab Contents
        when (selectedAdminTab) {
            0 -> AdminVerificationsTab(
                verifications = verifications,
                onReview = { reviewRecordDialog = it }
            )
            1 -> AdminAccreditationsTab(
                pharmacies = pharmacies,
                hospitals = hospitals,
                onTogglePharmacy = { id, verified -> viewModel.adminSetPharmacyVerification(id, verified) },
                onToggleHospital = { id, verified -> viewModel.adminSetHospitalVerification(id, verified) }
            )
            2 -> AdminContentModerationTab(
                reports = reports,
                onReviewReport = { reviewReportDialog = it }
            )
            3 -> AdminUsersTab(
                users = users,
                onToggleSuspension = { id, suspended -> viewModel.adminToggleUserSuspension(id, suspended) }
            )
            4 -> AdminAuditTrailTab(auditLogs = auditLogs)
        }
    }

    // Dialog: Review Professional Verification
    reviewRecordDialog?.let { record ->
        var reviewerNotes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { reviewRecordDialog = null },
            icon = { Icon(Icons.Default.Verified, contentDescription = null, tint = TealPrimary) },
            title = { Text("Review Professional Credentials") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Applicant: ${record.applicantName}", fontWeight = FontWeight.Bold)
                    Text(text = "Profession: ${record.professionType} • ${record.specialization}")
                    Text(text = "Regulatory Council: ${record.regulatoryCouncil}")
                    Text(text = "License No: ${record.licenseNumber}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                    
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = TealPrimary)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Submitted Certificate:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(record.certificateDocName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reviewerNotes,
                        onValueChange = { reviewerNotes = it },
                        label = { Text("Regulatory Reviewer Notes") },
                        placeholder = { Text("e.g., Verified against council digital register.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminReviewVerification(record.id, isApproved = true, reviewerNotes)
                        reviewRecordDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                    modifier = Modifier.testTag("admin_approve_verification_button")
                ) {
                    Text("Approve & Issue Badge")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { reviewRecordDialog = null }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            viewModel.adminReviewVerification(record.id, isApproved = false, reviewerNotes.ifBlank { "Credentials rejected by regulatory board." })
                            reviewRecordDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        modifier = Modifier.testTag("admin_reject_verification_button")
                    ) {
                        Text("Reject")
                    }
                }
            }
        )
    }

    // Dialog: Review Reported Medical Content
    reviewReportDialog?.let { report ->
        var actionNotes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { reviewReportDialog = null },
            icon = { Icon(Icons.Default.Report, contentDescription = null, tint = EmergencyRed) },
            title = { Text("Resolve Flagged Medical Content") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Title: ${report.contentTitle}", fontWeight = FontWeight.Bold)
                    Text("Author: ${report.authorName} (${report.authorRole})")
                    Text("Reason: ${report.reasonCategory}", color = EmergencyRed, fontWeight = FontWeight.SemiBold)
                    Text("Report Details: ${report.details}", fontSize = 13.sp)
                    Text("Reported By: ${report.reporterName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = actionNotes,
                        onValueChange = { actionNotes = it },
                        label = { Text("Action Notes / Author Feedback") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminResolveReportedContent(
                            reportId = report.id,
                            contentId = report.contentId,
                            contentType = report.contentType,
                            action = "CONTENT_REMOVED",
                            adminNotes = actionNotes.ifBlank { "Violated clinical evidence standards." }
                        )
                        reviewReportDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.testTag("admin_remove_content_button")
                ) {
                    Text("Take Down Content")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { reviewReportDialog = null }) {
                        Text("Close")
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.adminResolveReportedContent(
                                reportId = report.id,
                                contentId = report.contentId,
                                contentType = report.contentType,
                                action = "DISMISSED",
                                adminNotes = actionNotes.ifBlank { "Report reviewed; content verified as accurate." }
                            )
                            reviewReportDialog = null
                        }
                    ) {
                        Text("Dismiss Report")
                    }
                }
            }
        )
    }
}

@Composable
private fun AdminVerificationsTab(
    verifications: List<VerificationRecord>,
    onReview: (VerificationRecord) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Professional Verification Queue (${verifications.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(verifications, key = { it.id }) { record ->
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(record.applicantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = "${record.professionType} • ${record.regulatoryCouncil}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(record.status)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "License: ${record.licenseNumber}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Doc: ${record.certificateDocName}",
                            fontSize = 11.sp,
                            color = TealPrimary
                        )
                    }

                    if (record.reviewerNotes.isNotBlank()) {
                        Text(
                            text = "Notes: ${record.reviewerNotes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onReview(record) },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.testTag("review_record_${record.id}")
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Review Credentials")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAccreditationsTab(
    pharmacies: List<PharmacyOutlet>,
    hospitals: List<HospitalProfile>,
    onTogglePharmacy: (String, Boolean) -> Unit,
    onToggleHospital: (String, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Registered Pharmacy Outlets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(pharmacies, key = { it.id }) { pharmacy ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(pharmacy.name, fontWeight = FontWeight.Bold)
                        }
                        Text("Reg: ${pharmacy.licenseNumber} • ${pharmacy.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Hours: ${pharmacy.operatingHours}", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { onTogglePharmacy(pharmacy.id, !pharmacy.isVerified) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (pharmacy.isVerified) EmergencyGreen else EmergencyAmber
                        ),
                        modifier = Modifier.testTag("toggle_pharmacy_${pharmacy.id}")
                    ) {
                        Text(if (pharmacy.isVerified) "Verified" else "Unverified")
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Hospitals & Healthcare Facilities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(hospitals, key = { it.id }) { hospital ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(hospital.name, fontWeight = FontWeight.Bold)
                        }
                        Text("${hospital.facilityType} • Reg: ${hospital.licenseNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("ER Status: ${hospital.emergencyStatus}", fontSize = 11.sp, color = EmergencyGreen, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { onToggleHospital(hospital.id, !hospital.isVerified) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hospital.isVerified) EmergencyGreen else EmergencyAmber
                        ),
                        modifier = Modifier.testTag("toggle_hospital_${hospital.id}")
                    ) {
                        Text(if (hospital.isVerified) "Accredited" else "Suspended")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminContentModerationTab(
    reports: List<ReportedContentRecord>,
    onReviewReport: (ReportedContentRecord) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Reported Medical Content (${reports.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (reports.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "No reported medical content at this time. The clinical publication registry is in full compliance.",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(reports, key = { it.id }) { report ->
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (report.status == "PENDING_REVIEW") EmergencyRed else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Report, contentDescription = null, tint = EmergencyRed)
                            Spacer(Modifier.width(6.dp))
                            Text(report.reasonCategory, fontWeight = FontWeight.Bold, color = EmergencyRed)
                        }
                        StatusBadge(report.status)
                    }

                    Text("Article/Media: ${report.contentTitle}", fontWeight = FontWeight.SemiBold)
                    Text("Author: ${report.authorName} (${report.authorRole})", fontSize = 12.sp)
                    Text("Details: ${report.details}", fontSize = 13.sp)

                    if (report.status == "PENDING_REVIEW") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = { onReviewReport(report) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                modifier = Modifier.testTag("review_report_${report.id}")
                            ) {
                                Text("Review & Resolve")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersTab(
    users: List<UserAccountEntity>,
    onToggleSuspension: (String, Boolean) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Registered Accounts (${users.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(users, key = { it.id }) { user ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.fullName, fontWeight = FontWeight.Bold)
                            if (user.verificationStatus == "VERIFIED") {
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = VerifiedBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text("${user.role} • ${user.email}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (user.licenseNumber.isNotBlank()) {
                            Text("License: ${AuthSecurity.maskLicenseNumber(user.licenseNumber)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    OutlinedButton(
                        onClick = { onToggleSuspension(user.id, user.isSuspended) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (user.isSuspended) EmergencyGreen else EmergencyRed
                        ),
                        modifier = Modifier.testTag("suspend_user_${user.id}")
                    ) {
                        Icon(
                            imageVector = if (user.isSuspended) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (user.isSuspended) "Reactivate" else "Suspend")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAuditTrailTab(auditLogs: List<AuditLogRecord>) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = TealPrimary)
                Spacer(Modifier.width(8.dp))
                Text("System Audit Trail & Regulatory Logs (${auditLogs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        items(auditLogs, key = { it.id }) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = TealPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = log.actionType,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(dateFormat.format(Date(log.timestamp)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(log.description, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("Actor: ${log.actorName} (${log.actorRole}) • IP: ${log.ipAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "VERIFIED", "CONTENT_REMOVED" -> EmergencyGreen.copy(alpha = 0.15f) to EmergencyGreen
        "REJECTED" -> EmergencyRed.copy(alpha = 0.15f) to EmergencyRed
        "PENDING", "PENDING_REVIEW" -> EmergencyAmber.copy(alpha = 0.15f) to EmergencyAmber
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.replace("_", " "),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

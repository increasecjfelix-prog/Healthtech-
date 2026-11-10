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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.data.model.UserRole
import com.example.data.security.AuthSecurity
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@Composable
fun ProfessionalDashboardScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val articles by viewModel.allArticles.collectAsStateWithLifecycle()
    val videos by viewModel.allVideos.collectAsStateWithLifecycle()
    val prescriptions by viewModel.allPrescriptions.collectAsStateWithLifecycle()

    var showPublishArticleDialog by remember { mutableStateOf(false) }
    var showUploadVideoDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Clinical Overview", "My Publications", "Prescriptions")

    val isDoctor = currentRole == UserRole.DOCTOR
    val isPharmacist = currentRole == UserRole.PHARMACIST
    val isVerified = currentUser?.isVerified == true

    Column(modifier = modifier.fillMaxSize()) {
        // Professional Header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(TealPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TealPrimary)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentUser?.fullName ?: (if (isDoctor) "Dr. Medical Practitioner" else "Pharm. Clinical Pharmacist"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentRole.label} • ${currentUser?.specialty.orEmpty().ifBlank { "General Practice" }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Verification Status Badge
                    Surface(
                        color = if (isVerified) EmergencyGreen.copy(alpha = 0.15f) else EmergencyAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (isVerified) EmergencyGreen else EmergencyAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isVerified) "VERIFIED" else "PENDING REVIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVerified) EmergencyGreen else EmergencyAmber
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }

        // Verification Gate Check
        if (!isVerified) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmergencyAmber.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, EmergencyAmber)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyAmber)
                        Spacer(Modifier.width(8.dp))
                        Text("Credential Verification in Progress", fontWeight = FontWeight.Bold, color = EmergencyAmber)
                    }
                    Text(
                        "Your registration is currently under active review by the regulatory council (${currentUser?.regulatoryBody.orEmpty().ifBlank { if (isDoctor) "MDCN" else "PCN" }}). You will be able to publish clinical articles and sign prescriptions once verified.",
                        fontSize = 13.sp
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> ProfessionalOverviewTab(
                user = currentUser,
                role = currentRole,
                isVerified = isVerified,
                onOpenPublishArticle = { showPublishArticleDialog = true },
                onOpenUploadVideo = { showUploadVideoDialog = true }
            )
            1 -> ProfessionalPublicationsTab(
                articles = articles.filter { it.authorName.contains(currentUser?.fullName.orEmpty().take(6), ignoreCase = true) || it.authorLicenseNo == currentUser?.licenseNumber },
                videos = videos.filter { it.authorName.contains(currentUser?.fullName.orEmpty().take(6), ignoreCase = true) || it.authorLicenseNo == currentUser?.licenseNumber },
                onPublishArticle = { showPublishArticleDialog = true }
            )
            2 -> ProfessionalPrescriptionsTab(
                prescriptions = prescriptions
            )
        }
    }

    // Dialog: Publish Medical Article
    if (showPublishArticleDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(if (isPharmacist) "Medication safety" else "General health education") }
        var summary by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var takeaways by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showPublishArticleDialog = false },
            title = { Text("Publish Verified Medical Article") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Article Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (e.g. Medication safety, Preventive health)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Clinical Executive Summary") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Full Clinical Body") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = takeaways,
                        onValueChange = { takeaways = it },
                        label = { Text("Key Takeaways for Patients (Bullet Points)") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.publishMedicalArticle(
                            title = title.ifBlank { "Clinical Advisory" },
                            authorName = currentUser?.fullName ?: "Verified Professional",
                            authorRole = if (isDoctor) "Doctor" else "Pharmacist",
                            authorLicense = currentUser?.licenseNumber ?: "MDCN/PCN-REG",
                            category = category,
                            summary = summary.ifBlank { "Clinical health education advisory." },
                            content = content.ifBlank { "Clinical guidelines for patient wellness." },
                            keyTakeaways = takeaways.ifBlank { "• Consult your physician regularly" }
                        )
                        showPublishArticleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("publish_article_submit_button")
                ) {
                    Text("Publish to Public")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublishArticleDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Upload Clinical Video
    if (showUploadVideoDialog) {
        var videoTitle by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var duration by remember { mutableStateOf("04:30") }
        var cmeUnits by remember { mutableStateOf("2") }

        AlertDialog(
            onDismissRequest = { showUploadVideoDialog = false },
            title = { Text("Upload Verified Clinical Video") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = videoTitle,
                        onValueChange = { videoTitle = it },
                        label = { Text("Video Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Clinical Objectives") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cmeUnits,
                            onValueChange = { cmeUnits = it },
                            label = { Text("CME Units") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.uploadMedicalVideo(
                            title = videoTitle.ifBlank { "Clinical Procedure Video" },
                            description = description.ifBlank { "Evidence-based clinical demonstration." },
                            authorName = currentUser?.fullName ?: "Verified Clinician",
                            authorRole = if (isDoctor) "Doctor" else "Pharmacist",
                            authorLicense = currentUser?.licenseNumber ?: "MDCN-REG",
                            facilityName = currentUser?.facilityName ?: "Accredited Health Center",
                            category = "Clinical Procedures",
                            durationString = duration,
                            videoUrl = "https://healthtech.demo/clinical_stream.mp4",
                            takeaways = "• Follow standard sterile techniques\n• Monitor patient vital signs",
                            cmeUnits = cmeUnits.toIntOrNull() ?: 1
                        )
                        showUploadVideoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Upload Video")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadVideoDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ProfessionalOverviewTab(
    user: com.example.data.model.UserAccount?,
    role: UserRole,
    isVerified: Boolean,
    onOpenPublishArticle: () -> Unit,
    onOpenUploadVideo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Professional Credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Full Name: ${user?.fullName}", fontWeight = FontWeight.SemiBold)
                Text("Role: ${role.label}")
                Text("Regulatory Council: ${user?.regulatoryBody.orEmpty().ifBlank { if (role == UserRole.DOCTOR) "MDCN" else "PCN" }}")
                Text(
                    text = "License No: ${AuthSecurity.maskLicenseNumber(user?.licenseNumber.orEmpty().ifBlank { "MDCN/PCN-REG-8472" })}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
                Text("Affiliated Facility: ${user?.facilityName.orEmpty().ifBlank { "University Teaching Hospital" }}")
            }
        }

        if (isVerified) {
            Text("Professional Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("publish_article_card"),
                    onClick = onOpenPublishArticle
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(28.dp))
                        Text("Publish Article", fontWeight = FontWeight.Bold)
                        Text("Share evidence-based health guidance with public.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenUploadVideo
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.OndemandVideo, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                        Text("Upload Video", fontWeight = FontWeight.Bold)
                        Text("Post accredited clinical visual education.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalPublicationsTab(
    articles: List<com.example.data.model.MedicalArticle>,
    videos: List<com.example.data.model.MedicalVideo>,
    onPublishArticle: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Published Content", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onPublishArticle,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("New Publication")
                }
            }
        }

        if (articles.isEmpty() && videos.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "You haven't authored any publications yet. Tap 'New Publication' above to author verified medical education content.",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(articles, key = { it.id }) { art ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(art.title, fontWeight = FontWeight.Bold)
                    Text("Category: ${art.category} • ${art.publishedDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(art.summary, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Likes: ${art.likesCount}", fontSize = 11.sp, color = TealPrimary)
                        Text("Status: Approved & Public", fontSize = 11.sp, color = EmergencyGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalPrescriptionsTab(
    prescriptions: List<com.example.data.model.PrescriptionRequestRecord>
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Patient Prescription Requests (${prescriptions.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(prescriptions, key = { it.id }) { rx ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(rx.patientName, fontWeight = FontWeight.Bold)
                        Text(rx.status, color = TealPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Text("Target Pharmacy: ${rx.targetPharmacyName}", fontSize = 12.sp)
                    Text("Diagnosis: ${rx.diagnosisSummary}", fontSize = 12.sp)
                    Text("Rx: ${rx.medicationsList}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

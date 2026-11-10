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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicalArticle
import com.example.data.model.UserRole
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyGreenContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedMedicalScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentRole.collectAsStateWithLifecycle()
    val articles by viewModel.filteredArticles.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.articleCategoryFilter.collectAsStateWithLifecycle()

    var activeMediaTab by remember { mutableIntStateOf(0) } // 0 = Articles, 1 = Watch (Videos)
    var readingArticle by remember { mutableStateOf<MedicalArticle?>(null) }
    var showPublishDialog by remember { mutableStateOf(false) }
    var showGateDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Cardiology", "Pharmacology", "Pediatrics", "Preventive Care", "Maternal Health")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Segment Tab Row: Articles vs Watch Videos
        PrimaryTabRow(
            selectedTabIndex = activeMediaTab,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeMediaTab == 0,
                onClick = { activeMediaTab = 0 },
                text = { Text("Clinical Articles", fontWeight = if (activeMediaTab == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_medical_articles")
            )
            Tab(
                selected = activeMediaTab == 1,
                onClick = { activeMediaTab = 1 },
                text = { Text("Watch Feed", fontWeight = if (activeMediaTab == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.OndemandVideo, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_medical_watch")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeMediaTab == 1) {
            // Watch Feed for clinical educational video content
            MedicalWatchSection(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Credential Integrity Banner
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Verified Medical Publishing Standard",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "To protect public health and eliminate dangerous online misinformation, all medical advice and educational content is authored strictly by council-verified doctors and pharmacists, and peer-reviewed before publication.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = VerifiedBadgeBg
                                ) {
                                    Text(
                                        "🔒 Zero Unverified UGC",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VerifiedBlue,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (role == UserRole.DOCTOR || role == UserRole.PHARMACIST) {
                                            showPublishDialog = true
                                        } else {
                                            showGateDialog = true
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("publish_advice_button")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Publish Advice", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Category Filter Chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { viewModel.articleCategoryFilter.value = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                modifier = Modifier.testTag("filter_cat_$cat")
                            )
                        }
                    }
                }

                // Article Feed
                items(articles, key = { it.id }) { article ->
                    VerifiedArticleCard(
                        article = article,
                        onReadMore = { readingArticle = article },
                        onToggleBookmark = { viewModel.toggleBookmark(article) },
                        onLike = { viewModel.likeArticle(article.id) },
                        onShare = { viewModel.showMessage("Article link copied for clinical sharing.") }
                    )
                }
            }
        }
    }

    // Unverified Gate Dialog (Explaining professional verification)
    if (showGateDialog) {
        AlertDialog(
            onDismissRequest = { showGateDialog = false },
            icon = {
                Icon(Icons.Default.Security, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(36.dp))
            },
            title = { Text("Professional Verification Required") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Only verified Medical Doctors and Certified Pharmacists with validated practicing licenses may author health guidance on this platform.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Tip: You can switch your active role in the 'Verification' tab to test publishing as a Doctor or Pharmacist, or submit your council certificate for validation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGateDialog = false
                        viewModel.setRole(UserRole.DOCTOR)
                        viewModel.showMessage("Role switched to Licensed Doctor for testing.")
                        showPublishDialog = true
                    }
                ) {
                    Text("Switch to Doctor Role")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGateDialog = false }) { Text("Got It") }
            }
        )
    }

    // Article Reader Dialog
    readingArticle?.let { article ->
        ArticleDetailDialog(
            article = article,
            onDismiss = { readingArticle = null },
            onSubmitClarification = { question ->
                viewModel.showMessage("Question submitted to ${article.authorName}. Verified answer will appear shortly.")
            }
        )
    }

    // Publish Article Dialog (For verified doctors/pharmacists)
    if (showPublishDialog) {
        PublishArticleDialog(
            currentRole = role,
            onDismiss = { showPublishDialog = false },
            onPublish = { title, authorName, authorRole, license, cat, summary, content, takeaways ->
                viewModel.publishMedicalArticle(title, authorName, authorRole, license, cat, summary, content, takeaways)
                showPublishDialog = false
            }
        )
    }
}

@Composable
fun VerifiedArticleCard(
    article: MedicalArticle,
    onReadMore: () -> Unit,
    onToggleBookmark: () -> Unit,
    onLike: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("article_card_${article.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author Credential Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = article.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Doctor",
                            tint = VerifiedBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${article.authorRole} • ${article.authorLicenseNo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Article Title
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Peer-Review Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmergencyGreenContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = EmergencyGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Peer-Reviewed by ${article.reviewingBody}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = EmergencyGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${article.publishedDate} • ${article.readTimeMinutes} min read",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike, modifier = Modifier.size(36.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (article.likesCount > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (article.likesCount > 0) Color.Red else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text("${article.likesCount}", style = MaterialTheme.typography.labelSmall)

                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (article.isBookmarked) TealPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onReadMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("read_article_button_${article.id}"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Read Full Clinical Advice")
            }
        }
    }
}

@Composable
fun ArticleDetailDialog(
    article: MedicalArticle,
    onDismiss: () -> Unit,
    onSubmitClarification: (String) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(article.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${article.authorName} (${article.authorRole})",
                        style = MaterialTheme.typography.labelMedium,
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(14.dp))
                }
                Text("Council License: ${article.authorLicenseNo}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Key Takeaways Highlight Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "📋 Key Clinical Takeaways:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            article.keyTakeaways,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Full Content
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )

                HorizontalDivider()

                // Q&A / Clarification Box
                Text("Need clarification from a verified doctor?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    placeholder = { Text("Ask a question about this guidance...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            onSubmitClarification(questionText)
                            questionText = ""
                        }
                    },
                    enabled = questionText.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit Question", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun PublishArticleDialog(
    currentRole: UserRole,
    onDismiss: () -> Unit,
    onPublish: (String, String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var authorName by remember {
        mutableStateOf(if (currentRole == UserRole.DOCTOR) "Dr. Sarah Alabi, MD" else "Pharm. David Osei, B.Pharm")
    }
    var authorRole by remember {
        mutableStateOf(if (currentRole == UserRole.DOCTOR) "Consultant Physician" else "Clinical Specialist Pharmacist")
    }
    var licenseNumber by remember {
        mutableStateOf(if (currentRole == UserRole.DOCTOR) "MDCN/Reg/39012" else "PCN/R/55410")
    }
    var category by remember { mutableStateOf("Preventive Care") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var takeaways by remember { mutableStateOf("• Follow prescribing instructions.\n• Consult clinical professional for questions.") }

    val categories = listOf("Cardiology", "Pharmacology", "Pediatrics", "Preventive Care", "Maternal Health")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publish Verified Medical Advice")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Article Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = { Text("Author Name & Credentials") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = authorRole,
                        onValueChange = { authorRole = it },
                        label = { Text("Specialty") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("Council Lic #") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Text("Specialty Category:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Executive Clinical Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Full Clinical Advice & Evidence") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = takeaways,
                    onValueChange = { takeaways = it },
                    label = { Text("Key Bullet Takeaways") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onPublish(title, authorName, authorRole, licenseNumber, category, summary, content, takeaways)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.testTag("submit_publish_article_button")
            ) {
                Text("Publish to App")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

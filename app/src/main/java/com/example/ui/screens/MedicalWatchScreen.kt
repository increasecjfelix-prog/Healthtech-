package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicalVideo
import com.example.data.model.UserRole
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyGreenContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel
import kotlinx.coroutines.delay

@Composable
fun MedicalWatchSection(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentRole.collectAsStateWithLifecycle()
    val videos by viewModel.filteredVideos.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.videoCategoryFilter.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var activePlayingVideo by remember { mutableStateOf<MedicalVideo?>(null) }
    var showUploadModal by remember { mutableStateOf(false) }
    var showGateDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Cardiology", "Pharmacology", "Pediatrics", "Preventive Care", "Maternal Health")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Clinical Video Verification Banner & Upload Action
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = TealPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.OndemandVideo,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Verified Medical Watch Feed",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Peer-Reviewed Clinical Video Lectures & Demos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "All medical videos require certificate-backed validation. Only verified Doctors and Pharmacists are authorized to upload clinical guidance and medication tutorials to curb harmful internet medical misinformation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VerifiedBadgeBg
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Council Regulated",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerifiedBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Upload Video Button: Only Doctors and Pharmacists can upload
                        Button(
                            onClick = {
                                if (role == UserRole.DOCTOR || role == UserRole.PHARMACIST) {
                                    showUploadModal = true
                                } else {
                                    showGateDialog = true
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealPrimary
                            ),
                            modifier = Modifier.testTag("upload_video_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Video", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 2. Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { viewModel.videoCategoryFilter.value = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        leadingIcon = if (selectedCategory == cat) {
                            { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        modifier = Modifier.testTag("video_filter_$cat")
                    )
                }
            }
        }

        // 3. Videos Feed
        if (videos.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No verified videos in this category yet", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Doctors and pharmacists can upload the first clinical video for $selectedCategory.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(videos, key = { it.id }) { video ->
                MedicalVideoCard(
                    video = video,
                    onPlay = {
                        activePlayingVideo = video
                        viewModel.recordVideoView(video.id)
                    },
                    onToggleBookmark = { viewModel.toggleVideoBookmark(video) },
                    onLike = { viewModel.likeVideo(video.id) },
                    onShare = { viewModel.showMessage("Clinical video link copied: ${video.title}") }
                )
            }
        }
    }

    // Role-Gate Dialog for Patients / Non-Doctors trying to upload
    if (showGateDialog) {
        AlertDialog(
            onDismissRequest = { showGateDialog = false },
            icon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(36.dp))
            },
            title = { Text("Professional Upload Restricted") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Video publishing is strictly limited to verified Medical Doctors and Licensed Pharmacists with credentials validated by regulatory councils.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "This ensures public safety by ensuring health tutorials and clinical guidance are never disseminated by unverified accounts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            "💡 Pro Tip: You can switch to 'Doctor' or 'Pharmacist' role in the top header or Verification tab to experience medical video publishing.",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGateDialog = false
                        viewModel.setRole(UserRole.DOCTOR)
                        viewModel.showMessage("Role set to Medical Doctor. You can now upload clinical videos.")
                        showUploadModal = true
                    }
                ) {
                    Text("Switch to Doctor & Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGateDialog = false }) { Text("Dismiss") }
            }
        )
    }

    // Interactive Video Player Modal
    activePlayingVideo?.let { video ->
        MedicalVideoPlayerDialog(
            video = video,
            onDismiss = { activePlayingVideo = null },
            onLike = { viewModel.likeVideo(video.id) },
            onBookmark = { viewModel.toggleVideoBookmark(video) },
            onShare = { viewModel.showMessage("Sharing verified medical video lecture") }
        )
    }

    // Upload Video Modal (Only for Doctors & Pharmacists)
    if (showUploadModal) {
        UploadMedicalVideoDialog(
            currentRole = role,
            defaultAuthorName = currentUser?.fullName ?: (if (role == UserRole.DOCTOR) "Dr. Amina Alabi, MD" else "Pharm. David Osei, PharmD"),
            defaultLicense = if (role == UserRole.DOCTOR) "MDCN/Reg/39012" else "PCN/R/55410",
            defaultFacility = if (role == UserRole.DOCTOR) "Teaching Hospital & Specialist Clinic" else "CarePlus Community Pharmacy",
            onDismiss = { showUploadModal = false },
            onUpload = { title, desc, author, authorRole, lic, fac, cat, dur, url, takeaways, cme ->
                viewModel.uploadMedicalVideo(
                    title = title,
                    description = desc,
                    authorName = author,
                    authorRole = authorRole,
                    authorLicense = lic,
                    facilityName = fac,
                    category = cat,
                    durationString = dur,
                    videoUrl = url,
                    takeaways = takeaways,
                    cmeUnits = cme
                )
                showUploadModal = false
            }
        )
    }
}

/**
 * High-craft Video Card displaying credential badges, duration pill,
 * peer-review seal, and play trigger.
 */
@Composable
fun MedicalVideoCard(
    video: MedicalVideo,
    onPlay: () -> Unit,
    onToggleBookmark: () -> Unit,
    onLike: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_card_${video.id}")
    ) {
        Column {
            // Video Thumbnail & Player Poster Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF13222F),
                                Color(0xFF09141D)
                            )
                        )
                    )
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle medical iconography / motif
                Icon(
                    imageVector = Icons.Default.OndemandVideo,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.size(110.dp)
                )

                // Play Button Center Action
                Surface(
                    shape = CircleShape,
                    color = TealPrimary,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("play_button_${video.id}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Top Left: Category Badge & CME Points
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = video.category,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (video.accreditedCmeUnits > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmergencyGreen.copy(alpha = 0.85f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    "${video.accreditedCmeUnits} CME Credits",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom Right: Duration Pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = video.videoDuration,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Body Content
            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Verified Author Line
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.authorName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Professional",
                            tint = VerifiedBlue,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Text(
                        text = video.publishedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${video.authorRole} • Council Lic #${video.authorLicenseNo} (${video.facilityName})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Short Description
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Clinical Peer Review Tag
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmergencyGreenContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmergencyGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Audited: ${video.reviewingBoard}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmergencyGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                // Footer Stats & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = "Views", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${video.viewsCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onLike)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Likes",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${video.likesCount}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = if (video.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (video.isBookmarked) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Button(
                            onClick = onPlay,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Watch", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Comprehensive Interactive Video Player Dialog with Simulated Streaming,
 * Controls (Play/Pause, Scrubbing slider, Playback speed, Audio),
 * Key Clinical Takeaways, and Verified Credentials.
 */
@Composable
fun MedicalVideoPlayerDialog(
    video: MedicalVideo,
    onDismiss: () -> Unit,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentPlaybackSec by remember { mutableIntStateOf(0) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var userScrubbing by remember { mutableStateOf(false) }

    val totalSec = video.durationSeconds.coerceAtLeast(60)

    // Simulated playback loop when active
    LaunchedEffect(isPlaying, userScrubbing, playbackSpeed) {
        while (isPlaying && !userScrubbing && currentPlaybackSec < totalSec) {
            delay((1000 / playbackSpeed).toLong())
            currentPlaybackSec += 1
        }
        if (currentPlaybackSec >= totalSec) {
            isPlaying = false
        }
    }

    val formatTime: (Int) -> String = { seconds ->
        val m = seconds / 60
        val s = seconds % 60
        String.format("%02d:%02d", m, s)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_player_dialog"),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Video Canvas Screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    // Clinical Video Simulated Visual Feed
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.OndemandVideo else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPlaying) "Streaming Verified Lecture • ${playbackSpeed}x" else "Playback Paused",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = video.facilityName,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }

                    // Floating Watermark: Verified Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verified Source", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Player Overlay Controls at Bottom of video screen
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Progress Bar
                        Slider(
                            value = currentPlaybackSec.toFloat(),
                            onValueChange = {
                                userScrubbing = true
                                currentPlaybackSec = it.toInt()
                            },
                            onValueChangeFinished = {
                                userScrubbing = false
                            },
                            valueRange = 0f..totalSec.toFloat(),
                            modifier = Modifier.fillMaxWidth().height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (currentPlaybackSec >= totalSec) {
                                            currentPlaybackSec = 0
                                            isPlaying = true
                                        } else {
                                            isPlaying = !isPlaying
                                        }
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else if (currentPlaybackSec >= totalSec) Icons.Default.Replay else Icons.Default.PlayArrow,
                                        contentDescription = "Toggle Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    "${formatTime(currentPlaybackSec)} / ${formatTime(totalSec)}",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(
                                    onClick = {
                                        playbackSpeed = when (playbackSpeed) {
                                            1.0f -> 1.25f
                                            1.25f -> 1.5f
                                            1.5f -> 2.0f
                                            else -> 1.0f
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${playbackSpeed}x", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Title & Author Meta
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = video.authorName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(15.dp))
                        }
                        Text(
                            text = "${video.authorRole} • ${video.authorLicenseNo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = video.facilityName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (video.accreditedCmeUnits > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmergencyGreenContainer
                        ) {
                            Text(
                                text = "🏆 ${video.accreditedCmeUnits} CME Credits",
                                color = EmergencyGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Key Medical Takeaways / Clinical Evidence Box
                if (video.keyMedicalTakeaways.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Clinical Takeaways & Evidence",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.keyMedicalTakeaways,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Description
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Peer Review Audit Strip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VerifiedBadgeBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Peer-Reviewed Educational Video",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = VerifiedBlue
                            )
                            Text(
                                "Audited by ${video.reviewingBoard} for pharmacological and clinical accuracy.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close Watch")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onLike) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Like (${video.likesCount})")
            }
        }
    )
}

/**
 * Upload Medical Video Modal Dialog:
 * Strictly gated for Doctors and Pharmacists to enter clinical details,
 * council license verification, duration, takeaways, and CME points.
 */
@Composable
fun UploadMedicalVideoDialog(
    currentRole: UserRole,
    defaultAuthorName: String,
    defaultLicense: String,
    defaultFacility: String,
    onDismiss: () -> Unit,
    onUpload: (
        title: String,
        desc: String,
        author: String,
        authorRole: String,
        license: String,
        facility: String,
        category: String,
        duration: String,
        videoUrl: String,
        takeaways: String,
        cmeUnits: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf(defaultAuthorName) }
    var authorRole by remember { mutableStateOf(if (currentRole == UserRole.DOCTOR) "Medical Doctor" else "Licensed Pharmacist") }
    var licenseNumber by remember { mutableStateOf(defaultLicense) }
    var facilityName by remember { mutableStateOf(defaultFacility) }
    var category by remember { mutableStateOf("Cardiology") }
    var durationString by remember { mutableStateOf("04:30") }
    var takeaways by remember { mutableStateOf("") }
    var cmeUnits by remember { mutableIntStateOf(1) }
    var mockVideoUrl by remember { mutableStateOf("https://healthtech.demo/clinical_upload_preview.mp4") }

    val categories = listOf("Cardiology", "Pharmacology", "Pediatrics", "Preventive Care", "Maternal Health")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upload_video_dialog"),
        icon = {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(36.dp))
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Upload Medical Video", fontWeight = FontWeight.Bold)
                Text(
                    "Council-Verified Professional Submission",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Regulatory Notice
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VerifiedBadgeBg
                ) {
                    Text(
                        "🔒 Notice: All video uploads are stamped with your council license and undergo automated editorial review before public indexing.",
                        style = MaterialTheme.typography.labelSmall,
                        color = VerifiedBlue,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Video Title / Clinical Topic *") },
                    placeholder = { Text("e.g., Safe Pediatric Dosing Guidelines") },
                    modifier = Modifier.fillMaxWidth().testTag("video_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = { Text("Clinician / Pharmacist Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("video_author_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = authorRole,
                        onValueChange = { authorRole = it },
                        label = { Text("Specialty / Title") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("Council Lic # *") },
                        modifier = Modifier.weight(1f).testTag("video_license_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = facilityName,
                    onValueChange = { facilityName = it },
                    label = { Text("Hospital / Pharmacy Facility") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = durationString,
                        onValueChange = { durationString = it },
                        label = { Text("Duration (MM:SS)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = "$cmeUnits",
                        onValueChange = { cmeUnits = it.toIntOrNull() ?: 0 },
                        label = { Text("CME Credits") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Clinical Overview & Scope *") },
                    modifier = Modifier.fillMaxWidth().testTag("video_desc_input"),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = takeaways,
                    onValueChange = { takeaways = it },
                    label = { Text("Key Bullet Clinical Takeaways") },
                    placeholder = { Text("1. First-line dosage. 2. Contraindications...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )

                // Simulated File Upload Picker
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.OndemandVideo, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("clinical_recording_2026.mp4", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Attached • 1080p Full HD", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmergencyGreenContainer
                        ) {
                            Text("Ready", color = EmergencyGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onUpload(
                            title.trim(),
                            description.trim(),
                            authorName.trim(),
                            authorRole.trim(),
                            licenseNumber.trim(),
                            facilityName.trim(),
                            category,
                            durationString.trim(),
                            mockVideoUrl,
                            takeaways.trim(),
                            cmeUnits
                        )
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.testTag("submit_upload_video_button")
            ) {
                Text("Publish Video")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

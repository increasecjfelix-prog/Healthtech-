package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicationSchedule
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyGreenContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel

@Composable
fun MedicationScheduleSection(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.medicationSchedules.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<MedicationSchedule?>(null) }
    var scheduleToDelete by remember { mutableStateOf<MedicationSchedule?>(null) }

    val filterOptions = listOf("All", "Active", "Morning", "Afternoon", "Evening", "Night")

    val filteredSchedules = schedules.filter { sched ->
        when (selectedFilter) {
            "Active" -> sched.isActive
            "Morning" -> sched.timeOfDay.contains("Morning", ignoreCase = true)
            "Afternoon" -> sched.timeOfDay.contains("Afternoon", ignoreCase = true)
            "Evening" -> sched.timeOfDay.contains("Evening", ignoreCase = true)
            "Night" -> sched.timeOfDay.contains("Night", ignoreCase = true) || sched.timeOfDay.contains("Bed", ignoreCase = true)
            else -> true
        }
    }

    val totalActive = schedules.count { it.isActive }
    val takenToday = schedules.count { it.isActive && it.isTakenToday }
    val adherencePercent = if (totalActive > 0) (takenToday.toFloat() / totalActive.toFloat()) else 0f

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Dosing Summary & Adherence Tracker
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = TealPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Daily Medication Schedule",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Dosage adherence & timing regimen",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_schedule_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Schedule", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Adherence: $takenToday of $totalActive doses taken",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(adherencePercent * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { adherencePercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (adherencePercent == 1f) EmergencyGreen else TealPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        modifier = Modifier.testTag("filter_schedule_$filter")
                    )
                }
            }
        }

        // Schedule items
        if (filteredSchedules.isEmpty()) {
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
                        Icon(Icons.Default.Medication, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No medication schedules found", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Tap 'New Schedule' above to configure a medication reminder with dosage, frequency, and time of day.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredSchedules, key = { it.id }) { schedule ->
                MedicationScheduleCard(
                    schedule = schedule,
                    onToggleTaken = { viewModel.toggleDoseTaken(schedule.id, schedule.isTakenToday) },
                    onToggleActive = { viewModel.toggleScheduleActive(schedule.id, schedule.isActive) },
                    onEdit = { editingSchedule = schedule },
                    onDelete = { scheduleToDelete = schedule }
                )
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog || editingSchedule != null) {
        val target = editingSchedule
        AddEditMedicationScheduleDialog(
            existingSchedule = target,
            onDismiss = {
                showAddDialog = false
                editingSchedule = null
            },
            onSave = { id, name, dosage, freq, timeOfDay, instructions, start, end, reminder, doctor, notes ->
                viewModel.saveMedicationSchedule(
                    id = id,
                    medicationName = name,
                    dosage = dosage,
                    frequency = freq,
                    timeOfDay = timeOfDay,
                    instructions = instructions,
                    startDate = start,
                    endDate = end,
                    reminderEnabled = reminder,
                    prescribingDoctor = doctor,
                    notes = notes
                )
                showAddDialog = false
                editingSchedule = null
            }
        )
    }

    // Delete Confirmation Dialog
    scheduleToDelete?.let { schedule ->
        AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            title = { Text("Delete Medication Schedule") },
            text = { Text("Are you sure you want to remove ${schedule.medicationName} from your dosing schedule?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMedicationSchedule(schedule.id)
                        scheduleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { scheduleToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MedicationScheduleCard(
    schedule: MedicationSchedule,
    onToggleTaken: () -> Unit,
    onToggleActive: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (schedule.isTakenToday) EmergencyGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isTakenToday) EmergencyGreenContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_card_${schedule.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Medication Name + Dosage + Active Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = if (schedule.isTakenToday) EmergencyGreen.copy(alpha = 0.15f) else TealPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (schedule.isTakenToday) Icons.Default.CheckCircle else Icons.Default.Medication,
                                contentDescription = null,
                                tint = if (schedule.isTakenToday) EmergencyGreen else TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = schedule.medicationName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (schedule.isTakenToday) TextDecoration.None else TextDecoration.None
                        )
                        Text(
                            text = "Dose: ${schedule.dosage}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TealPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Taken Today quick toggle chip/checkbox
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (schedule.isTakenToday) EmergencyGreenContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable(onClick = onToggleTaken)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (schedule.isTakenToday) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = if (schedule.isTakenToday) EmergencyGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (schedule.isTakenToday) "Taken Today" else "Take Dose",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (schedule.isTakenToday) EmergencyGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Schema Badges: Frequency & Time of Day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Frequency Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.EventRepeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Frequency", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(schedule.frequency, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Time of Day Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Time of Day", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(schedule.timeOfDay, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Instructions (if present)
            if (schedule.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = schedule.instructions,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Prescribing Doctor & Active State Footer
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (schedule.reminderEnabled) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reminder On", fontSize = 10.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (schedule.prescribingDoctor.isNotBlank()) {
                        Text(
                            "Rx: ${schedule.prescribingDoctor}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleActive, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (schedule.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (schedule.isActive) "Pause Schedule" else "Resume Schedule",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Schedule", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Schedule", tint = EmergencyRed.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditMedicationScheduleDialog(
    existingSchedule: MedicationSchedule?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        name: String,
        dosage: String,
        frequency: String,
        timeOfDay: String,
        instructions: String,
        startDate: String,
        endDate: String,
        reminderEnabled: Boolean,
        prescribingDoctor: String,
        notes: String
    ) -> Unit
) {
    var medicationName by remember { mutableStateOf(existingSchedule?.medicationName ?: "") }
    var dosage by remember { mutableStateOf(existingSchedule?.dosage ?: "500 mg") }
    var frequency by remember { mutableStateOf(existingSchedule?.frequency ?: "Twice daily") }
    var timeOfDay by remember { mutableStateOf(existingSchedule?.timeOfDay ?: "Morning & Evening") }
    var instructions by remember { mutableStateOf(existingSchedule?.instructions ?: "") }
    var startDate by remember { mutableStateOf(existingSchedule?.startDate ?: "2026-09-16") }
    var endDate by remember { mutableStateOf(existingSchedule?.endDate ?: "Ongoing") }
    var reminderEnabled by remember { mutableStateOf(existingSchedule?.reminderEnabled ?: true) }
    var doctorName by remember { mutableStateOf(existingSchedule?.prescribingDoctor ?: "") }
    var notes by remember { mutableStateOf(existingSchedule?.notes ?: "") }

    val commonDosages = listOf("250 mg", "500 mg", "1000 mg", "10 mg", "20 mg", "1 Tablet", "5 ml")
    val commonFrequencies = listOf("Once daily", "Twice daily", "Three times daily", "Every 8 hours", "As needed")
    val commonTimes = listOf("Morning (Breakfast)", "Noon (Lunch)", "Evening (Dinner)", "Night (Bedtime)", "Morning & Evening")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_schedule_dialog"),
        icon = {
            Icon(Icons.Default.Schedule, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(32.dp))
        },
        title = {
            Text(
                if (existingSchedule == null) "Add Medication Schedule" else "Edit Medication Schedule",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Medication Name Field
                OutlinedTextField(
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    label = { Text("Medication Name *") },
                    placeholder = { Text("e.g., Amoxicillin, Lisinopril") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("schedule_name_input")
                )

                // Dosage Field + Quick suggestions
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage *") },
                    placeholder = { Text("e.g., 500 mg, 1 tablet, 10 ml") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("schedule_dosage_input")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(commonDosages) { d ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (dosage == d) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { dosage = d }
                        ) {
                            Text(
                                text = d,
                                color = if (dosage == d) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Frequency Field + Quick suggestions
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency *") },
                    placeholder = { Text("e.g., Once daily, Twice daily, Every 8 hours") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("schedule_frequency_input")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(commonFrequencies) { f ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (frequency == f) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { frequency = f }
                        ) {
                            Text(
                                text = f,
                                color = if (frequency == f) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Time of Day Field + Quick suggestions
                OutlinedTextField(
                    value = timeOfDay,
                    onValueChange = { timeOfDay = it },
                    label = { Text("Time of Day *") },
                    placeholder = { Text("e.g., Morning (08:00 AM), Night (Bedtime)") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("schedule_time_input")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(commonTimes) { t ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (timeOfDay == t) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { timeOfDay = t }
                        ) {
                            Text(
                                text = t,
                                color = if (timeOfDay == t) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Clinical Instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions / With Food?") },
                    placeholder = { Text("e.g., Take with food or full glass of water") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("schedule_instructions_input")
                )

                // Prescribing Doctor
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Prescribing Clinician / Doctor") },
                    placeholder = { Text("e.g., Dr. Amina Alabi") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Reminder Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Enable Dose Reminder", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Receive notifications at designated times", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        existingSchedule?.id,
                        medicationName,
                        dosage,
                        frequency,
                        timeOfDay,
                        instructions,
                        startDate,
                        endDate,
                        reminderEnabled,
                        doctorName,
                        notes
                    )
                },
                enabled = medicationName.isNotBlank()
            ) {
                Text(if (existingSchedule == null) "Add Schedule" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

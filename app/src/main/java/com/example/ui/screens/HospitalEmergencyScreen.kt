package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EmergencyStatusOption
import com.example.data.model.HospitalProfile
import com.example.data.model.UserRole
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HospitalEmergencyScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val role by viewModel.currentRole.collectAsStateWithLifecycle()
    val hospitals by viewModel.hospitals.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFacilityType by remember { mutableStateOf("All") }
    var editingHospital by remember { mutableStateOf<HospitalProfile?>(null) }
    var triageHospital by remember { mutableStateOf<HospitalProfile?>(null) }

    val canManageFacility = role == UserRole.HOSPITAL || role == UserRole.HEALTHCARE_FACILITY || role == UserRole.ADMIN

    fun dialNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            viewModel.showMessage("Dialing $phoneNumber")
        }
    }

    val facilityTypes = listOf("All", "Hospital", "Other Healthcare Facility", "Diagnostic Center", "Maternity & Child Clinic")

    val filteredHospitals = remember(hospitals, searchQuery, selectedFacilityType) {
        hospitals.filter { hospital ->
            val matchesQuery = searchQuery.isBlank() ||
                    hospital.name.contains(searchQuery, ignoreCase = true) ||
                    hospital.servicesOffered.contains(searchQuery, ignoreCase = true) ||
                    hospital.address.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedFacilityType == "All" ||
                    hospital.facilityType.contains(selectedFacilityType, ignoreCase = true)

            matchesQuery && matchesType
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-Priority Emergency SOS Action Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
                border = BorderStroke(1.5.dp, EmergencyRed)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(EmergencyRed, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "EMERGENCY HOTLINE & AMBULANCE DISPATCH",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = EmergencyRed
                                )
                                Text(
                                    "Immediate Medical Triage & Resuscitation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "In life-threatening emergencies (cardiac arrest, severe trauma, stroke, breathing failure), connect immediately to city dispatch or alert the closest hospital ER trauma team.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { dialNumber("911") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sos_call_911_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call 911 / SOS")
                        }

                        OutlinedButton(
                            onClick = {
                                val closest = hospitals.minByOrNull { it.distanceKm }
                                if (closest != null) {
                                    triageHospital = closest
                                } else {
                                    viewModel.showMessage("No nearby hospital found.")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("alert_er_triage_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                            border = BorderStroke(1.dp, EmergencyRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alert ER Triage")
                        }
                    }
                }
            }
        }

        // Critical Regulatory Notice (Mandatory Rule)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmergencyAmber.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, EmergencyAmber)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyAmber, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "IMPORTANT SAFETY NOTICE: Do not represent emergency availability as guaranteed unless the facility has recently confirmed its live status. In extreme critical conditions, proceed directly to the nearest tertiary facility or contact emergency dispatch.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Search & Filters
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search hospitals, emergency services, clinics...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_hospitals_input")
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(facilityTypes) { type ->
                    FilterChip(
                        selected = selectedFacilityType == type,
                        onClick = { selectedFacilityType = type },
                        label = { Text(type, fontSize = 12.sp) }
                    )
                }
            }
        }

        // List of Hospitals
        items(filteredHospitals, key = { it.id }) { hospital ->
            HospitalProfileCard(
                hospital = hospital,
                canEditStatus = canManageFacility,
                onCallEmergency = { dialNumber(hospital.emergencyPhone) },
                onCallAmbulance = { dialNumber(hospital.ambulancePhone) },
                onAlertTriage = { triageHospital = hospital },
                onEditStatus = { editingHospital = hospital },
                onTogglePreferred = {
                    viewModel.togglePreferredFacility(
                        facilityId = hospital.id,
                        facilityName = hospital.name,
                        facilityType = hospital.facilityType,
                        address = hospital.address,
                        phone = hospital.emergencyPhone
                    )
                }
            )
        }
    }

    // Triage Pre-Notification Dialog
    triageHospital?.let { hospital ->
        EmergencyTriageDialog(
            hospital = hospital,
            onDismiss = { triageHospital = null },
            onSubmitTriage = { condition, etaMinutes, patientNote ->
                viewModel.showMessage("ALERT TRANSMITTED: ${hospital.name} ER has pre-notified trauma team for $condition. ETA: $etaMinutes mins.")
                triageHospital = null
            }
        )
    }

    // Hospital Live Status Updater Dialog
    editingHospital?.let { hospital ->
        EditHospitalStatusDialog(
            hospital = hospital,
            onDismiss = { editingHospital = null },
            onSave = { status, icuBeds, erWait ->
                viewModel.updateHospitalEmergency(hospital.id, status, icuBeds, erWait)
                editingHospital = null
            }
        )
    }
}

@Composable
fun HospitalProfileCard(
    hospital: HospitalProfile,
    canEditStatus: Boolean,
    onCallEmergency: () -> Unit,
    onCallAmbulance: () -> Unit,
    onAlertTriage: () -> Unit,
    onEditStatus: () -> Unit,
    onTogglePreferred: () -> Unit = {}
) {
    var isPreferred by remember { mutableStateOf(false) }
    val timeAgoString = remember(hospital.emergencyStatusUpdatedAt) {
        val diffMins = ((System.currentTimeMillis() - hospital.emergencyStatusUpdatedAt) / 60000).toInt()
        when {
            diffMins <= 1 -> "Confirmed just now"
            diffMins < 60 -> "Confirmed $diffMins mins ago"
            else -> "Last confirmed ${diffMins / 60}h ago"
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hospital_card_${hospital.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name, Type, and Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            hospital.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "${hospital.facilityType} • 📍 ${hospital.distanceKm} km away",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        hospital.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            isPreferred = !isPreferred
                            onTogglePreferred()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPreferred) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Preferred Facility",
                            tint = if (isPreferred) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    FacilityVerifiedBadge(label = if (hospital.isVerified) "Verified Facility" else "Unaccredited")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time ER Status Badge & Freshness Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    EmergencyStatusPill(
                        status = hospital.emergencyStatus,
                        erWaitMinutes = hospital.erWaitMinutes
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = timeAgoString,
                            fontSize = 11.sp,
                            color = if (timeAgoString.contains("just now") || timeAgoString.contains("mins")) EmergencyGreen else EmergencyAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (canEditStatus) {
                    Button(
                        onClick = onEditStatus,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.testTag("update_live_status_${hospital.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update ER Status", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Resource Dashboard Grid
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hotel, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Available ICU Beds:", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            "${hospital.availableIcuBeds} of ${hospital.totalIcuBeds} Open",
                            fontWeight = FontWeight.Bold,
                            color = if (hospital.availableIcuBeds > 0) EmergencyGreen else EmergencyRed,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Critical Trauma Bays:", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            "${hospital.traumaBaysReady} Resuscitation Bays Ready",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bloodtype, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Blood Bank Stock:", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            hospital.bloodBankStock,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Air, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Oxygen Supply:", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            if (hospital.oxygenPlantOperational) "Operational (100% Purity)" else "Backup Cylinders Ready",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmergencyGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Key Emergency Services:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                hospital.servicesOffered,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCallEmergency,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_hospital_er_${hospital.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call ER Desk")
                }

                OutlinedButton(
                    onClick = onAlertTriage,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("triage_alert_${hospital.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.NotificationImportant, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pre-Alert ER")
                }
            }
        }
    }
}

@Composable
fun FacilityVerifiedBadge(label: String) {
    Surface(
        color = VerifiedBadgeBg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = VerifiedBlue,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = VerifiedBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmergencyStatusPill(status: String, erWaitMinutes: Int) {
    val (bgColor, textColor, label) = when (status) {
        "OPEN", "ACCEPTING_PATIENTS" -> Triple(EmergencyGreen.copy(alpha = 0.15f), EmergencyGreen, "Accepting Emergency Patients")
        "CLOSED", "NOT_ACCEPTING_PATIENTS" -> Triple(EmergencyRed.copy(alpha = 0.15f), EmergencyRed, "Not Accepting Patients")
        "TEMPORARILY_UNAVAILABLE" -> Triple(EmergencyAmber.copy(alpha = 0.15f), EmergencyAmber, "Temporarily Unavailable")
        "CRITICAL" -> Triple(EmergencyRed.copy(alpha = 0.15f), EmergencyRed, "Critical Capacity • Diversion Advised")
        "BUSY" -> Triple(EmergencyAmber.copy(alpha = 0.15f), EmergencyAmber, "High Volume • Wait: $erWaitMinutes min")
        else -> Triple(EmergencyGreen.copy(alpha = 0.15f), EmergencyGreen, "ER Open • Wait: $erWaitMinutes min")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(textColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmergencyTriageDialog(
    hospital: HospitalProfile,
    onDismiss: () -> Unit,
    onSubmitTriage: (String, Int, String) -> Unit
) {
    var selectedCondition by remember { mutableStateOf("Acute Chest Pain / Suspected Cardiac") }
    var etaMinutes by remember { mutableIntStateOf(10) }
    var notes by remember { mutableStateOf("Patient conscious, severe crushing chest discomfort.") }

    val conditions = listOf(
        "Acute Chest Pain / Suspected Cardiac",
        "Severe Trauma / Accident",
        "Pediatric Respiratory Distress",
        "Suspected Stroke (FAST Positive)",
        "Severe Allergic Anaphylaxis",
        "Obstetric / Labor Emergency"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NotificationImportant, contentDescription = null, tint = EmergencyRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pre-Alert Hospital ER Triage")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Send an incoming patient triage notice directly to ${hospital.name} emergency resuscitation team so trauma doctors and equipment can be staged prior to arrival.",
                    style = MaterialTheme.typography.bodySmall
                )

                Text("Patient Clinical Condition:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Column {
                    conditions.take(4).forEach { cond ->
                        FilterChip(
                            selected = selectedCondition == cond,
                            onClick = { selectedCondition = cond },
                            label = { Text(cond, fontSize = 11.sp) },
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Estimated Arrival (ETA):", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (etaMinutes > 2) etaMinutes -= 2 }) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        Text("$etaMinutes mins", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { etaMinutes += 5 }) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Quick Patient Notes / Vitals") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitTriage(selectedCondition, etaMinutes, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                modifier = Modifier.testTag("confirm_triage_alert_button")
            ) {
                Text("Dispatch ER Alert Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditHospitalStatusDialog(
    hospital: HospitalProfile,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int) -> Unit
) {
    var status by remember { mutableStateOf(hospital.emergencyStatus) }
    var availableIcu by remember { mutableIntStateOf(hospital.availableIcuBeds) }
    var waitMinutes by remember { mutableIntStateOf(hospital.erWaitMinutes) }

    // Exactly matching specification:
    // Open, Closed, Temporarily unavailable, Accepting emergency patients, Not accepting emergency patients
    val statusOptions = listOf(
        "ACCEPTING_PATIENTS" to "Accepting emergency patients",
        "OPEN" to "Open",
        "BUSY" to "High Capacity",
        "NOT_ACCEPTING_PATIENTS" to "Not accepting patients",
        "TEMPORARILY_UNAVAILABLE" to "Temporarily unavailable",
        "CLOSED" to "Closed"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Update ER Emergency Department Status")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Emergency Desk for: ${hospital.name}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text("Emergency Department Status (Required):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    statusOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = status == key,
                            onClick = { status = key },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Available ICU Beds (Max: ${hospital.totalIcuBeds}):")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (availableIcu > 0) availableIcu-- }) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        Text("$availableIcu", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (availableIcu < hospital.totalIcuBeds) availableIcu++ }) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Average ER Wait Time:")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (waitMinutes >= 5) waitMinutes -= 5 }) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        Text("$waitMinutes min", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { waitMinutes += 5 }) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(status, availableIcu, waitMinutes) },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("save_er_status_button")
            ) {
                Text("Confirm & Record Timestamp")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

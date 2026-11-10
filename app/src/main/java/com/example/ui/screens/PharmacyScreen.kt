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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.components.DatabaseAndApiExplorerDialog
import com.example.ui.theme.BrandSky
import com.example.ui.theme.MedCardBg
import com.example.ui.theme.StockAmber
import com.example.ui.theme.StockGreen
import com.example.ui.theme.TextDarkSlate
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeText
import com.example.ui.viewmodel.PharmacyMedicationSearchResult
import com.example.data.model.MedicationItem
import com.example.data.model.PharmacyOutlet
import com.example.data.model.PosSaleRecord
import com.example.data.model.UserRole
import com.example.ui.components.FacilityVerifiedBadge
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.HealthTechViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val role by viewModel.currentRole.collectAsStateWithLifecycle()
    val medications by viewModel.filteredMedications.collectAsStateWithLifecycle()
    val allMeds by viewModel.allMedications.collectAsStateWithLifecycle()
    val pharmacies by viewModel.pharmacies.collectAsStateWithLifecycle()
    val posSales by viewModel.posSales.collectAsStateWithLifecycle()
    val posCart by viewModel.posCart.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchRadius by viewModel.searchRadiusKm.collectAsStateWithLifecycle()
    val nearbyResults by viewModel.nearbySearchResults.collectAsStateWithLifecycle()
    val rxOnly by viewModel.rxOnlyFilter.collectAsStateWithLifecycle()
    val inStockOnly by viewModel.inStockOnlyFilter.collectAsStateWithLifecycle()

    var selectedModeTab by remember { mutableIntStateOf(if (role == UserRole.PHARMACIST) 1 else 0) }

    // Dialog States
    var showAddMedDialog by remember { mutableStateOf(false) }
    var showPosRegisterDialog by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf<PosSaleRecord?>(null) }
    var showPrescriptionMatcherDialog by remember { mutableStateOf(false) }
    var showDbExplorerDialog by remember { mutableStateOf(false) }
    var reservingMedication by remember { mutableStateOf<MedicationItem?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Mode Selector: Patient Search vs Pharmacy Inventory & POS
        SecondaryTabRow(
            selectedTabIndex = selectedModeTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedModeTab == 0,
                onClick = { selectedModeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Medications")
                    }
                },
                modifier = Modifier.testTag("tab_find_medications")
            )
            Tab(
                selected = selectedModeTab == 1,
                onClick = { selectedModeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pharmacy POS & Stock")
                    }
                },
                modifier = Modifier.testTag("tab_pharmacy_pos")
            )
            Tab(
                selected = selectedModeTab == 2,
                onClick = { selectedModeTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dose Schedules")
                    }
                },
                modifier = Modifier.testTag("tab_medication_schedules")
            )
        }

        when (selectedModeTab) {
            0 -> {
                // PATIENT VIEW: Prescription & Neighborhood Medicine Locator (PostGIS + React Native Design)
                PatientMedicineLocator(
                    searchQuery = searchQuery,
                    onQueryChange = { viewModel.searchQuery.value = it },
                    radiusKm = searchRadius,
                    onRadiusChange = { viewModel.setRadiusKm(it) },
                    rxOnly = rxOnly,
                    onRxToggle = { viewModel.rxOnlyFilter.value = !rxOnly },
                    inStockOnly = inStockOnly,
                    onStockToggle = { viewModel.inStockOnlyFilter.value = !inStockOnly },
                    searchResults = nearbyResults,
                    onReserve = { reservingMedication = it },
                    onOpenPrescriptionMatcher = { showPrescriptionMatcherDialog = true },
                    onOpenDbExplorer = { showDbExplorerDialog = true }
                )
            }
            1 -> {
                // PHARMACY OUTLET VIEW: Inventory Management & POS Tools
                PharmacistPortalView(
                    role = role,
                    pharmacies = pharmacies,
                    medications = allMeds,
                    posSales = posSales,
                    posCartCount = posCart.values.sumOf { it.quantity },
                    onAddMedication = { showAddMedDialog = true },
                    onOpenPos = { showPosRegisterDialog = true },
                    onDeleteMed = { viewModel.deleteMedication(it.id) },
                    onViewReceipt = { showReceiptDialog = it },
                    onAddToCart = { viewModel.addToPosCart(it) }
                )
            }
            else -> {
                // PATIENT MEDICATION SCHEDULES (Dosing schema, frequency, time of day)
                MedicationScheduleSection(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Reservation Dialog
    reservingMedication?.let { med ->
        val pharmacy = pharmacies.find { it.id == med.pharmacyId }
        AlertDialog(
            onDismissRequest = { reservingMedication = null },
            title = {
                Text("Reserve Medication for Pickup")
            },
            text = {
                Column {
                    Text(
                        "${med.brandName} (${med.name})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pickup at: ${pharmacy?.name ?: "Neighborhood Pharmacy"}")
                    Text("Address: ${pharmacy?.address ?: "Near City Center"}")
                    Text("Est. Price: $${String.format("%.2f", med.price)}")
                    if (med.requiresPrescription) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                "⚠️ Prescription Required: You must present a valid medical prescription upon pharmacy collection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Your reservation will hold this medicine in stock for 4 hours. Present your reservation code at the counter.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val code = "RX-RES-${(1000..9999).random()}"
                        viewModel.showMessage("Reservation confirmed! Code: $code held at ${pharmacy?.name}")
                        reservingMedication = null
                    },
                    modifier = Modifier.testTag("confirm_reservation_button")
                ) {
                    Text("Confirm Reservation")
                }
            },
            dismissButton = {
                TextButton(onClick = { reservingMedication = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Prescription Matcher Dialog
    if (showPrescriptionMatcherDialog) {
        PrescriptionMatcherDialog(
            pharmacies = pharmacies,
            allMeds = allMeds,
            onDismiss = { showPrescriptionMatcherDialog = false },
            onReserveAll = { pharmacy, matchedMeds ->
                showPrescriptionMatcherDialog = false
                viewModel.showMessage("Prescription reserved at ${pharmacy.name} (${matchedMeds.size} items in stock)!")
            }
        )
    }

    // Add Medication to Inventory Dialog
    if (showAddMedDialog) {
        AddMedicationDialog(
            pharmacies = pharmacies,
            onDismiss = { showAddMedDialog = false },
            onAdd = { newMed ->
                viewModel.addMedication(newMed)
                showAddMedDialog = false
            }
        )
    }

    // POS Register Checkout Dialog
    if (showPosRegisterDialog) {
        PosRegisterDialog(
            cart = posCart,
            onUpdateQty = { id, qty -> viewModel.updatePosCartQuantity(id, qty) },
            onCheckout = { patient, rxNo, payment, pharmName, lic ->
                viewModel.checkoutPosSale(patient, rxNo, payment, pharmName, lic)
                showPosRegisterDialog = false
            },
            onDismiss = { showPosRegisterDialog = false }
        )
    }

    // Receipt Dialog
    showReceiptDialog?.let { receipt ->
        ReceiptDetailsDialog(
            receipt = receipt,
            onDismiss = { showReceiptDialog = null }
        )
    }

    // Database & API Explorer (PostgreSQL + PostGIS, REST, GraphQL)
    if (showDbExplorerDialog) {
        DatabaseAndApiExplorerDialog(
            viewModel = viewModel,
            onDismiss = { showDbExplorerDialog = false }
        )
    }
}

@Composable
fun PatientMedicineLocator(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    radiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    rxOnly: Boolean,
    onRxToggle: () -> Unit,
    inStockOnly: Boolean,
    onStockToggle: () -> Unit,
    searchResults: List<PharmacyMedicationSearchResult>,
    onReserve: (MedicationItem) -> Unit,
    onOpenPrescriptionMatcher: () -> Unit,
    onOpenDbExplorer: () -> Unit
) {
    val context = LocalContext.current
    var showSqlBanner by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen Header (Matching React Native: "Locate Medication", "Real-time pharmacy stock within your neighborhood")
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Locate Medication",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSlate
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Real-time pharmacy stock within your neighborhood",
                            fontSize = 13.sp,
                            color = TextMutedSlate
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenDbExplorer,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, BrandSky),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_open_db_explorer")
                    ) {
                        Icon(
                            Icons.Default.Terminal,
                            contentDescription = null,
                            tint = BrandSky,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("API & SQL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandSky)
                    }
                }
            }
        }

        // PostGIS Geolocation Info & Radius Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BrandSky,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PostGIS Spatial Engine (5.483°N, 7.033°E)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandSky
                            )
                        }

                        TextButton(
                            onClick = { showSqlBanner = !showSqlBanner },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(if (showSqlBanner) "Hide SQL" else "View SQL", fontSize = 11.sp, color = BrandSky)
                        }
                    }

                    if (showSqlBanner) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                "WHERE ST_DWithin(p.location, ST_MakePoint(7.033, 5.483)::geography, ${(radiusKm * 1000).toInt()})\nORDER BY ST_Distance(p.location, ST_MakePoint(7.033, 5.483)::geography) ASC",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF38BDF8),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Radius Filter Pills
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Search Radius:", style = MaterialTheme.typography.labelSmall, color = TextMutedSlate)
                        listOf(2.0, 5.0, 10.0, 25.0).forEach { km ->
                            val isSelected = radiusKm == km
                            FilterChip(
                                selected = isSelected,
                                onClick = { onRadiusChange(km) },
                                label = {
                                    Text(
                                        "${km.toInt()} km",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Input (matching React Native: TextInput placeholder "Search drug name (e.g., Amoxicillin, Paracetamol)")
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_medication_input"),
                placeholder = {
                    Text(
                        "Search drug name (e.g., Amoxicillin, Paracetamol)",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandSky) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Quick Filter Chips + Multi-Drug Matcher
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = inStockOnly,
                    onClick = onStockToggle,
                    label = { Text("In Stock Only", fontSize = 12.sp) },
                    leadingIcon = if (inStockOnly) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )
                FilterChip(
                    selected = rxOnly,
                    onClick = onRxToggle,
                    label = { Text("Prescription (Rx)", fontSize = 12.sp) },
                    leadingIcon = if (rxOnly) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onOpenPrescriptionMatcher,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("prescription_matcher_button")
                ) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = BrandSky
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Multi-Drug", fontSize = 12.sp, color = BrandSky)
                }
            }
        }

        // Count Header
        item {
            Text(
                text = "${searchResults.size} Outlets with '${searchQuery.ifBlank { "All Drugs" }}' in Stock",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkSlate
            )
        }

        // Medication Cards adhering strictly to React Native reference
        items(searchResults, key = { "${it.pharmacy.id}_${it.medication.id}" }) { result ->
            PharmacySearchResultCard(
                result = result,
                onReserve = { onReserve(result.medication) },
                onCall = { phone ->
                    try {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        context.startActivity(dialIntent)
                    } catch (e: Exception) {
                        // handled gracefully
                    }
                }
            )
        }

        if (searchResults.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No pharmacies found within ${radiusKm.toInt()} km with this medication in stock.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(onClick = { onRadiusChange(25.0) }) {
                        Text("Expand Search Radius to 25 km")
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacySearchResultCard(
    result: PharmacyMedicationSearchResult,
    onReserve: () -> Unit,
    onCall: (String) -> Unit
) {
    val pharmacy = result.pharmacy
    val medication = result.medication
    val isLowStock = medication.stockQuantity in 1..5

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pharmacy_card_${pharmacy.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Pharmacy Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = pharmacy.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSlate
                        )
                        if (pharmacy.isVerified) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = VerifiedBadgeBg
                            ) {
                                Text(
                                    text = "✓ Verified",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerifiedBadgeText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = pharmacy.address,
                        fontSize = 13.sp,
                        color = TextMutedSlate
                    )
                }

                Text(
                    text = "${String.format("%.1f", result.distanceKm)} km",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandSky
                )
            }

            // Divider matching #F1F5F9
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = Color(0xFFF1F5F9)
            )

            // Medication Details Box (bg: #F8FAFC)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MedCardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = medication.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Dosage: ${medication.strength.ifBlank { medication.dosageForm }}",
                            fontSize = 12.sp,
                            color = TextMutedSlate
                        )
                    }

                    val priceText = medication.priceFormatted.ifBlank {
                        "₦" + String.format("%,.0f", medication.price)
                    }
                    Text(
                        text = priceText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSlate
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Footer: Stock indicator on left, Call Outlet and Reserve buttons on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = when {
                                    medication.stockQuantity <= 0 -> Color(0xFFDC2626)
                                    isLowStock -> StockAmber
                                    else -> Color(0xFF10B981)
                                },
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = when {
                            medication.stockQuantity <= 0 -> "Out of stock"
                            isLowStock -> "Only ${medication.stockQuantity} left in stock"
                            else -> "${medication.stockQuantity} items in stock"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            medication.stockQuantity <= 0 -> Color(0xFFDC2626)
                            isLowStock -> StockAmber
                            else -> StockGreen
                        }
                    )
                }

                // Action Buttons: Call Outlet & Reserve
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onReserve,
                        enabled = medication.stockQuantity > 0,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("reserve_med_${medication.id}")
                    ) {
                        Text("Hold", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onCall(pharmacy.phone) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSky),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("call_outlet_button_${pharmacy.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Call Outlet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationCard(
    medication: MedicationItem,
    pharmacy: PharmacyOutlet?,
    onReserve: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("medication_card_${medication.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.brandName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${medication.dosageForm} • ${medication.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = medication.priceFormatted.ifBlank { "₦" + String.format("%,.0f", medication.price) },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (medication.requiresPrescription) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = "Rx Required",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "OTC",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)

            // Pharmacy Outlet Location & Stock Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalPharmacy,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pharmacy?.name ?: "Certified Pharmacy",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "📍 ${pharmacy?.distanceKm ?: 0.8} km away • ${if (pharmacy?.isOpen24Hours == true) "Open 24/7" else "Closes 10 PM"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stock status indicator
                val stockText = when {
                    medication.stockQuantity <= 0 -> "Out of Stock"
                    medication.stockQuantity < 10 -> "Only ${medication.stockQuantity} left"
                    else -> "${medication.stockQuantity} in stock"
                }
                val stockColor = when {
                    medication.stockQuantity <= 0 -> EmergencyRed
                    medication.stockQuantity < 10 -> EmergencyAmber
                    else -> EmergencyGreen
                }

                Text(
                    text = stockText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = stockColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onReserve,
                enabled = medication.stockQuantity > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reserve_medication_button_${medication.id}"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reserve at This Pharmacy")
            }
        }
    }
}

@Composable
fun PharmacistPortalView(
    role: UserRole,
    pharmacies: List<PharmacyOutlet>,
    medications: List<MedicationItem>,
    posSales: List<PosSaleRecord>,
    posCartCount: Int,
    onAddMedication: () -> Unit,
    onOpenPos: () -> Unit,
    onDeleteMed: (MedicationItem) -> Unit,
    onViewReceipt: (PosSaleRecord) -> Unit,
    onAddToCart: (MedicationItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Outlet Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "CarePlus Certified Pharmacy Outlet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        FacilityVerifiedBadge(label = "Reg #PCN-8472")
                    }
                    Text(
                        "Active Pharmacist POS & Local Inventory Management Suite",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenPos,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_pos_register_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (posCartCount > 0) "POS Cart ($posCartCount)" else "POS Terminal")
                        }

                        OutlinedButton(
                            onClick = onAddMedication,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_medication_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Add Stock")
                        }
                    }
                }
            }
        }

        // Inventory Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Current Pharmacy Inventory (${medications.size} SKUs)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Inventory Items
        items(medications, key = { it.id }) { med ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                med.brandName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "${med.name} • Batch: ${med.batchNumber} • Exp: ${med.expiryDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "$${String.format("%.2f", med.price)}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Qty in Stock: ${med.stockQuantity}",
                            fontWeight = FontWeight.SemiBold,
                            color = if (med.stockQuantity > 5) EmergencyGreen else EmergencyRed,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { onAddToCart(med) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("pos_add_item_${med.id}")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to POS", fontSize = 12.sp)
                            }

                            IconButton(onClick = { onDeleteMed(med) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete item",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent POS Sales Record Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Recent POS Invoices & Dispensing Records",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (posSales.isEmpty()) {
            item {
                Text(
                    "No sales recorded yet. Use the POS Terminal above to ring up transactions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(posSales) { sale ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Receipt #${sale.id} • ${sale.patientName}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "${sale.itemsSummary} (${sale.paymentMethod})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Pharmacist: ${sale.pharmacistName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TealPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "$${String.format("%.2f", sale.totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { onViewReceipt(sale) }) {
                                Text("View Receipt", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrescriptionMatcherDialog(
    pharmacies: List<PharmacyOutlet>,
    allMeds: List<MedicationItem>,
    onDismiss: () -> Unit,
    onReserveAll: (PharmacyOutlet, List<MedicationItem>) -> Unit
) {
    var prescriptionInput by remember { mutableStateOf("Augmentin, Ventolin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Neighborhood Prescription Matcher")
        },
        text = {
            Column {
                Text(
                    "Enter the medicines written on your prescription to find which neighborhood pharmacy has ALL of them in stock:",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = prescriptionInput,
                    onValueChange = { prescriptionInput = it },
                    placeholder = { Text("e.g., Augmentin, Ventolin, Metformin") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                val items = prescriptionInput.split(",").map { it.trim() }.filter { it.isNotBlank() }

                Text("Available Matches:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))

                pharmacies.forEach { pharm ->
                    val medsAtPharm = allMeds.filter { it.pharmacyId == pharm.id && it.stockQuantity > 0 }
                    val matches = items.filter { query ->
                        medsAtPharm.any { it.name.contains(query, ignoreCase = true) || it.brandName.contains(query, ignoreCase = true) }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (matches.size == items.size) EmergencyGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pharm.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "${matches.size} of ${items.size} meds in stock • ${pharm.distanceKm} km away",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (matches.size == items.size) EmergencyGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (matches.isNotEmpty()) {
                                Button(
                                    onClick = { onReserveAll(pharm, medsAtPharm) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Reserve", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AddMedicationDialog(
    pharmacies: List<PharmacyOutlet>,
    onDismiss: () -> Unit,
    onAdd: (MedicationItem) -> Unit
) {
    var brandName by remember { mutableStateOf("") }
    var genericName by remember { mutableStateOf("") }
    var dosageForm by remember { mutableStateOf("Tablet (30s)") }
    var category by remember { mutableStateOf("Cardiovascular") }
    var priceStr by remember { mutableStateOf("15.00") }
    var stockStr by remember { mutableStateOf("50") }
    var requiresPrescription by remember { mutableStateOf(true) }
    var indication by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication to Inventory") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Brand Name (e.g., Lipitor 20mg)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = genericName,
                    onValueChange = { genericName = it },
                    label = { Text("Generic Active Ingredient (e.g., Atorvastatin)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price ($)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("Initial Stock") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                OutlinedTextField(
                    value = dosageForm,
                    onValueChange = { dosageForm = it },
                    label = { Text("Dosage Form / Packaging") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = indication,
                    onValueChange = { indication = it },
                    label = { Text("Clinical Indication / Usage") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = requiresPrescription,
                        onClick = { requiresPrescription = !requiresPrescription },
                        label = { Text(if (requiresPrescription) "Rx Required (Prescription)" else "Over the Counter (OTC)") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (brandName.isNotBlank() && genericName.isNotBlank()) {
                        val med = MedicationItem(
                            id = "med_${UUID.randomUUID().toString().take(6)}",
                            pharmacyId = "pharm_1",
                            name = genericName,
                            brandName = brandName,
                            dosageForm = dosageForm,
                            category = category,
                            batchNumber = "BATCH-${(1000..9999).random()}",
                            expiryDate = "10/2028",
                            price = priceStr.toDoubleOrNull() ?: 10.0,
                            stockQuantity = stockStr.toIntOrNull() ?: 20,
                            requiresPrescription = requiresPrescription,
                            indication = indication.ifBlank { "Clinical indication specified by prescribing physician." }
                        )
                        onAdd(med)
                    }
                },
                enabled = brandName.isNotBlank() && genericName.isNotBlank()
            ) {
                Text("Save to Inventory")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PosRegisterDialog(
    cart: Map<String, com.example.ui.viewmodel.PosCartItem>,
    onUpdateQty: (String, Int) -> Unit,
    onCheckout: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var patientName by remember { mutableStateOf("John Doe") }
    var prescriptionNo by remember { mutableStateOf("RX-2026-9912") }
    var paymentMethod by remember { mutableStateOf("Card / POS") }
    val pharmacistName = "Pharm. David Osei"
    val pharmacistLicense = "PCN/R/55410"

    val subtotal = cart.values.sumOf { it.medication.price * it.quantity }
    val tax = subtotal * 0.05
    val total = subtotal + tax

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = TealPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pharmacy POS Checkout")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (cart.isEmpty()) {
                    Text(
                        "No items currently in POS cart. Add items from the pharmacy inventory list first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text("Selected Medications:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    cart.values.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.medication.brandName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("$${item.medication.price} each", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onUpdateQty(item.medication.id, item.quantity - 1) }, modifier = Modifier.size(28.dp)) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                Text("${item.quantity}", modifier = Modifier.padding(horizontal = 6.dp), fontWeight = FontWeight.Bold)
                                IconButton(onClick = { onUpdateQty(item.medication.id, item.quantity + 1) }, modifier = Modifier.size(28.dp)) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:", style = MaterialTheme.typography.bodySmall)
                        Text("$${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tax (5%):", style = MaterialTheme.typography.bodySmall)
                        Text("$${String.format("%.2f", tax)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$${String.format("%.2f", total)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = TealPrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = prescriptionNo,
                        onValueChange = { prescriptionNo = it },
                        label = { Text("Prescription # / Doctor Ref") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Payment Method selector
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Card / POS", "Cash", "Insurance").forEach { method ->
                            FilterChip(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                label = { Text(method, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (cart.isNotEmpty()) {
                Button(
                    onClick = {
                        onCheckout(patientName, prescriptionNo, paymentMethod, pharmacistName, pharmacistLicense)
                    },
                    modifier = Modifier.testTag("submit_pos_checkout_button")
                ) {
                    Text("Process Sale & Print Receipt")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun ReceiptDetailsDialog(
    receipt: PosSaleRecord,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TealPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dispensing POS Receipt")
            }
        },
        text = {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("CarePlus Community Pharmacy", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Licensed Premises Reg: PCN/R/2021/8472", style = MaterialTheme.typography.labelSmall)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    Text("Receipt ID: ${receipt.id}", fontWeight = FontWeight.SemiBold)
                    Text("Patient: ${receipt.patientName}")
                    Text("Prescription: ${receipt.prescriptionNumber}")
                    Text("Payment Method: ${receipt.paymentMethod}")
                    Text("Dispensed Items: ${receipt.itemsSummary}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Paid:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$${String.format("%.2f", receipt.totalAmount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = TealPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Dispensed & Certified by:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${receipt.pharmacistName} (${receipt.pharmacistLicense})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = TealPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}

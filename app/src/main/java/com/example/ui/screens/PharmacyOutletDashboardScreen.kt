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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.example.data.model.MedicationItem
import com.example.data.model.PharmacyOutlet
import com.example.data.model.PrescriptionRequestRecord
import com.example.data.model.UserRole
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.HealthTechViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun PharmacyOutletDashboardScreen(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val pharmacies by viewModel.pharmacies.collectAsStateWithLifecycle()
    val allMedications by viewModel.allMedications.collectAsStateWithLifecycle()
    val posSales by viewModel.posSales.collectAsStateWithLifecycle()
    val posCart by viewModel.posCart.collectAsStateWithLifecycle()
    val prescriptions by viewModel.allPrescriptions.collectAsStateWithLifecycle()

    // Pharmacy Association
    val activePharmacy = pharmacies.firstOrNull { it.id == "pharm_1" } ?: pharmacies.firstOrNull()

    // Security & Verification Guard:
    // Only verified pharmacy accounts should access pharmacy management tools
    val isVerified = activePharmacy?.isVerified == true && (currentUser?.isVerified == true || currentRole == UserRole.ADMIN)

    if (!isVerified) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, EmergencyAmber)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = EmergencyAmber, modifier = Modifier.size(54.dp))
                    Text(
                        text = "Pharmacy Verification Required",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "To access pharmacy inventory, prescription processing, and POS terminal tools, your pharmacy premises license must be verified by the Pharmacy Council of Nigeria (PCN) and platform compliance administrators.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { viewModel.navigateToTab("verification") },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Submit Premises Credentials")
                    }
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Inventory", "POS Terminal", "Prescriptions", "Store Profile", "Sales History")

    var showAddMedDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<MedicationItem?>(null) }
    var editingProfileDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Outlet Header
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
                                .size(38.dp)
                                .background(TealPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = TealPrimary)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = activePharmacy?.name ?: "CarePoint Community Pharmacy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Premises Reg: ${activePharmacy?.licenseNumber ?: "PCN/REG/2021/8472"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
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
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmergencyGreen, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("VERIFIED OUTLET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmergencyGreen)
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
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> PharmacyInventoryTab(
                medications = allMedications,
                onAddMedication = { showAddMedDialog = true },
                onEditMedication = { editingMedication = it },
                onDeleteMedication = { viewModel.deleteMedication(it) },
                onUpdateStock = { id, qty -> viewModel.updateStockQuantity(id, qty) },
                onToggleAvailability = { id, avail -> viewModel.updateMedicationAvailability(id, avail) }
            )
            1 -> PharmacyPosTab(
                medications = allMedications,
                posCart = posCart,
                onAddToCart = { viewModel.addToPosCart(it) },
                onUpdateQuantity = { id, qty -> viewModel.updatePosCartQuantity(id, qty) },
                onClearCart = { viewModel.clearPosCart() },
                onCheckout = { patient, rxNo, payment ->
                    viewModel.checkoutPosSale(
                        pharmacyId = activePharmacy?.id ?: "pharm_1",
                        patientName = patient,
                        prescriptionNo = rxNo,
                        paymentMethod = payment,
                        pharmacistName = currentUser?.fullName ?: "Duty Pharmacist",
                        pharmacistLicense = currentUser?.licenseNumber ?: "PCN/REG/2018/51029"
                    )
                }
            )
            2 -> PharmacyPrescriptionsTab(
                prescriptions = prescriptions,
                onUpdateStatus = { id, status, notes -> viewModel.updatePrescriptionStatus(id, status, notes) }
            )
            3 -> PharmacyProfileTab(
                pharmacy = activePharmacy,
                onEdit = { editingProfileDialog = true }
            )
            4 -> PharmacySalesHistoryTab(sales = posSales)
        }
    }

    // Dialog: Add Medication to Inventory
    if (showAddMedDialog) {
        var genericName by remember { mutableStateOf("") }
        var brandName by remember { mutableStateOf("") }
        var dosageForm by remember { mutableStateOf("Tablet") }
        var strength by remember { mutableStateOf("500mg") }
        var category by remember { mutableStateOf("Antibiotic") }
        var batchNumber by remember { mutableStateOf("BATCH-${System.currentTimeMillis() % 10000}") }
        var priceInput by remember { mutableStateOf("2500") }
        var stockInput by remember { mutableStateOf("50") }
        var requiresPrescription by remember { mutableStateOf(false) }
        var indication by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMedDialog = false },
            title = { Text("Add Medicine to Inventory") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = genericName,
                        onValueChange = { genericName = it },
                        label = { Text("Generic Name (e.g., Amoxicillin)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Brand Name (e.g., Amoxil)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dosageForm,
                            onValueChange = { dosageForm = it },
                            label = { Text("Dosage Form") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = strength,
                            onValueChange = { strength = it },
                            label = { Text("Strength") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Price (₦)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stockInput,
                            onValueChange = { stockInput = it },
                            label = { Text("Initial Stock") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Therapeutic Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = indication,
                        onValueChange = { indication = it },
                        label = { Text("Clinical Indication") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Requires Prescription (Rx)", fontSize = 13.sp)
                        Switch(checked = requiresPrescription, onCheckedChange = { requiresPrescription = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceInput.toDoubleOrNull() ?: 1000.0
                        val stock = stockInput.toIntOrNull() ?: 10
                        val med = MedicationItem(
                            id = "med_${UUID.randomUUID().toString().take(6)}",
                            pharmacyId = activePharmacy?.id ?: "pharm_1",
                            name = genericName.ifBlank { "Generic Medicine" },
                            brandName = brandName.ifBlank { genericName },
                            dosageForm = dosageForm,
                            strength = strength,
                            category = category,
                            batchNumber = batchNumber,
                            expiryDate = "2028-06",
                            price = price,
                            priceFormatted = "₦${"%,.2f".format(price)}",
                            stockQuantity = stock,
                            isAvailable = true,
                            requiresPrescription = requiresPrescription,
                            indication = indication.ifBlank { "General clinical therapeutic use" }
                        )
                        viewModel.addMedication(med)
                        showAddMedDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("submit_add_med_button")
                ) {
                    Text("Add to Stock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMedDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Edit Medication Details
    editingMedication?.let { med ->
        var stockInput by remember { mutableStateOf(med.stockQuantity.toString()) }
        var priceInput by remember { mutableStateOf(med.price.toString()) }
        var isAvailable by remember { mutableStateOf(med.isAvailable) }

        AlertDialog(
            onDismissRequest = { editingMedication = null },
            title = { Text("Update ${med.brandName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${med.name} • ${med.strength} (${med.dosageForm})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = stockInput,
                        onValueChange = { stockInput = it },
                        label = { Text("Stock Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Unit Price (₦)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as Available for Search")
                        Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newStock = stockInput.toIntOrNull() ?: med.stockQuantity
                        val newPrice = priceInput.toDoubleOrNull() ?: med.price
                        viewModel.updateMedication(
                            med.copy(
                                stockQuantity = newStock,
                                price = newPrice,
                                priceFormatted = "₦${"%,.2f".format(newPrice)}",
                                isAvailable = isAvailable,
                                lastUpdatedTimestamp = System.currentTimeMillis()
                            )
                        )
                        editingMedication = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMedication = null }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Edit Pharmacy Profile
    if (editingProfileDialog && activePharmacy != null) {
        var name by remember { mutableStateOf(activePharmacy.name) }
        var address by remember { mutableStateOf(activePharmacy.address) }
        var phone by remember { mutableStateOf(activePharmacy.phone) }
        var hours by remember { mutableStateOf(activePharmacy.operatingHours) }
        var status by remember { mutableStateOf(activePharmacy.operatingStatus) }

        AlertDialog(
            onDismissRequest = { editingProfileDialog = false },
            title = { Text("Edit Pharmacy Outlet Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Pharmacy Outlet Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Physical Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Dispensary Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Operating Hours") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Operating Status (Open / Closed / Call First)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePharmacyProfile(
                            activePharmacy.copy(
                                name = name,
                                address = address,
                                phone = phone,
                                operatingHours = hours,
                                operatingStatus = status
                            )
                        )
                        editingProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Update Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProfileDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PharmacyInventoryTab(
    medications: List<MedicationItem>,
    onAddMedication: () -> Unit,
    onEditMedication: (MedicationItem) -> Unit,
    onDeleteMedication: (String) -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onToggleAvailability: (String, Boolean) -> Unit
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
                Column {
                    Text("Medicines Inventory (${medications.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Real-time stock controls & availability", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = onAddMedication,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("add_medicine_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add Product")
                }
            }
        }

        items(medications, key = { it.id }) { med ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.brandName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${med.name} • ${med.strength} (${med.dosageForm})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Batch: ${med.batchNumber} • Exp: ${med.expiryDate}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(med.priceFormatted.ifBlank { "₦${med.price}" }, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealPrimary)
                            Surface(
                                color = if (med.stockQuantity > 0 && med.isAvailable) EmergencyGreen.copy(alpha = 0.15f) else EmergencyRed.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (med.stockQuantity > 0 && med.isAvailable) "Stock: ${med.stockQuantity}" else "Out of Stock",
                                    color = if (med.stockQuantity > 0 && med.isAvailable) EmergencyGreen else EmergencyRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Quick adjust:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { onUpdateStock(med.id, (med.stockQuantity - 5).coerceAtLeast(0)) },
                                modifier = Modifier.size(width = 44.dp, height = 30.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("-5", fontSize = 11.sp) }
                            Spacer(Modifier.width(4.dp))
                            OutlinedButton(
                                onClick = { onUpdateStock(med.id, med.stockQuantity + 10) },
                                modifier = Modifier.size(width = 44.dp, height = 30.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("+10", fontSize = 11.sp) }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { onEditMedication(med) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealPrimary)
                            }
                            IconButton(onClick = { onDeleteMedication(med.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EmergencyRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PharmacyPosTab(
    medications: List<MedicationItem>,
    posCart: Map<String, com.example.ui.viewmodel.PosCartItem>,
    onAddToCart: (MedicationItem) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: (String, String, String) -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var prescriptionNumber by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash / POS Card") }
    var posSearch by remember { mutableStateOf("") }

    val filteredMeds = remember(posSearch, medications) {
        if (posSearch.isBlank()) medications
        else medications.filter { it.brandName.contains(posSearch, ignoreCase = true) || it.name.contains(posSearch, ignoreCase = true) }
    }

    val totalAmount = posCart.values.sumOf { it.medication.price * it.quantity }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // POS Cart Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PointOfSale, contentDescription = null, tint = TealPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Active POS Cart (${posCart.size} items)", fontWeight = FontWeight.Bold)
                        }
                        if (posCart.isNotEmpty()) {
                            TextButton(onClick = onClearCart) { Text("Clear Cart", color = EmergencyRed) }
                        }
                    }

                    if (posCart.isEmpty()) {
                        Text(
                            "Cart is empty. Tap any medication below to add it to this sale transaction.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        posCart.values.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.medication.brandName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("₦${item.medication.price} each", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = { onUpdateQuantity(item.medication.id, item.quantity - 1) },
                                        modifier = Modifier.size(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) { Text("-") }
                                    Spacer(Modifier.width(6.dp))
                                    Text("${item.quantity}", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(6.dp))
                                    OutlinedButton(
                                        onClick = { onUpdateQuantity(item.medication.id, item.quantity + 1) },
                                        modifier = Modifier.size(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) { Text("+") }
                                    Spacer(Modifier.width(12.dp))
                                    Text("₦${"%,.2f".format(item.medication.price * item.quantity)}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Sale Amount:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("₦${"%,.2f".format(totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimary)
                        }

                        OutlinedTextField(
                            value = patientName,
                            onValueChange = { patientName = it },
                            label = { Text("Customer / Patient Name (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = prescriptionNumber,
                            onValueChange = { prescriptionNumber = it },
                            label = { Text("Prescription Slip # (if Rx)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                onCheckout(patientName, prescriptionNumber, paymentMethod)
                                patientName = ""
                                prescriptionNumber = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pos_checkout_button")
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Complete Sale & Print Receipt")
                        }
                    }
                }
            }
        }

        // Available items to add
        item {
            Text("Select Products to Add:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = posSearch,
                onValueChange = { posSearch = it },
                label = { Text("Quick Product Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(filteredMeds, key = { it.id }) { med ->
            Card(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(med.brandName, fontWeight = FontWeight.Bold)
                        Text("${med.name} • ${med.strength} • In Stock: ${med.stockQuantity}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("₦${med.price}", fontWeight = FontWeight.Bold, color = TealPrimary)
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { onAddToCart(med) },
                        enabled = med.stockQuantity > 0 && med.isAvailable,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.testTag("add_to_cart_${med.id}")
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun PharmacyPrescriptionsTab(
    prescriptions: List<PrescriptionRequestRecord>,
    onUpdateStatus: (String, String, String) -> Unit
) {
    var responseNotes by remember { mutableStateOf("") }
    var selectedPrescription by remember { mutableStateOf<PrescriptionRequestRecord?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Incoming Prescription Requests (${prescriptions.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (prescriptions.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(12.dp)) {
                    Text("No prescription requests at this time.", modifier = Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(prescriptions, key = { it.id }) { rx ->
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(rx.patientName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Phone: ${rx.patientPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = if (rx.status == "ACCEPTED") EmergencyGreen.copy(alpha = 0.15f) else EmergencyAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = rx.status,
                                color = if (rx.status == "ACCEPTED") EmergencyGreen else EmergencyAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text("Prescriber: ${rx.prescribingDoctor}", fontSize = 13.sp)
                    Text("Diagnosis: ${rx.diagnosisSummary}", fontSize = 13.sp)
                    Text("Medications: ${rx.medicationsList}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("Attached Document: ${rx.prescriptionDocumentUrl}", fontSize = 11.sp, color = TealPrimary)

                    if (rx.notesFromPharmacist.isNotBlank()) {
                        Text("Pharmacist Notes: ${rx.notesFromPharmacist}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(
                            onClick = { onUpdateStatus(rx.id, "ACCEPTED", "Prescription verified. Ready for dispensary pickup.") },
                            modifier = Modifier.testTag("accept_prescription_${rx.id}")
                        ) {
                            Text("Accept & Prepare")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { onUpdateStatus(rx.id, "DISPENSED", "Medication dispensed to patient.") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                            modifier = Modifier.testTag("dispense_prescription_${rx.id}")
                        ) {
                            Text("Mark Dispensed")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PharmacyProfileTab(
    pharmacy: PharmacyOutlet?,
    onEdit: () -> Unit
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
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Premises Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = TealPrimary)
                    }
                }

                Text("Name: ${pharmacy?.name}", fontWeight = FontWeight.SemiBold)
                Text("Physical Address: ${pharmacy?.address}")
                Text("Phone: ${pharmacy?.phone}")
                Text("Operating Hours: ${pharmacy?.operatingHours}")
                Text("Operating Status: ${pharmacy?.operatingStatus}", fontWeight = FontWeight.Bold, color = EmergencyGreen)
                Text("Regulatory License: ${pharmacy?.licenseNumber}", fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun PharmacySalesHistoryTab(sales: List<com.example.data.model.PosSaleRecord>) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Completed POS Transactions (${sales.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(sales, key = { it.id }) { sale ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sale.id, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("₦${"%,.2f".format(sale.totalAmount)}", fontWeight = FontWeight.Bold, color = TealPrimary)
                    }
                    Text(sale.itemsSummary, fontSize = 13.sp)
                    Text("Customer: ${sale.patientName} • Rx: ${sale.prescriptionNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Dispensed by: ${sale.pharmacistName} (${sale.pharmacistLicense}) • ${dateFormat.format(Date(sale.timestamp))}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

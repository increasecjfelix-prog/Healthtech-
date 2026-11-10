package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BrandSky
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.StockGreen
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.HealthTechViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseAndApiExplorerDialog(
    viewModel: HealthTechViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(TealPrimary.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Architecture & API Explorer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "PostgreSQL + PostGIS • REST • GraphQL Console",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider()

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("PostgreSQL + PostGIS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("GraphQL Console", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("REST API Endpoints", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Http, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            when (selectedTab) {
                0 -> PostgresSchemaView(context = context)
                1 -> GraphqlConsoleView(viewModel = viewModel)
                2 -> RestApiEndpointsView(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PostgresSchemaView(context: Context) {
    val scope = rememberCoroutineScope()
    var copiedMessage by remember { mutableStateOf(false) }

    val postgresDdl = """
-- ==========================================================
-- Health-Tech Platform Database Schema (PostgreSQL + PostGIS)
-- Spatial Geolocation, Role Segregation, Credential Workflow
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS postgis;

-- Enum Types for System Integrity
CREATE TYPE user_role AS ENUM (
    'patient', 'pharmacist', 'doctor', 'hospital_admin', 'system_admin'
);
CREATE TYPE verification_status AS ENUM (
    'pending', 'under_review', 'verified', 'rejected', 'revoked'
);
CREATE TYPE emergency_status_enum AS ENUM (
    'open_full_capacity', 'limited_capacity', 'diversion_only', 'closed'
);

-- 1. USERS & ACCOUNTS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(30) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'patient',
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. PROFESSIONAL CREDENTIALS & LICENSES
CREATE TABLE professional_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    license_number VARCHAR(100) UNIQUE NOT NULL,
    issuing_body VARCHAR(100) NOT NULL, -- e.g., PCN, MDCN
    document_url VARCHAR(500) NOT NULL,
    status verification_status DEFAULT 'pending',
    verified_by UUID REFERENCES users(id),
    rejection_reason TEXT,
    submitted_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMPTZ
);

-- 3. PHARMACIES & GEOLOCATION (PostGIS)
CREATE TABLE pharmacies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID REFERENCES users(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    operating_hours JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_pharmacies_location ON pharmacies USING GIST(location);

-- 4. MEDICATIONS (National Formulary & Drug Dictionary)
CREATE TABLE medications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    generic_name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255),
    dosage_form VARCHAR(50) NOT NULL, -- Tablet, Syrup, Injection, Suspension
    strength VARCHAR(50) NOT NULL,     -- 625mg, 500mg, 125mg/5ml
    category VARCHAR(100) NOT NULL
);

-- 5. PHARMACY INVENTORIES (Stock & Batch Tracking)
CREATE TABLE pharmacy_inventories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pharmacy_id UUID REFERENCES pharmacies(id) ON DELETE CASCADE,
    medication_id UUID REFERENCES medications(id) ON DELETE CASCADE,
    stock_quantity INT NOT NULL CHECK (stock_quantity >= 0),
    unit_price NUMERIC(10, 2) NOT NULL,
    batch_number VARCHAR(50),
    expiry_date DATE NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pharmacy_medication UNIQUE (pharmacy_id, medication_id)
);

-- 6. HOSPITALS & EMERGENCY REAL-TIME CAPACITY
CREATE TABLE hospitals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    license_number VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    emergency_phone VARCHAR(30) NOT NULL,
    emergency_status emergency_status_enum DEFAULT 'open_full_capacity',
    available_icu_beds INT DEFAULT 0,
    available_general_beds INT DEFAULT 0,
    has_oxygen_supply BOOLEAN DEFAULT TRUE,
    has_trauma_unit BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_hospitals_location ON hospitals USING GIST(location);

-- 7. VERIFIED HEALTH ARTICLES & ADVICE
CREATE TABLE health_articles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    is_published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
""".trimIndent()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "PostgreSQL + PostGIS Schema",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("PostgreSQL DDL", postgresDdl)
                                clipboard.setPrimaryClip(clip)
                                copiedMessage = true
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                if (copiedMessage) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (copiedMessage) "Copied" else "Copy SQL", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Features: GIST spatial indexes on geography(POINT, 4326), check constraints on stock quantities, composite unique constraints, role enums, and real-time emergency capacity columns.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // PostGIS Spatial Query Feature Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "SPATIAL DISTANCE QUERY (PostGIS ST_DWithin & ST_Distance)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            """
SELECT 
    p.id,
    p.name,
    p.address,
    m.generic_name,
    m.strength,
    i.stock_quantity,
    i.unit_price,
    ROUND((ST_Distance(p.location, ST_MakePoint(7.033, 5.483)::geography) / 1000)::numeric, 1) AS distance_km
FROM pharmacies p
JOIN pharmacy_inventories i ON p.id = i.pharmacy_id
JOIN medications m ON i.medication_id = m.id
WHERE 
    m.generic_name ILIKE '%Amoxicillin%'
    AND i.stock_quantity > 0
    AND ST_DWithin(p.location, ST_MakePoint(7.033, 5.483)::geography, 10000)
ORDER BY distance_km ASC;
                            """.trimIndent(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // SQL DDL Output Code Block
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "DATABASE DEFINITION (DDL)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            postgresDdl,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GraphqlConsoleView(viewModel: HealthTechViewModel) {
    val hospitals by viewModel.hospitals.collectAsStateWithLifecycle()
    val medications by viewModel.allMedications.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var selectedOperation by remember { mutableIntStateOf(0) }
    var executionResult by remember { mutableStateOf<String?>(null) }
    var isExecuting by remember { mutableStateOf(false) }

    val sampleQueries = listOf(
        "query: searchMedicationNearby" to """
query SearchMedicationNearby {
  searchMedicationNearby(
    medicationName: "Amoxicillin"
    latitude: 5.483
    longitude: 7.033
    radiusKm: 10.0
  ) {
    id
    name
    address
    distanceKm
    phone
    isVerified
    inventory {
      medicationName
      strength
      stockQuantity
      unitPrice
    }
  }
}
        """.trimIndent(),
        "query: nearbyEmergencyHospitals" to """
query NearbyEmergencyHospitals {
  nearbyEmergencyHospitals(
    latitude: 5.483
    longitude: 7.033
    requireIcu: true
  ) {
    id
    name
    emergencyPhone
    emergencyStatus
    availableIcuBeds
    availableGeneralBeds
    hasOxygenSupply
    hasTraumaUnit
    distanceKm
  }
}
        """.trimIndent(),
        "mutation: updateEmergencyStatus" to """
mutation UpdateEmergencyStatus {
  updateEmergencyStatus(
    hospitalId: "hosp_1"
    status: OPEN_FULL_CAPACITY
    availableIcuBeds: 7
  ) {
    id
    name
    emergencyStatus
    availableIcuBeds
    updatedAt
  }
}
        """.trimIndent(),
        "mutation: updateStockQuantity" to """
mutation UpdateStockQuantity {
  updateStockQuantity(
    inventoryId: "med_1"
    newQuantity: 45
  ) {
    id
    name
    stockQuantity
    updatedAt
  }
}
        """.trimIndent()
    )

    fun executeActiveGraphQL() {
        scope.launch {
            isExecuting = true
            delay(120) // simulate real network + query round-trip latency
            executionResult = when (selectedOperation) {
                0 -> {
                    """
{
  "data": {
    "searchMedicationNearby": [
      {
        "id": "pharm_1",
        "name": "CarePoint Community Pharmacy",
        "address": "14 Commercial Avenue, Yaba, Lagos",
        "distanceKm": 0.8,
        "phone": "+2348001234567",
        "isVerified": true,
        "inventory": [
          {
            "medicationName": "Amoxicillin / Clavulanic Acid",
            "strength": "625mg",
            "stockQuantity": ${medications.find { it.id == "med_1" }?.stockQuantity ?: 42},
            "unitPrice": 3500.00
          }
        ]
      },
      {
        "id": "pharm_2",
        "name": "Meds & More Express Outlet",
        "address": "88 Herbert Macaulay Way, Yaba, Lagos",
        "distanceKm": 1.4,
        "phone": "+2348009876543",
        "isVerified": true,
        "inventory": [
          {
            "medicationName": "Amoxicillin Capsules",
            "strength": "500mg",
            "stockQuantity": ${medications.find { it.id == "med_2" }?.stockQuantity ?: 5},
            "unitPrice": 1800.00
          }
        ]
      },
      {
        "id": "pharm_3",
        "name": "HealthFirst Pharmacy",
        "address": "22 University Road, Akoka, Lagos",
        "distanceKm": 3.2,
        "phone": "+2348001122334",
        "isVerified": true,
        "inventory": [
          {
            "medicationName": "Amoxicillin Suspension",
            "strength": "125mg/5ml",
            "stockQuantity": ${medications.find { it.id == "med_3" }?.stockQuantity ?: 18},
            "unitPrice": 2100.00
          }
        ]
      }
    ]
  },
  "extensions": {
    "executionLatency": "38ms",
    "spatialEngine": "PostGIS ST_DWithin (radius: 10km)",
    "status": 200
  }
}
                    """.trimIndent()
                }
                1 -> {
                    """
{
  "data": {
    "nearbyEmergencyHospitals": [
      ${
                        hospitals.joinToString(",\n      ") { hosp ->
                            """
      {
        "id": "${hosp.id}",
        "name": "${hosp.name}",
        "emergencyPhone": "${hosp.emergencyPhone}",
        "emergencyStatus": "${hosp.emergencyStatus}",
        "availableIcuBeds": ${hosp.availableIcuBeds},
        "availableGeneralBeds": ${hosp.availableGeneralBeds},
        "hasOxygenSupply": ${hosp.hasOxygenSupply},
        "hasTraumaUnit": ${hosp.hasTraumaUnit},
        "distanceKm": ${hosp.distanceKm}
      }""".trimIndent()
                        }
                    }
    ]
  },
  "extensions": {
    "executionLatency": "26ms",
    "spatialIndex": "idx_hospitals_location",
    "status": 200
  }
}
                    """.trimIndent()
                }
                2 -> {
                    viewModel.updateHospitalEmergency("hosp_1", "OPEN_FULL_CAPACITY", 7, 10)
                    """
{
  "data": {
    "updateEmergencyStatus": {
      "id": "hosp_1",
      "name": "Lagos Metropolitan University Teaching Hospital",
      "emergencyStatus": "OPEN_FULL_CAPACITY",
      "availableIcuBeds": 7,
      "updatedAt": "${System.currentTimeMillis()}"
    }
  },
  "extensions": {
    "executionLatency": "45ms",
    "mutation": "COMMITTED_TO_ROOM_DATABASE",
    "status": 200
  }
}
                    """.trimIndent()
                }
                3 -> {
                    viewModel.updateStockQuantity("med_1", 45)
                    """
{
  "data": {
    "updateStockQuantity": {
      "id": "med_1",
      "name": "Amoxicillin / Clavulanic Acid 625mg",
      "stockQuantity": 45,
      "updatedAt": "${System.currentTimeMillis()}"
    }
  },
  "extensions": {
    "executionLatency": "31ms",
    "mutation": "STOCK_UPDATED_LIVE",
    "status": 200
  }
}
                    """.trimIndent()
                }
                else -> ""
            }
            isExecuting = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "Select GraphQL Operation to Execute:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sampleQueries.forEachIndexed { index, (name, _) ->
                    FilterChip(
                        selected = selectedOperation == index,
                        onClick = {
                            selectedOperation = index
                            executionResult = null
                        },
                        label = { Text(name, fontSize = 11.sp) },
                        leadingIcon = if (selectedOperation == index) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null
                    )
                }
            }
        }

        // Query Document
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "GRAPHQL DOCUMENT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        Button(
                            onClick = { executeActiveGraphQL() },
                            enabled = !isExecuting,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSky),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp).testTag("run_graphql_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isExecuting) "Running..." else "Execute", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            sampleQueries[selectedOperation].second,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFF1F5F9),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Response Document
        if (executionResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "GRAPHQL RESPONSE (200 OK)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80)
                            )
                            Text(
                                "Real-Time JSON Output",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                executionResult ?: "",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF86EFAC),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestApiEndpointsView(viewModel: HealthTechViewModel) {
    val endpoints = listOf(
        RestEndpoint(
            method = "GET",
            path = "/api/v1/pharmacies/search",
            query = "medication=Amoxicillin&lat=5.483&lng=7.033&radius_km=10",
            description = "PostGIS proximity drug search querying pharmacies having stock within distance radius.",
            responseExample = """
[
  {
    "pharmacy_id": "pharm_1",
    "name": "CarePoint Community Pharmacy",
    "address": "14 Commercial Avenue, Yaba, Lagos",
    "distance_km": 0.8,
    "phone": "+2348001234567",
    "is_verified": true,
    "generic_name": "Amoxicillin / Clavulanic Acid",
    "strength": "625mg",
    "stock_quantity": 42,
    "unit_price": "₦3,500"
  }
]
            """.trimIndent()
        ),
        RestEndpoint(
            method = "GET",
            path = "/api/v1/hospitals/emergency-near-me",
            query = "lat=5.483&lng=7.033",
            description = "Retrieves ER triage capacity, available ICU beds, and live status of nearby medical centers.",
            responseExample = """
[
  {
    "id": "hosp_1",
    "name": "Lagos Metropolitan University Teaching Hospital",
    "emergency_status": "open_full_capacity",
    "available_icu_beds": 6,
    "available_general_beds": 42,
    "has_oxygen_supply": true,
    "has_trauma_unit": true,
    "distance_km": 1.2,
    "emergency_phone": "+2348009110422"
  }
]
            """.trimIndent()
        ),
        RestEndpoint(
            method = "PATCH",
            path = "/api/v1/pharmacies/{id}/inventory/{item_id}",
            query = "",
            description = "Atomic inventory stock decrement after prescription dispensing or point-of-sale checkout.",
            responseExample = """
{
  "status": "success",
  "item_id": "med_1",
  "stock_quantity": 41,
  "updated_at": "2026-09-16T16:30:00Z"
}
            """.trimIndent()
        ),
        RestEndpoint(
            method = "POST",
            path = "/api/v1/verification/submit",
            query = "",
            description = "Submits doctor, pharmacist, or hospital credentials to MDCN/PCN for license verification.",
            responseExample = """
{
  "status": "submitted",
  "verification_id": "ver_9841",
  "review_status": "pending",
  "submitted_at": "2026-09-16T16:30:00Z"
}
            """.trimIndent()
        ),
        RestEndpoint(
            method = "GET",
            path = "/api/v1/content/articles",
            query = "category=Pharmacology",
            description = "Returns verified, peer-reviewed clinical articles published by certified doctors.",
            responseExample = """
[
  {
    "id": "art_2",
    "title": "Antibiotic Resistance & Stewardship",
    "author_name": "Pharm. David Osei",
    "is_verified_professional": true,
    "is_peer_reviewed": true
  }
]
            """.trimIndent()
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(endpoints) { ep ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (ep.method) {
                                "GET" -> Color(0xFF0284C7).copy(alpha = 0.15f)
                                "POST" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                "PATCH" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = ep.method,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = when (ep.method) {
                                    "GET" -> Color(0xFF0284C7)
                                    "POST" -> Color(0xFF059669)
                                    "PATCH" -> Color(0xFFD97706)
                                    else -> Color(0xFFDC2626)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = ep.path,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (ep.query.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "?${ep.query}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandSky,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ep.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = ep.responseExample,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

data class RestEndpoint(
    val method: String,
    val path: String,
    val query: String,
    val description: String,
    val responseExample: String
)

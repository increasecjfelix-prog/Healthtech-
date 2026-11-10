package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HospitalEmergencyScreen
import com.example.ui.screens.PharmacyOutletDashboardScreen
import com.example.ui.screens.PharmacyScreen
import com.example.ui.screens.ProfessionalDashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VerificationScreen
import com.example.ui.screens.VerifiedMedicalScreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBlue
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.HealthTechViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTechApp(
    viewModel: HealthTechViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val posCart by viewModel.posCart.collectAsStateWithLifecycle()
    val cartCount = posCart.values.sumOf { it.quantity }
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showAuthModal by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (showAuthModal) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = { showAuthModal = false }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(TealPrimary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HealthTech",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Profile / Sign In button
                    IconButton(
                        onClick = {
                            if (isLoggedIn) {
                                viewModel.setTab(AppNavTab.SETTINGS)
                            } else {
                                showAuthModal = true
                            }
                        },
                        modifier = Modifier.testTag("top_bar_account_button")
                    ) {
                        Icon(
                            imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.AutoMirrored.Filled.Login,
                            contentDescription = if (isLoggedIn) "Account & Settings" else "Sign In",
                            tint = if (isLoggedIn) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick SOS Emergency Call Button in top bar
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:911")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showMessage("Emergency line: 911")
                            }
                        },
                        modifier = Modifier.testTag("top_bar_sos_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(EmergencyRed, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Emergency SOS",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    // Role Indicator Pill - tap to jump directly to role dashboard
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VerifiedBadgeBg,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clickable {
                                when (currentRole) {
                                    UserRole.ADMIN -> viewModel.setTab(AppNavTab.ADMIN_DASHBOARD)
                                    UserRole.PHARMACY_OUTLET -> viewModel.setTab(AppNavTab.OUTLET_DASHBOARD)
                                    UserRole.DOCTOR, UserRole.PHARMACIST -> viewModel.setTab(AppNavTab.PROFESSIONAL_DASHBOARD)
                                    UserRole.HOSPITAL, UserRole.HEALTHCARE_FACILITY -> viewModel.setTab(AppNavTab.EMERGENCY)
                                    UserRole.PATIENT -> viewModel.setTab(AppNavTab.VERIFICATION)
                                }
                            }
                    ) {
                        Text(
                            text = when (currentRole) {
                                UserRole.PATIENT -> "Patient"
                                UserRole.PHARMACIST -> "Pharmacist"
                                UserRole.DOCTOR -> "Doctor"
                                UserRole.PHARMACY_OUTLET -> "Pharmacy Outlet"
                                UserRole.HOSPITAL -> "Hospital ER"
                                UserRole.HEALTHCARE_FACILITY -> "Facility"
                                UserRole.ADMIN -> "Admin"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = VerifiedBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // 1. Pharmacy Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.PHARMACY,
                    onClick = { viewModel.setTab(AppNavTab.PHARMACY) },
                    icon = {
                        if (cartCount > 0) {
                            BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = "Pharmacy")
                            }
                        } else {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = "Pharmacy")
                        }
                    },
                    label = { Text("Pharmacy", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_pharmacy")
                )

                // 2. Verified Medical Advice Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.VERIFIED_INFO,
                    onClick = { viewModel.setTab(AppNavTab.VERIFIED_INFO) },
                    icon = { Icon(Icons.Default.Verified, contentDescription = "Medical Advice") },
                    label = { Text("Advice", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_advice")
                )

                // 3. Hospital & Emergency Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.EMERGENCY,
                    onClick = { viewModel.setTab(AppNavTab.EMERGENCY) },
                    icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Hospital & ER") },
                    label = { Text("Emergency", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_emergency")
                )

                // 4. Role-Specific Workspace or Verification Tab
                when (currentRole) {
                    UserRole.ADMIN -> {
                        NavigationBarItem(
                            selected = currentTab == AppNavTab.ADMIN_DASHBOARD,
                            onClick = { viewModel.setTab(AppNavTab.ADMIN_DASHBOARD) },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Portal") },
                            label = { Text("Admin Hub", fontSize = 10.sp) },
                            modifier = Modifier.testTag("nav_tab_admin_hub")
                        )
                    }
                    UserRole.PHARMACY_OUTLET -> {
                        NavigationBarItem(
                            selected = currentTab == AppNavTab.OUTLET_DASHBOARD,
                            onClick = { viewModel.setTab(AppNavTab.OUTLET_DASHBOARD) },
                            icon = { Icon(Icons.Default.PointOfSale, contentDescription = "Outlet POS") },
                            label = { Text("Outlet POS", fontSize = 10.sp) },
                            modifier = Modifier.testTag("nav_tab_outlet_pos")
                        )
                    }
                    UserRole.DOCTOR, UserRole.PHARMACIST -> {
                        NavigationBarItem(
                            selected = currentTab == AppNavTab.PROFESSIONAL_DASHBOARD,
                            onClick = { viewModel.setTab(AppNavTab.PROFESSIONAL_DASHBOARD) },
                            icon = { Icon(Icons.Default.Badge, contentDescription = "Professional Workspace") },
                            label = { Text("Workspace", fontSize = 10.sp) },
                            modifier = Modifier.testTag("nav_tab_workspace")
                        )
                    }
                    else -> {
                        NavigationBarItem(
                            selected = currentTab == AppNavTab.VERIFICATION,
                            onClick = { viewModel.setTab(AppNavTab.VERIFICATION) },
                            icon = { Icon(Icons.Default.Security, contentDescription = "Verification") },
                            label = { Text("Verify", fontSize = 10.sp) },
                            modifier = Modifier.testTag("nav_tab_verification")
                        )
                    }
                }

                // 5. Settings Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.SETTINGS,
                    onClick = { viewModel.setTab(AppNavTab.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { tab ->
            when (tab) {
                AppNavTab.PHARMACY -> PharmacyScreen(viewModel = viewModel)
                AppNavTab.VERIFIED_INFO -> VerifiedMedicalScreen(viewModel = viewModel)
                AppNavTab.EMERGENCY -> HospitalEmergencyScreen(viewModel = viewModel)
                AppNavTab.VERIFICATION -> VerificationScreen(viewModel = viewModel)
                AppNavTab.PROFESSIONAL_DASHBOARD -> ProfessionalDashboardScreen(viewModel = viewModel)
                AppNavTab.OUTLET_DASHBOARD -> PharmacyOutletDashboardScreen(viewModel = viewModel)
                AppNavTab.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                AppNavTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.setTab(AppNavTab.PHARMACY) },
                    onNavigateToAuth = { showAuthModal = true }
                )
            }
        }
    }

}

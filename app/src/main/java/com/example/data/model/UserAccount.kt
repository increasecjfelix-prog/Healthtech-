package com.example.data.model

data class UserAccount(
    val id: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val verificationStatus: VerificationState = VerificationState.PENDING,
    val licenseNumber: String = "",
    val regulatoryBody: String = "",
    val professionalCategory: String = "",
    val specialty: String = "",
    val facilityName: String = "",
    val certificateDocName: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val isSuspended: Boolean = false,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val bloodGroup: String = "O+",
    val allergies: String = "None known"
)

data class AppSettings(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val emergencySmsAlerts: Boolean = true,
    val autoShareVitalsWithER: Boolean = true,
    val offlineSyncEnabled: Boolean = true,
    val prescriptionReminders: Boolean = true,
    val biometricAuthEnabled: Boolean = false,
    val dataSaverMode: Boolean = false
)

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. All 7 System Roles
enum class UserRole(val label: String, val code: String) {
    PATIENT("Patient / User", "patient"),
    DOCTOR("Medical Doctor", "doctor"),
    PHARMACIST("Pharmacist", "pharmacist"),
    PHARMACY_OUTLET("Pharmacy Outlet", "pharmacy_outlet"),
    HOSPITAL("Hospital", "hospital"),
    HEALTHCARE_FACILITY("Other Healthcare Facility", "healthcare_facility"),
    ADMIN("Platform Administrator", "admin")
}

// 2. Professional Verification States
enum class VerificationState(val label: String) {
    PENDING("Pending"),
    VERIFIED("Verified"),
    REJECTED("Rejected"),
    SUSPENDED("Suspended")
}

// 3. Emergency Statuses required by specifications
enum class EmergencyStatusOption(val rawValue: String, val label: String) {
    OPEN("OPEN", "Open"),
    CLOSED("CLOSED", "Closed"),
    TEMPORARILY_UNAVAILABLE("TEMPORARILY_UNAVAILABLE", "Temporarily unavailable"),
    ACCEPTING_PATIENTS("ACCEPTING_PATIENTS", "Accepting emergency patients"),
    NOT_ACCEPTING_PATIENTS("NOT_ACCEPTING_PATIENTS", "Not accepting emergency patients")
}

@Entity(tableName = "pharmacies")
data class PharmacyOutlet(
    @PrimaryKey val id: String,
    val name: String,
    val licenseNumber: String,
    val address: String,
    val distanceKm: Double,
    val phone: String,
    val isOpen24Hours: Boolean,
    val rating: Double,
    val isVerified: Boolean = true,
    val latitude: Double = 5.483,
    val longitude: Double = 7.033,
    val operatingHours: String = "Mon-Sat: 08:00 - 20:00",
    val operatingStatus: String = "Open", // "Open", "Closed", "Call First"
    val lastInventoryUpdate: Long = System.currentTimeMillis() - 7200000L, // 2 hours ago
    val isActive: Boolean = true
)

@Entity(tableName = "medications")
data class MedicationItem(
    @PrimaryKey val id: String,
    val pharmacyId: String,
    val name: String, // Generic Name
    val brandName: String, // Brand Name
    val dosageForm: String, // Tablet, Capsule, Syrup, Injection, Suspension
    val strength: String = "", // e.g. "500mg", "250mg/5ml", "10mg"
    val category: String, // Antibiotic, Analgesic, Antihypertensive, etc.
    val batchNumber: String,
    val expiryDate: String,
    val price: Double,
    val priceFormatted: String = "",
    val stockQuantity: Int,
    val isAvailable: Boolean = true,
    val requiresPrescription: Boolean,
    val indication: String,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis() - 3600000L // 1 hour ago
)

@Entity(tableName = "medication_schedules")
data class MedicationSchedule(
    @PrimaryKey val id: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val timeOfDay: String,
    val instructions: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val reminderEnabled: Boolean = true,
    val isTakenToday: Boolean = false,
    val isActive: Boolean = true,
    val prescribingDoctor: String = "",
    val patientName: String = "Self",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pos_sales")
data class PosSaleRecord(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val pharmacyId: String,
    val itemsSummary: String,
    val patientName: String,
    val prescriptionNumber: String,
    val totalAmount: Double,
    val paymentMethod: String,
    val pharmacistName: String,
    val pharmacistLicense: String
)

@Entity(tableName = "medical_articles")
data class MedicalArticle(
    @PrimaryKey val id: String,
    val title: String,
    val authorName: String,
    val authorRole: String, // "Doctor", "Pharmacist"
    val authorLicenseNo: String,
    val isVerifiedProfessional: Boolean = true,
    val isPeerReviewed: Boolean = true,
    val reviewingBody: String = "National Clinical Review Board",
    val category: String, // "General health education", "Medication information", "Preventive health", "Chronic disease education", "Mental health education", "Public health", "Medication safety"
    val summary: String,
    val content: String,
    val keyTakeaways: String,
    val publishedDate: String,
    val readTimeMinutes: Int,
    val likesCount: Int = 0,
    val isBookmarked: Boolean = false,
    val isFlagged: Boolean = false,
    val isApprovedByAdmin: Boolean = true
)

@Entity(tableName = "medical_videos")
data class MedicalVideo(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorRole: String, // "Doctor", "Pharmacist"
    val authorLicenseNo: String,
    val facilityName: String,
    val category: String,
    val videoDuration: String, // e.g. "04:15"
    val durationSeconds: Int = 255,
    val videoUrl: String = "",
    val keyMedicalTakeaways: String = "",
    val accreditedCmeUnits: Int = 0,
    val isVerifiedProfessional: Boolean = true,
    val isPeerReviewed: Boolean = true,
    val reviewingBoard: String = "Council Clinical Media Review",
    val publishedDate: String = "Today",
    val viewsCount: Int = 124,
    val likesCount: Int = 45,
    val isBookmarked: Boolean = false,
    val isFlagged: Boolean = false
)

@Entity(tableName = "hospitals")
data class HospitalProfile(
    @PrimaryKey val id: String,
    val name: String,
    val facilityType: String = "Hospital", // "Hospital", "Other Healthcare Facility", "Specialist Clinic", "Diagnostic Center", "Maternity"
    val licenseNumber: String,
    val address: String,
    val distanceKm: Double,
    val emergencyPhone: String,
    val ambulancePhone: String,
    val operatingHours: String = "24/7 Emergency & Inpatient Services",
    val emergencyStatus: String = "ACCEPTING_PATIENTS", // OPEN, CLOSED, TEMPORARILY_UNAVAILABLE, ACCEPTING_PATIENTS, NOT_ACCEPTING_PATIENTS
    val emergencyStatusUpdatedAt: Long = System.currentTimeMillis() - 1800000L, // 30 mins ago
    val erWaitMinutes: Int = 12,
    val availableIcuBeds: Int = 4,
    val totalIcuBeds: Int = 12,
    val availableGeneralBeds: Int = 24,
    val hasOxygenSupply: Boolean = true,
    val hasTraumaUnit: Boolean = true,
    val traumaBaysReady: Int = 2,
    val bloodBankStock: String = "O+ (12 units), A+ (8 units)",
    val oxygenPlantOperational: Boolean = true,
    val ambulanceAvailable: Boolean = true,
    val ambulanceUnitsActive: Int = 2,
    val servicesOffered: String = "Emergency Trauma, Intensive Care, General Surgery, Cardiology, Pediatrics",
    val isVerified: Boolean = true,
    val latitude: Double = 5.483,
    val longitude: Double = 7.033
)

@Entity(tableName = "verifications")
data class VerificationRecord(
    @PrimaryKey val id: String,
    val applicantName: String,
    val email: String = "",
    val phone: String = "",
    val facilityName: String = "",
    val professionType: String, // "DOCTOR", "PHARMACIST", "HOSPITAL", "PHARMACY_OUTLET", "HEALTHCARE_FACILITY"
    val regulatoryCouncil: String, // "MDCN (Medical and Dental Council)", "PCN (Pharmacy Council)", etc.
    val licenseNumber: String,
    val certificateDocName: String,
    val specialization: String = "", // e.g. "Cardiology", "Clinical Pharmacy"
    val status: String = "PENDING", // "PENDING", "VERIFIED", "REJECTED", "SUSPENDED"
    val reviewerNotes: String = "",
    val submissionTimestamp: Long = System.currentTimeMillis(),
    val reviewedTimestamp: Long? = null,
    val verifiedBadgeIssued: Boolean = false
)

// Users table with secure authentication credentials
@Entity(tableName = "users")
data class UserAccountEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val passwordSalt: String,
    val role: String, // UserRole enum name
    val verificationStatus: String = "PENDING", // PENDING, VERIFIED, REJECTED, SUSPENDED
    val regulatoryBody: String = "",
    val licenseNumber: String = "",
    val professionalCategory: String = "",
    val specialty: String = "",
    val facilityAffiliation: String = "",
    val certificateDocName: String = "",
    val isSuspended: Boolean = false,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val bloodGroup: String = "O+",
    val allergies: String = "None known",
    val createdAt: Long = System.currentTimeMillis()
)

// Reported Medical Education Content
@Entity(tableName = "reported_content")
data class ReportedContentRecord(
    @PrimaryKey val id: String,
    val contentId: String,
    val contentType: String, // "ARTICLE", "VIDEO"
    val contentTitle: String,
    val authorName: String,
    val authorRole: String,
    val reporterName: String,
    val reporterEmail: String,
    val reasonCategory: String, // "Misleading Information", "Harmful Medical Advice", "Unlicensed Claims", "Offensive Content", "Outdated Protocol"
    val details: String,
    val reportedTimestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING_REVIEW", // "PENDING_REVIEW", "CONTENT_REMOVED", "DISMISSED", "AUTHOR_WARNED"
    val adminNotes: String = "",
    val reviewedBy: String = "",
    val reviewedTimestamp: Long? = null
)

// Platform Audit Trail & Activity Logs
@Entity(tableName = "audit_logs")
data class AuditLogRecord(
    @PrimaryKey val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actorName: String,
    val actorEmail: String,
    val actorRole: String,
    val actionType: String, // "VERIFICATION_APPROVED", "VERIFICATION_REJECTED", "ACCOUNT_SUSPENDED", "ACCOUNT_REACTIVATED", "CONTENT_REMOVED", "EMERGENCY_STATUS_UPDATED", "INVENTORY_RESTOCKED", "PRESCRIPTION_PROCESSED"
    val targetId: String,
    val targetType: String, // "USER", "VERIFICATION", "ARTICLE", "HOSPITAL", "PHARMACY", "MEDICATION"
    val description: String,
    val ipAddress: String = "127.0.0.1"
)

// Medicine Authenticity & Anti-Counterfeiting Verification Module
@Entity(tableName = "medicine_authenticity")
data class MedicineAuthenticityRecord(
    @PrimaryKey val id: String,
    val drugName: String,
    val brandName: String,
    val manufacturer: String,
    val batchNumber: String,
    val scratchCode: String,
    val regulatoryPin: String,
    val verificationStatus: String, // "AUTHENTIC_VERIFIED", "POTENTIAL_COUNTERFEIT", "EXPIRED_BATCH", "RECALL_ALERT"
    val verificationProvider: String = "Mobile Authentication Service (MAS) / Regulatory Agency",
    val checkedTimestamp: Long = System.currentTimeMillis(),
    val alertNotes: String = ""
)

// Prescription Submissions by Patients
@Entity(tableName = "prescription_requests")
data class PrescriptionRequestRecord(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientName: String,
    val patientPhone: String,
    val patientEmail: String,
    val targetPharmacyId: String,
    val targetPharmacyName: String,
    val prescribingDoctor: String,
    val diagnosisSummary: String,
    val medicationsList: String,
    val prescriptionDocumentUrl: String = "",
    val status: String = "PENDING_REVIEW", // "PENDING_REVIEW", "ACCEPTED", "DISPENSED", "REJECTED"
    val notesFromPharmacist: String = "",
    val submittedTimestamp: Long = System.currentTimeMillis()
)

// Saved/Preferred Facilities for Patients
@Entity(tableName = "preferred_facilities")
data class PreferredFacilityRecord(
    @PrimaryKey val id: String,
    val userId: String,
    val facilityId: String,
    val facilityName: String,
    val facilityType: String, // "PHARMACY", "HOSPITAL", "CLINIC"
    val address: String,
    val phone: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

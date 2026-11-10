package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "email") val email: String,
    @Json(name = "password_hash") val passwordHash: String,
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "role") val role: String
)

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password_hash") val passwordHash: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "email") val email: String,
    @Json(name = "role") val role: String,
    @Json(name = "is_verified") val isVerified: Boolean
)

@JsonClass(generateAdapter = true)
data class PharmacyNearbyResponseDto(
    @Json(name = "pharmacy_id") val pharmacyId: String,
    @Json(name = "name") val name: String,
    @Json(name = "address") val address: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "distance_km") val distanceKm: Double,
    @Json(name = "is_verified") val isVerified: Boolean,
    @Json(name = "medication") val medication: MedicationOfferDto? = null
)

@JsonClass(generateAdapter = true)
data class MedicationOfferDto(
    @Json(name = "medication_id") val medicationId: String,
    @Json(name = "name") val name: String,
    @Json(name = "brand_name") val brandName: String,
    @Json(name = "strength") val strength: String,
    @Json(name = "dosage_form") val dosageForm: String,
    @Json(name = "price") val price: Double,
    @Json(name = "stock_quantity") val stockQuantity: Int,
    @Json(name = "requires_prescription") val requiresPrescription: Boolean
)

@JsonClass(generateAdapter = true)
data class HospitalEmergencyCapacityDto(
    @Json(name = "hospital_id") val hospitalId: String,
    @Json(name = "hospital_name") val hospitalName: String,
    @Json(name = "emergency_status") val emergencyStatus: String,
    @Json(name = "available_icu_beds") val availableIcuBeds: Int,
    @Json(name = "total_icu_beds") val totalIcuBeds: Int,
    @Json(name = "diverting_ambulances") val divertingAmbulances: Boolean,
    @Json(name = "er_wait_time_minutes") val erWaitTimeMinutes: Int,
    @Json(name = "distance_km") val distanceKm: Double,
    @Json(name = "phone") val phone: String
)

@JsonClass(generateAdapter = true)
data class PrescriptionUploadRequestDto(
    @Json(name = "patient_id") val patientId: String,
    @Json(name = "diagnosis") val diagnosis: String,
    @Json(name = "medications") val medications: List<String>,
    @Json(name = "prescription_image_base64") val prescriptionImageBase64: String? = null
)

@JsonClass(generateAdapter = true)
data class PrescriptionUploadResponseDto(
    @Json(name = "prescription_id") val prescriptionId: String,
    @Json(name = "status") val status: String,
    @Json(name = "matching_pharmacy_count") val matchingPharmacyCount: Int
)

@JsonClass(generateAdapter = true)
data class EmergencyTriageRequestDto(
    @Json(name = "caller_id") val callerId: String,
    @Json(name = "incident_type") val incidentType: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "severity_level") val severityLevel: String,
    @Json(name = "notes") val notes: String
)

@JsonClass(generateAdapter = true)
data class EmergencyTriageResponseDto(
    @Json(name = "incident_id") val incidentId: String,
    @Json(name = "dispatched_hospital_id") val dispatchedHospitalId: String,
    @Json(name = "dispatched_hospital_name") val dispatchedHospitalName: String,
    @Json(name = "estimated_arrival_minutes") val estimatedArrivalMinutes: Int,
    @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class LicenseVerificationRequestDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "role") val role: String,
    @Json(name = "license_number") val licenseNumber: String,
    @Json(name = "issuing_authority") val issuingAuthority: String,
    @Json(name = "document_url") val documentUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class LicenseVerificationResponseDto(
    @Json(name = "verification_id") val verificationId: String,
    @Json(name = "status") val status: String,
    @Json(name = "submitted_at") val submittedAt: String
)

@JsonClass(generateAdapter = true)
data class StockUpdateRequestDto(
    @Json(name = "quantity") val quantity: Int
)

@JsonClass(generateAdapter = true)
data class GraphQLRequest(
    @Json(name = "query") val query: String,
    @Json(name = "variables") val variables: Map<String, Any?> = emptyMap()
)

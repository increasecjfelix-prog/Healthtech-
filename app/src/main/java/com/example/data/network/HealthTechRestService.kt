package com.example.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HealthTechRestService {

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<AuthResponseDto>

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<AuthResponseDto>

    @GET("api/v1/pharmacies/nearby")
    suspend fun searchNearbyPharmacies(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Double,
        @Query("drug") drugName: String? = null,
        @Query("in_stock") inStockOnly: Boolean = false,
        @Query("rx_only") rxOnly: Boolean = false
    ): Response<List<PharmacyNearbyResponseDto>>

    @GET("api/v1/hospitals/emergency-status")
    suspend fun getHospitalEmergencyStatus(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Double = 25.0
    ): Response<List<HospitalEmergencyCapacityDto>>

    @POST("api/v1/prescriptions")
    suspend fun uploadPrescription(
        @Body request: PrescriptionUploadRequestDto
    ): Response<PrescriptionUploadResponseDto>

    @POST("api/v1/emergency/incidents")
    suspend fun submitEmergencyIncident(
        @Body request: EmergencyTriageRequestDto
    ): Response<EmergencyTriageResponseDto>

    @POST("api/v1/verification/submit-license")
    suspend fun submitProfessionalLicense(
        @Body request: LicenseVerificationRequestDto
    ): Response<LicenseVerificationResponseDto>

    @PATCH("api/v1/pharmacies/{pharmacyId}/medications/{medicationId}/stock")
    suspend fun updateStock(
        @Path("pharmacyId") pharmacyId: String,
        @Path("medicationId") medicationId: String,
        @Body request: StockUpdateRequestDto
    ): Response<Unit>
}

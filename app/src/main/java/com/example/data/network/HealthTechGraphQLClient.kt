package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HealthTechGraphQLClient(
    private val endpointUrl: String,
    private val okHttpClient: OkHttpClient
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GraphQLRequest::class.java)

    companion object {
        const val PHARMACY_INVENTORY_QUERY = """
            query PharmacyInventorySearch(${'$'}lat: Float!, ${'$'}lng: Float!, ${'$'}radiusKm: Float!, ${'$'}query: String) {
              nearbyPharmacies(latitude: ${'$'}lat, longitude: ${'$'}lng, radiusKm: ${'$'}radiusKm, search: ${'$'}query) {
                id
                name
                address
                distanceKm
                isVerified
                medications {
                  id
                  name
                  brandName
                  strength
                  stockQuantity
                  price
                  requiresPrescription
                }
              }
            }
        """

        const val HOSPITAL_EMERGENCY_QUERY = """
            query HospitalEmergencyCapacity(${'$'}lat: Float!, ${'$'}lng: Float!) {
              nearbyHospitals(latitude: ${'$'}lat, longitude: ${'$'}lng) {
                id
                name
                emergencyStatus
                availableIcuBeds
                totalIcuBeds
                divertingAmbulances
                erWaitTimeMinutes
              }
            }
        """

        const val UPDATE_STOCK_MUTATION = """
            mutation UpdateStock(${'$'}pharmacyId: ID!, ${'$'}medicationId: ID!, ${'$'}quantity: Int!) {
              updateMedicationStock(pharmacyId: ${'$'}pharmacyId, medicationId: ${'$'}medicationId, quantity: ${'$'}quantity) {
                success
                updatedStock
              }
            }
        """
    }

    suspend fun executeQuery(query: String, variables: Map<String, Any?> = emptyMap()): String {
        return withContext(Dispatchers.IO) {
            val payload = requestAdapter.toJson(GraphQLRequest(query, variables))
            val requestBody = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(endpointUrl)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("GraphQL request failed with HTTP ${response.code}: ${response.message}")
            }
            response.body?.string() ?: "{}"
        }
    }
}

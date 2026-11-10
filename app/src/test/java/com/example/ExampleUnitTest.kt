package com.example

import com.example.data.network.HealthTechApiClient
import com.example.data.network.LoginRequestDto
import com.example.data.network.MedicationOfferDto
import com.example.data.network.PharmacyNearbyResponseDto
import com.example.data.network.RegisterRequestDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify API client base URL and services configuration`() {
    val baseUrl = HealthTechApiClient.getBaseUrl()
    assertTrue(baseUrl.startsWith("http"))
    assertNotNull(HealthTechApiClient.restService)
    assertNotNull(HealthTechApiClient.graphQLClient)
  }

  @Test
  fun `verify authentication DTO models`() {
    val registerDto = RegisterRequestDto(
      firstName = "Ada",
      lastName = "Okonkwo",
      email = "ada.okonkwo@healthtech.org",
      passwordHash = "argon2_secure_hash_here",
      phoneNumber = "+2348031234567",
      role = "pharmacist"
    )
    assertEquals("Ada", registerDto.firstName)
    assertEquals("pharmacist", registerDto.role)

    val loginDto = LoginRequestDto(
      email = "ada.okonkwo@healthtech.org",
      passwordHash = "hash"
    )
    assertEquals("ada.okonkwo@healthtech.org", loginDto.email)
  }

  @Test
  fun `verify pharmacy nearby response DTO`() {
    val offer = MedicationOfferDto(
      medicationId = "med_1",
      name = "Amoxicillin",
      brandName = "Amoxil",
      strength = "500mg",
      dosageForm = "Capsule",
      price = 3500.0,
      stockQuantity = 45,
      requiresPrescription = true
    )

    val pharmacyDto = PharmacyNearbyResponseDto(
      pharmacyId = "pharm_1",
      name = "HealthPlus Pharmacy Victoria Island",
      address = "Plot 12 Ahmadu Bello Way, VI, Lagos",
      phone = "+234 1 270 3300",
      latitude = 6.4281,
      longitude = 3.4219,
      distanceKm = 1.4,
      isVerified = true,
      medication = offer
    )

    assertEquals(1.4, pharmacyDto.distanceKm, 0.01)
    assertTrue(pharmacyDto.isVerified)
    assertEquals(45, pharmacyDto.medication?.stockQuantity)
    assertTrue(pharmacyDto.medication?.requiresPrescription == true)
  }
}


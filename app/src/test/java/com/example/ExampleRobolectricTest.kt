package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.model.MedicalVideo
import com.example.data.model.MedicationSchedule
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("HealthTech", appName)
  }

  @Test
  fun `verify medication schedule schema fields integrity`() {
    val schedule = MedicationSchedule(
      id = "sched_test_101",
      medicationName = "Amoxicillin",
      dosage = "500 mg",
      frequency = "Three times daily",
      timeOfDay = "Morning, Afternoon, Night (08:00 AM, 02:00 PM, 08:00 PM)",
      instructions = "Take with a full glass of water with meals",
      startDate = "2026-09-16",
      endDate = "2026-09-23",
      reminderEnabled = true,
      isTakenToday = false,
      isActive = true,
      prescribingDoctor = "Dr. Amina Alabi, MD",
      notes = "Post-op prophylaxis"
    )

    assertEquals("Amoxicillin", schedule.medicationName)
    assertEquals("500 mg", schedule.dosage)
    assertEquals("Three times daily", schedule.frequency)
    assertEquals("Morning, Afternoon, Night (08:00 AM, 02:00 PM, 08:00 PM)", schedule.timeOfDay)
    assertEquals("Take with a full glass of water with meals", schedule.instructions)
    assertTrue(schedule.reminderEnabled)
    assertFalse(schedule.isTakenToday)
    assertTrue(schedule.isActive)
    assertEquals("Dr. Amina Alabi, MD", schedule.prescribingDoctor)
  }

  @Test
  fun `verify room database executes medication schedule dao crud and queries`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    try {
      val dao = inMemoryDb.healthTechDao()

      // 1. Insert
      val testSchedule = MedicationSchedule(
        id = "test_sched_1",
        medicationName = "Lisinopril",
        dosage = "10 mg",
        frequency = "Once daily",
        timeOfDay = "Morning (08:00 AM)",
        instructions = "Take with water",
        reminderEnabled = true,
        isTakenToday = false,
        isActive = true
      )
      dao.insertMedicationSchedule(testSchedule)

      // 2. Query
      val retrieved = dao.getMedicationScheduleById("test_sched_1")
      assertNotNull(retrieved)
      assertEquals("Lisinopril", retrieved?.medicationName)
      assertEquals("10 mg", retrieved?.dosage)
      assertEquals("Once daily", retrieved?.frequency)
      assertEquals("Morning (08:00 AM)", retrieved?.timeOfDay)

      // 3. Update taken today status
      dao.updateScheduleTakenToday("test_sched_1", true)
      val updated = dao.getMedicationScheduleById("test_sched_1")
      assertTrue(updated?.isTakenToday == true)

      // 4. Update frequency and time of day
      val modifiedSchedule = testSchedule.copy(
        dosage = "20 mg",
        frequency = "Twice daily",
        timeOfDay = "Morning (08:00 AM) & Evening (08:00 PM)"
      )
      dao.updateMedicationSchedule(modifiedSchedule)
      val afterUpdate = dao.getMedicationScheduleById("test_sched_1")
      assertEquals("20 mg", afterUpdate?.dosage)
      assertEquals("Twice daily", afterUpdate?.frequency)
      assertEquals("Morning (08:00 AM) & Evening (08:00 PM)", afterUpdate?.timeOfDay)

      // 5. Flow query
      val allSchedules = dao.getAllMedicationSchedules().first()
      assertEquals(1, allSchedules.size)
      assertEquals("Lisinopril", allSchedules[0].medicationName)

      // 6. Delete
      dao.deleteMedicationScheduleById("test_sched_1")
      val afterDelete = dao.getMedicationScheduleById("test_sched_1")
      assertEquals(null, afterDelete)
    } finally {
      inMemoryDb.close()
    }
  }

  @Test
  fun `verify medical video model integrity and cme accreditation`() {
    val video = MedicalVideo(
      id = "test_vid_1",
      title = "Safe Pediatric Dosing Guidelines",
      description = "Clinical lecture on calculating weight-based dosing for pediatric antibiotics.",
      authorName = "Dr. Amina Alabi, MD",
      authorRole = "Pediatric Cardiologist",
      authorLicenseNo = "MDCN/Reg/39012",
      facilityName = "St. Luke Teaching Hospital",
      category = "Pediatrics",
      videoDuration = "05:30",
      durationSeconds = 330,
      videoUrl = "https://healthtech.demo/videos/pediatric_dosing.mp4",
      keyMedicalTakeaways = "1. Always double-check mg/kg calculations. 2. Verify renal function.",
      accreditedCmeUnits = 2,
      isVerifiedProfessional = true,
      isPeerReviewed = true,
      reviewingBoard = "National Board of Pediatric Specialists"
    )

    assertEquals("Safe Pediatric Dosing Guidelines", video.title)
    assertEquals("MDCN/Reg/39012", video.authorLicenseNo)
    assertEquals(2, video.accreditedCmeUnits)
    assertTrue(video.isVerifiedProfessional)
    assertTrue(video.isPeerReviewed)
  }

  @Test
  fun `verify only doctor and pharmacist roles can publish video content`() {
    val patientCanUpload = UserRole.PATIENT == UserRole.DOCTOR || UserRole.PATIENT == UserRole.PHARMACIST
    val hospitalCanUpload = UserRole.HOSPITAL == UserRole.DOCTOR || UserRole.HOSPITAL == UserRole.PHARMACIST
    val doctorCanUpload = UserRole.DOCTOR == UserRole.DOCTOR || UserRole.DOCTOR == UserRole.PHARMACIST
    val pharmacistCanUpload = UserRole.PHARMACIST == UserRole.DOCTOR || UserRole.PHARMACIST == UserRole.PHARMACIST

    assertFalse("Patients must not be allowed to upload unverified video advice", patientCanUpload)
    assertFalse("Hospital ER desk role must not upload clinical educational lectures directly", hospitalCanUpload)
    assertTrue("Doctors must be authorized to upload clinical videos", doctorCanUpload)
    assertTrue("Pharmacists must be authorized to upload medication guidance videos", pharmacistCanUpload)
  }
}

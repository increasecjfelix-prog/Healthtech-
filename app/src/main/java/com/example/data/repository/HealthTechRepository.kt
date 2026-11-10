package com.example.data.repository

import com.example.data.dao.HealthTechDao
import com.example.data.model.AuditLogRecord
import com.example.data.model.HospitalProfile
import com.example.data.model.MedicalArticle
import com.example.data.model.MedicalVideo
import com.example.data.model.MedicationItem
import com.example.data.model.MedicationSchedule
import com.example.data.model.MedicineAuthenticityRecord
import com.example.data.model.PharmacyOutlet
import com.example.data.model.PosSaleRecord
import com.example.data.model.PreferredFacilityRecord
import com.example.data.model.PrescriptionRequestRecord
import com.example.data.model.ReportedContentRecord
import com.example.data.model.UserAccountEntity
import com.example.data.model.VerificationRecord
import kotlinx.coroutines.flow.Flow

class HealthTechRepository(private val dao: HealthTechDao) {

    // -------------------------------------------------------------
    // Users & Authentication
    // -------------------------------------------------------------
    val allUsers: Flow<List<UserAccountEntity>> = dao.getAllUsers()

    suspend fun getUserByEmail(email: String): UserAccountEntity? =
        dao.getUserByEmail(email)

    suspend fun getUserById(id: String): UserAccountEntity? =
        dao.getUserById(id)

    suspend fun insertUser(user: UserAccountEntity) =
        dao.insertUser(user)

    suspend fun updateUserVerification(userId: String, status: String) =
        dao.updateUserVerification(userId, status)

    suspend fun updateUserSuspension(userId: String, isSuspended: Boolean) =
        dao.updateUserSuspension(userId, isSuspended)

    // -------------------------------------------------------------
    // Pharmacies
    // -------------------------------------------------------------
    val allPharmacies: Flow<List<PharmacyOutlet>> = dao.getAllPharmacies()

    suspend fun getPharmacyById(id: String): PharmacyOutlet? =
        dao.getPharmacyById(id)

    suspend fun updatePharmacy(pharmacy: PharmacyOutlet) =
        dao.updatePharmacy(pharmacy)

    suspend fun setPharmacyVerification(pharmacyId: String, isVerified: Boolean) =
        dao.setPharmacyVerification(pharmacyId, isVerified)

    suspend fun touchPharmacyInventory(pharmacyId: String, timestamp: Long) =
        dao.touchPharmacyInventory(pharmacyId, timestamp)

    // -------------------------------------------------------------
    // Medications
    // -------------------------------------------------------------
    val allMedications: Flow<List<MedicationItem>> = dao.getAllMedications()

    fun getMedicationsByPharmacy(pharmacyId: String): Flow<List<MedicationItem>> =
        dao.getMedicationsByPharmacy(pharmacyId)

    fun searchMedications(query: String): Flow<List<MedicationItem>> =
        dao.searchMedications(query)

    suspend fun insertMedication(medication: MedicationItem) =
        dao.insertMedication(medication)

    suspend fun updateMedication(medication: MedicationItem) =
        dao.updateMedication(medication)

    suspend fun updateStockQuantity(medicationId: String, newQuantity: Int, timestamp: Long = System.currentTimeMillis()) =
        dao.updateStockQuantity(medicationId, newQuantity, timestamp)

    suspend fun updateMedicationAvailability(medicationId: String, isAvailable: Boolean) =
        dao.updateMedicationAvailability(medicationId, isAvailable)

    suspend fun updateMedicationPrice(medicationId: String, newPrice: Double, formatted: String) =
        dao.updateMedicationPrice(medicationId, newPrice, formatted)

    suspend fun deleteMedication(medicationId: String) =
        dao.deleteMedication(medicationId)

    // -------------------------------------------------------------
    // POS Sales
    // -------------------------------------------------------------
    val allPosSales: Flow<List<PosSaleRecord>> = dao.getAllPosSales()

    fun getPosSalesByPharmacy(pharmacyId: String): Flow<List<PosSaleRecord>> =
        dao.getPosSalesByPharmacy(pharmacyId)

    suspend fun recordPosSale(sale: PosSaleRecord, purchasedMedications: List<Pair<MedicationItem, Int>>) {
        dao.insertPosSale(sale)
        val now = System.currentTimeMillis()
        purchasedMedications.forEach { (item, qty) ->
            val newQty = (item.stockQuantity - qty).coerceAtLeast(0)
            dao.updateMedication(item.copy(stockQuantity = newQty, lastUpdatedTimestamp = now))
        }
        dao.touchPharmacyInventory(sale.pharmacyId, now)
    }

    // -------------------------------------------------------------
    // Medical Articles
    // -------------------------------------------------------------
    val allArticles: Flow<List<MedicalArticle>> = dao.getAllArticles()

    fun getArticlesByCategory(category: String): Flow<List<MedicalArticle>> =
        dao.getArticlesByCategory(category)

    suspend fun publishArticle(article: MedicalArticle) =
        dao.insertArticle(article)

    suspend fun toggleBookmark(articleId: String, currentBookmarked: Boolean) =
        dao.updateBookmark(articleId, !currentBookmarked)

    suspend fun likeArticle(articleId: String) =
        dao.incrementLikes(articleId)

    suspend fun setArticleAdminApproval(articleId: String, isApproved: Boolean, isFlagged: Boolean) =
        dao.setArticleAdminApproval(articleId, isApproved, isFlagged)

    suspend fun deleteArticle(articleId: String) =
        dao.deleteArticle(articleId)

    // -------------------------------------------------------------
    // Medical Videos
    // -------------------------------------------------------------
    val allVideos: Flow<List<MedicalVideo>> = dao.getAllVideos()

    fun getVideosByCategory(category: String): Flow<List<MedicalVideo>> =
        dao.getVideosByCategory(category)

    suspend fun uploadVideo(video: MedicalVideo) =
        dao.insertVideo(video)

    suspend fun incrementVideoLikes(videoId: String) =
        dao.incrementVideoLikes(videoId)

    suspend fun incrementVideoViews(videoId: String) =
        dao.incrementVideoViews(videoId)

    suspend fun toggleVideoBookmark(videoId: String, currentBookmarked: Boolean) =
        dao.updateVideoBookmark(videoId, !currentBookmarked)

    suspend fun setVideoFlagged(videoId: String, isFlagged: Boolean) =
        dao.setVideoFlagged(videoId, isFlagged)

    suspend fun deleteVideo(videoId: String) =
        dao.deleteVideo(videoId)

    // -------------------------------------------------------------
    // Content Reporting & Moderation
    // -------------------------------------------------------------
    val allReportedContent: Flow<List<ReportedContentRecord>> = dao.getAllReportedContent()

    suspend fun reportContent(report: ReportedContentRecord) =
        dao.insertContentReport(report)

    suspend fun updateReportStatus(
        reportId: String,
        status: String,
        adminNotes: String,
        reviewedBy: String,
        reviewedTimestamp: Long = System.currentTimeMillis()
    ) = dao.updateReportStatus(reportId, status, adminNotes, reviewedBy, reviewedTimestamp)

    // -------------------------------------------------------------
    // Hospitals & Healthcare Facilities
    // -------------------------------------------------------------
    val allHospitals: Flow<List<HospitalProfile>> = dao.getAllHospitals()

    suspend fun getHospitalById(id: String): HospitalProfile? =
        dao.getHospitalById(id)

    suspend fun updateHospital(hospital: HospitalProfile) =
        dao.updateHospital(hospital)

    suspend fun updateHospitalEmergencyStatus(
        hospitalId: String,
        status: String,
        availableIcu: Int,
        erWait: Int,
        timestamp: Long = System.currentTimeMillis()
    ) = dao.updateHospitalEmergencyStatus(hospitalId, status, availableIcu, erWait, timestamp)

    suspend fun setHospitalVerification(hospitalId: String, isVerified: Boolean) =
        dao.setHospitalVerification(hospitalId, isVerified)

    // -------------------------------------------------------------
    // Professional Verification Records
    // -------------------------------------------------------------
    val allVerifications: Flow<List<VerificationRecord>> = dao.getAllVerifications()

    suspend fun submitVerification(record: VerificationRecord) =
        dao.insertVerification(record)

    suspend fun checkLicense(licenseNumber: String): VerificationRecord? =
        dao.getVerificationByLicense(licenseNumber)

    suspend fun updateVerificationStatus(
        id: String,
        status: String,
        badgeIssued: Boolean,
        reviewerNotes: String = "",
        reviewedTimestamp: Long = System.currentTimeMillis()
    ) = dao.updateVerificationStatus(id, status, badgeIssued, reviewerNotes, reviewedTimestamp)

    // -------------------------------------------------------------
    // Platform Audit Logs
    // -------------------------------------------------------------
    val allAuditLogs: Flow<List<AuditLogRecord>> = dao.getAllAuditLogs()

    suspend fun recordAuditLog(log: AuditLogRecord) =
        dao.insertAuditLog(log)

    // -------------------------------------------------------------
    // Medicine Authenticity & Safety Checks
    // -------------------------------------------------------------
    val allAuthenticityChecks: Flow<List<MedicineAuthenticityRecord>> = dao.getAllAuthenticityChecks()

    suspend fun findAuthenticityByCode(code: String): MedicineAuthenticityRecord? =
        dao.findAuthenticityByCode(code)

    suspend fun recordAuthenticityCheck(record: MedicineAuthenticityRecord) =
        dao.insertAuthenticityCheck(record)

    // -------------------------------------------------------------
    // Prescription Requests
    // -------------------------------------------------------------
    val allPrescriptions: Flow<List<PrescriptionRequestRecord>> = dao.getAllPrescriptions()

    fun getPrescriptionsByPharmacy(pharmacyId: String): Flow<List<PrescriptionRequestRecord>> =
        dao.getPrescriptionsByPharmacy(pharmacyId)

    fun getPrescriptionsByPatient(patientId: String): Flow<List<PrescriptionRequestRecord>> =
        dao.getPrescriptionsByPatient(patientId)

    suspend fun submitPrescription(request: PrescriptionRequestRecord) =
        dao.insertPrescription(request)

    suspend fun updatePrescriptionStatus(id: String, status: String, notes: String) =
        dao.updatePrescriptionStatus(id, status, notes)

    // -------------------------------------------------------------
    // Preferred Facilities
    // -------------------------------------------------------------
    fun getPreferredFacilities(userId: String): Flow<List<PreferredFacilityRecord>> =
        dao.getPreferredFacilities(userId)

    suspend fun isFacilityPreferred(userId: String, facilityId: String): Boolean =
        dao.isFacilityPreferred(userId, facilityId)

    suspend fun togglePreferredFacility(item: PreferredFacilityRecord, currentIsSaved: Boolean) {
        if (currentIsSaved) {
            dao.removePreferredFacility(item.userId, item.facilityId)
        } else {
            dao.addPreferredFacility(item)
        }
    }

    // -------------------------------------------------------------
    // User Medication Schedules
    // -------------------------------------------------------------
    val allMedicationSchedules: Flow<List<MedicationSchedule>> = dao.getAllMedicationSchedules()
    val activeMedicationSchedules: Flow<List<MedicationSchedule>> = dao.getActiveMedicationSchedules()

    suspend fun getScheduleById(id: String): MedicationSchedule? =
        dao.getMedicationScheduleById(id)

    suspend fun addMedicationSchedule(schedule: MedicationSchedule) =
        dao.insertMedicationSchedule(schedule)

    suspend fun updateMedicationSchedule(schedule: MedicationSchedule) =
        dao.updateMedicationSchedule(schedule)

    suspend fun deleteMedicationSchedule(schedule: MedicationSchedule) =
        dao.deleteMedicationSchedule(schedule)

    suspend fun deleteMedicationScheduleById(id: String) =
        dao.deleteMedicationScheduleById(id)

    suspend fun toggleScheduleTakenToday(scheduleId: String, currentTaken: Boolean) =
        dao.updateScheduleTakenToday(scheduleId, !currentTaken)

    suspend fun toggleScheduleActiveStatus(scheduleId: String, currentActive: Boolean) =
        dao.updateScheduleActiveStatus(scheduleId, !currentActive)
}

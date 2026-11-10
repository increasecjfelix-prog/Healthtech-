package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface HealthTechDao {

    // -------------------------------------------------------------
    // 1. Users & Authentication
    // -------------------------------------------------------------
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccountEntity>)

    @Query("UPDATE users SET verificationStatus = :status WHERE id = :userId")
    suspend fun updateUserVerification(userId: String, status: String)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
    suspend fun updateUserSuspension(userId: String, isSuspended: Boolean)

    // -------------------------------------------------------------
    // 2. Pharmacies
    // -------------------------------------------------------------
    @Query("SELECT * FROM pharmacies ORDER BY distanceKm ASC")
    fun getAllPharmacies(): Flow<List<PharmacyOutlet>>

    @Query("SELECT * FROM pharmacies WHERE id = :pharmacyId LIMIT 1")
    suspend fun getPharmacyById(pharmacyId: String): PharmacyOutlet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacies(pharmacies: List<PharmacyOutlet>)

    @Update
    suspend fun updatePharmacy(pharmacy: PharmacyOutlet)

    @Query("UPDATE pharmacies SET isVerified = :isVerified WHERE id = :pharmacyId")
    suspend fun setPharmacyVerification(pharmacyId: String, isVerified: Boolean)

    @Query("UPDATE pharmacies SET lastInventoryUpdate = :timestamp WHERE id = :pharmacyId")
    suspend fun touchPharmacyInventory(pharmacyId: String, timestamp: Long)

    // -------------------------------------------------------------
    // 3. Medications & Inventory
    // -------------------------------------------------------------
    @Query("SELECT * FROM medications ORDER BY name ASC")
    fun getAllMedications(): Flow<List<MedicationItem>>

    @Query("SELECT * FROM medications WHERE pharmacyId = :pharmacyId ORDER BY name ASC")
    fun getMedicationsByPharmacy(pharmacyId: String): Flow<List<MedicationItem>>

    @Query("""
        SELECT * FROM medications 
        WHERE name LIKE '%' || :query || '%' 
           OR brandName LIKE '%' || :query || '%' 
           OR strength LIKE '%' || :query || '%' 
           OR dosageForm LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
    """)
    fun searchMedications(query: String): Flow<List<MedicationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(medications: List<MedicationItem>)

    @Update
    suspend fun updateMedication(medication: MedicationItem)

    @Query("UPDATE medications SET stockQuantity = :newQuantity, lastUpdatedTimestamp = :timestamp WHERE id = :medicationId")
    suspend fun updateStockQuantity(medicationId: String, newQuantity: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE medications SET isAvailable = :isAvailable WHERE id = :medicationId")
    suspend fun updateMedicationAvailability(medicationId: String, isAvailable: Boolean)

    @Query("UPDATE medications SET price = :newPrice, priceFormatted = :formatted WHERE id = :medicationId")
    suspend fun updateMedicationPrice(medicationId: String, newPrice: Double, formatted: String)

    @Query("DELETE FROM medications WHERE id = :medicationId")
    suspend fun deleteMedication(medicationId: String)

    // -------------------------------------------------------------
    // 4. POS Sales Records
    // -------------------------------------------------------------
    @Query("SELECT * FROM pos_sales ORDER BY timestamp DESC")
    fun getAllPosSales(): Flow<List<PosSaleRecord>>

    @Query("SELECT * FROM pos_sales WHERE pharmacyId = :pharmacyId ORDER BY timestamp DESC")
    fun getPosSalesByPharmacy(pharmacyId: String): Flow<List<PosSaleRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosSale(sale: PosSaleRecord)

    // -------------------------------------------------------------
    // 5. Medical Articles & Reporting
    // -------------------------------------------------------------
    @Query("SELECT * FROM medical_articles WHERE isApprovedByAdmin = 1 ORDER BY id DESC")
    fun getAllArticles(): Flow<List<MedicalArticle>>

    @Query("SELECT * FROM medical_articles WHERE category = :category AND isApprovedByAdmin = 1 ORDER BY id DESC")
    fun getArticlesByCategory(category: String): Flow<List<MedicalArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: MedicalArticle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<MedicalArticle>)

    @Query("UPDATE medical_articles SET isBookmarked = :isBookmarked WHERE id = :articleId")
    suspend fun updateBookmark(articleId: String, isBookmarked: Boolean)

    @Query("UPDATE medical_articles SET likesCount = likesCount + 1 WHERE id = :articleId")
    suspend fun incrementLikes(articleId: String)

    @Query("UPDATE medical_articles SET isApprovedByAdmin = :isApproved, isFlagged = :isFlagged WHERE id = :articleId")
    suspend fun setArticleAdminApproval(articleId: String, isApproved: Boolean, isFlagged: Boolean)

    @Query("DELETE FROM medical_articles WHERE id = :articleId")
    suspend fun deleteArticle(articleId: String)

    // -------------------------------------------------------------
    // 6. Medical Videos
    // -------------------------------------------------------------
    @Query("SELECT * FROM medical_videos WHERE isFlagged = 0 ORDER BY id DESC")
    fun getAllVideos(): Flow<List<MedicalVideo>>

    @Query("SELECT * FROM medical_videos WHERE category = :category AND isFlagged = 0 ORDER BY id DESC")
    fun getVideosByCategory(category: String): Flow<List<MedicalVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: MedicalVideo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<MedicalVideo>)

    @Query("UPDATE medical_videos SET likesCount = likesCount + 1 WHERE id = :videoId")
    suspend fun incrementVideoLikes(videoId: String)

    @Query("UPDATE medical_videos SET viewsCount = viewsCount + 1 WHERE id = :videoId")
    suspend fun incrementVideoViews(videoId: String)

    @Query("UPDATE medical_videos SET isBookmarked = :isBookmarked WHERE id = :videoId")
    suspend fun updateVideoBookmark(videoId: String, isBookmarked: Boolean)

    @Query("UPDATE medical_videos SET isFlagged = :isFlagged WHERE id = :videoId")
    suspend fun setVideoFlagged(videoId: String, isFlagged: Boolean)

    @Query("DELETE FROM medical_videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)

    // -------------------------------------------------------------
    // 7. Reported Content & Moderation
    // -------------------------------------------------------------
    @Query("SELECT * FROM reported_content ORDER BY reportedTimestamp DESC")
    fun getAllReportedContent(): Flow<List<ReportedContentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentReport(report: ReportedContentRecord)

    @Query("UPDATE reported_content SET status = :status, adminNotes = :adminNotes, reviewedBy = :reviewedBy, reviewedTimestamp = :reviewedTimestamp WHERE id = :reportId")
    suspend fun updateReportStatus(
        reportId: String,
        status: String,
        adminNotes: String,
        reviewedBy: String,
        reviewedTimestamp: Long = System.currentTimeMillis()
    )

    // -------------------------------------------------------------
    // 8. Hospitals & Healthcare Facilities
    // -------------------------------------------------------------
    @Query("SELECT * FROM hospitals ORDER BY distanceKm ASC")
    fun getAllHospitals(): Flow<List<HospitalProfile>>

    @Query("SELECT * FROM hospitals WHERE id = :id LIMIT 1")
    suspend fun getHospitalById(id: String): HospitalProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospitals(hospitals: List<HospitalProfile>)

    @Update
    suspend fun updateHospital(hospital: HospitalProfile)

    @Query("""
        UPDATE hospitals 
        SET emergencyStatus = :status, 
            availableIcuBeds = :availableIcu, 
            erWaitMinutes = :erWait,
            emergencyStatusUpdatedAt = :timestamp 
        WHERE id = :hospitalId
    """)
    suspend fun updateHospitalEmergencyStatus(
        hospitalId: String,
        status: String,
        availableIcu: Int,
        erWait: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE hospitals SET isVerified = :isVerified WHERE id = :hospitalId")
    suspend fun setHospitalVerification(hospitalId: String, isVerified: Boolean)

    // -------------------------------------------------------------
    // 9. Professional Verification Records
    // -------------------------------------------------------------
    @Query("SELECT * FROM verifications ORDER BY submissionTimestamp DESC")
    fun getAllVerifications(): Flow<List<VerificationRecord>>

    @Query("SELECT * FROM verifications WHERE licenseNumber = :licenseNumber LIMIT 1")
    suspend fun getVerificationByLicense(licenseNumber: String): VerificationRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(record: VerificationRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerifications(records: List<VerificationRecord>)

    @Query("""
        UPDATE verifications 
        SET status = :status, 
            verifiedBadgeIssued = :badgeIssued, 
            reviewerNotes = :reviewerNotes,
            reviewedTimestamp = :reviewedTimestamp 
        WHERE id = :id
    """)
    suspend fun updateVerificationStatus(
        id: String,
        status: String,
        badgeIssued: Boolean,
        reviewerNotes: String = "",
        reviewedTimestamp: Long = System.currentTimeMillis()
    )

    // -------------------------------------------------------------
    // 10. Audit Logs
    // -------------------------------------------------------------
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLogRecord>)

    // -------------------------------------------------------------
    // 11. Medicine Authenticity Checks
    // -------------------------------------------------------------
    @Query("SELECT * FROM medicine_authenticity ORDER BY checkedTimestamp DESC")
    fun getAllAuthenticityChecks(): Flow<List<MedicineAuthenticityRecord>>

    @Query("SELECT * FROM medicine_authenticity WHERE scratchCode = :code OR regulatoryPin = :code LIMIT 1")
    suspend fun findAuthenticityByCode(code: String): MedicineAuthenticityRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthenticityCheck(record: MedicineAuthenticityRecord)

    // -------------------------------------------------------------
    // 12. Prescription Requests
    // -------------------------------------------------------------
    @Query("SELECT * FROM prescription_requests ORDER BY submittedTimestamp DESC")
    fun getAllPrescriptions(): Flow<List<PrescriptionRequestRecord>>

    @Query("SELECT * FROM prescription_requests WHERE targetPharmacyId = :pharmacyId ORDER BY submittedTimestamp DESC")
    fun getPrescriptionsByPharmacy(pharmacyId: String): Flow<List<PrescriptionRequestRecord>>

    @Query("SELECT * FROM prescription_requests WHERE patientId = :patientId ORDER BY submittedTimestamp DESC")
    fun getPrescriptionsByPatient(patientId: String): Flow<List<PrescriptionRequestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: PrescriptionRequestRecord)

    @Query("UPDATE prescription_requests SET status = :status, notesFromPharmacist = :notes WHERE id = :id")
    suspend fun updatePrescriptionStatus(id: String, status: String, notes: String)

    // -------------------------------------------------------------
    // 13. Preferred Facilities (Favorites)
    // -------------------------------------------------------------
    @Query("SELECT * FROM preferred_facilities WHERE userId = :userId ORDER BY savedTimestamp DESC")
    fun getPreferredFacilities(userId: String): Flow<List<PreferredFacilityRecord>>

    @Query("SELECT EXISTS(SELECT 1 FROM preferred_facilities WHERE userId = :userId AND facilityId = :facilityId)")
    suspend fun isFacilityPreferred(userId: String, facilityId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPreferredFacility(item: PreferredFacilityRecord)

    @Query("DELETE FROM preferred_facilities WHERE userId = :userId AND facilityId = :facilityId")
    suspend fun removePreferredFacility(userId: String, facilityId: String)

    // -------------------------------------------------------------
    // 14. Medication Schedules
    // -------------------------------------------------------------
    @Query("SELECT * FROM medication_schedules ORDER BY isActive DESC, timeOfDay ASC")
    fun getAllMedicationSchedules(): Flow<List<MedicationSchedule>>

    @Query("SELECT * FROM medication_schedules WHERE isActive = 1 ORDER BY timeOfDay ASC")
    fun getActiveMedicationSchedules(): Flow<List<MedicationSchedule>>

    @Query("SELECT * FROM medication_schedules WHERE id = :id LIMIT 1")
    suspend fun getMedicationScheduleById(id: String): MedicationSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationSchedule(schedule: MedicationSchedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationSchedules(schedules: List<MedicationSchedule>)

    @Update
    suspend fun updateMedicationSchedule(schedule: MedicationSchedule)

    @Delete
    suspend fun deleteMedicationSchedule(schedule: MedicationSchedule)

    @Query("DELETE FROM medication_schedules WHERE id = :id")
    suspend fun deleteMedicationScheduleById(id: String)

    @Query("UPDATE medication_schedules SET isTakenToday = :taken WHERE id = :id")
    suspend fun updateScheduleTakenToday(id: String, taken: Boolean)

    @Query("UPDATE medication_schedules SET isActive = :isActive WHERE id = :id")
    suspend fun updateScheduleActiveStatus(id: String, isActive: Boolean)
}

package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.data.model.UserRole
import com.example.data.model.VerificationRecord
import com.example.data.security.AuthSecurity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PharmacyOutlet::class,
        MedicationItem::class,
        PosSaleRecord::class,
        MedicalArticle::class,
        MedicalVideo::class,
        HospitalProfile::class,
        VerificationRecord::class,
        MedicationSchedule::class,
        UserAccountEntity::class,
        ReportedContentRecord::class,
        AuditLogRecord::class,
        MedicineAuthenticityRecord::class,
        PrescriptionRequestRecord::class,
        PreferredFacilityRecord::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthTechDao(): HealthTechDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthtech_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.healthTechDao())
                }
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.healthTechDao())
                }
            }
        }

        suspend fun populateInitialData(dao: HealthTechDao) {
            val defaultSalt = AuthSecurity.generateSalt()
            val defaultHash = AuthSecurity.hashPassword("password123", defaultSalt)
            val now = System.currentTimeMillis()

            // 1. Initial Seed Users for All 7 Roles
            val seedUsers = listOf(
                UserAccountEntity(
                    id = "usr_patient_1",
                    fullName = "Felix Increase",
                    email = "increasecjfelix@gmail.com",
                    phone = "+2348011223344",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.PATIENT.name,
                    verificationStatus = "VERIFIED",
                    emergencyContactName = "Blessing Increase",
                    emergencyContactPhone = "+2348099887766",
                    bloodGroup = "O+",
                    allergies = "Penicillin (Mild)",
                    createdAt = now - 86400000L * 10
                ),
                UserAccountEntity(
                    id = "usr_doctor_1",
                    fullName = "Dr. Adaobi Okeke, MD",
                    email = "dr.adaobi@healthtech.org",
                    phone = "+2348022334455",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.DOCTOR.name,
                    verificationStatus = "VERIFIED",
                    regulatoryBody = "Medical and Dental Council of Nigeria (MDCN)",
                    licenseNumber = "MDCN/R/2016/39482",
                    professionalCategory = "Medical Practitioner",
                    specialty = "Cardiovascular Medicine",
                    facilityAffiliation = "St. Nicholas Hospital",
                    certificateDocName = "MDCN_Practicing_License_2026.pdf",
                    createdAt = now - 86400000L * 25
                ),
                UserAccountEntity(
                    id = "usr_pharmacist_1",
                    fullName = "Pharm. Emeka Eze, B.Pharm",
                    email = "emeka.eze@healthtech.org",
                    phone = "+2348033445566",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.PHARMACIST.name,
                    verificationStatus = "VERIFIED",
                    regulatoryBody = "Pharmacy Council of Nigeria (PCN)",
                    licenseNumber = "PCN/REG/2018/51029",
                    professionalCategory = "Registered Pharmacist",
                    specialty = "Clinical Pharmacology & Antimicrobial Stewardship",
                    facilityAffiliation = "CarePoint Community Pharmacy",
                    certificateDocName = "PCN_Annual_Premises_Cert_2026.pdf",
                    createdAt = now - 86400000L * 30
                ),
                UserAccountEntity(
                    id = "usr_pharmacy_outlet_1",
                    fullName = "CarePoint Community Pharmacy Outlet",
                    email = "ops@carepointrx.org",
                    phone = "+2348001234567",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.PHARMACY_OUTLET.name,
                    verificationStatus = "VERIFIED",
                    regulatoryBody = "PCN Registry of Premises",
                    licenseNumber = "PCN/PREMISES/2021/8472",
                    facilityAffiliation = "CarePoint Yaba Hub",
                    createdAt = now - 86400000L * 40
                ),
                UserAccountEntity(
                    id = "usr_hospital_1",
                    fullName = "Lagos University Teaching Hospital (LUTH)",
                    email = "emergency@luth.gov.ng",
                    phone = "+234 1 800 5884",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.HOSPITAL.name,
                    verificationStatus = "VERIFIED",
                    regulatoryBody = "Federal Ministry of Health & MDCN",
                    licenseNumber = "FMoH/TERTIARY/LAG/001",
                    facilityAffiliation = "Federal Government of Nigeria",
                    createdAt = now - 86400000L * 60
                ),
                UserAccountEntity(
                    id = "usr_facility_1",
                    fullName = "Medex Diagnostic & Specialized Imaging Centre",
                    email = "info@medexdiagnostics.ng",
                    phone = "+234 1 293 8472",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.HEALTHCARE_FACILITY.name,
                    verificationStatus = "VERIFIED",
                    regulatoryBody = "Health Facility Monitoring and Accreditation Agency (HEFAMAA)",
                    licenseNumber = "HEFAMAA/DIAG/2022/1903",
                    facilityAffiliation = "Medex Healthcare Diagnostics Group",
                    createdAt = now - 86400000L * 15
                ),
                UserAccountEntity(
                    id = "usr_admin_1",
                    fullName = "Platform Compliance Administrator",
                    email = "admin@healthtech.gov.ng",
                    phone = "+234 1 000 8899",
                    passwordHash = defaultHash,
                    passwordSalt = defaultSalt,
                    role = UserRole.ADMIN.name,
                    verificationStatus = "VERIFIED",
                    regulatoryBody = "HealthTech Regulatory Oversight Bureau",
                    licenseNumber = "HTECH/ADMIN/SUPER/001",
                    createdAt = now - 86400000L * 100
                )
            )
            dao.insertUsers(seedUsers)

            // 2. Pharmacies
            val samplePharmacies = listOf(
                PharmacyOutlet(
                    id = "pharm_1",
                    name = "CarePoint Community Pharmacy",
                    licenseNumber = "PCN/REG/2021/8472",
                    address = "14 Commercial Avenue, Yaba, Lagos",
                    distanceKm = 0.8,
                    phone = "+2348001234567",
                    isOpen24Hours = true,
                    rating = 4.9,
                    isVerified = true,
                    latitude = 5.483,
                    longitude = 7.033,
                    operatingHours = "Mon-Sun: 24 Hours Open",
                    operatingStatus = "Open",
                    lastInventoryUpdate = now - 3600000L, // 1 hour ago
                    isActive = true
                ),
                PharmacyOutlet(
                    id = "pharm_2",
                    name = "Meds & More Express Outlet",
                    licenseNumber = "PCN/REG/2019/3321",
                    address = "52 Herbert Macaulay Way, Alagomeji, Lagos",
                    distanceKm = 1.6,
                    phone = "+2348007654321",
                    isOpen24Hours = false,
                    rating = 4.7,
                    isVerified = true,
                    latitude = 5.487,
                    longitude = 7.036,
                    operatingHours = "Mon-Sat: 08:00 - 21:00",
                    operatingStatus = "Open",
                    lastInventoryUpdate = now - 1800000L, // 30 mins ago
                    isActive = true
                ),
                PharmacyOutlet(
                    id = "pharm_3",
                    name = "St. Jude Clinical Pharmacy",
                    licenseNumber = "PCN/REG/2022/9901",
                    address = "8 Borno Way, Ebute Metta, Lagos",
                    distanceKm = 2.4,
                    phone = "+2348009876543",
                    isOpen24Hours = false,
                    rating = 4.8,
                    isVerified = true,
                    latitude = 5.479,
                    longitude = 7.029,
                    operatingHours = "Mon-Fri: 08:30 - 19:30",
                    operatingStatus = "Call First",
                    lastInventoryUpdate = now - 86400000L * 2, // 2 days ago (disclaimer applies)
                    isActive = true
                ),
                PharmacyOutlet(
                    id = "pharm_4",
                    name = "PrimeLife 24/7 Dispensary",
                    licenseNumber = "PCN/REG/2020/4155",
                    address = "102 Ikorodu Road, Fadeyi, Lagos",
                    distanceKm = 3.8,
                    phone = "+2348005551212",
                    isOpen24Hours = true,
                    rating = 4.6,
                    isVerified = true,
                    latitude = 5.495,
                    longitude = 7.045,
                    operatingHours = "Mon-Sun: 24/7",
                    operatingStatus = "Open",
                    lastInventoryUpdate = now - 900000L, // 15 mins ago
                    isActive = true
                )
            )
            dao.insertPharmacies(samplePharmacies)

            // 3. Medications matching search specifications
            val sampleMedications = listOf(
                MedicationItem(
                    id = "med_1",
                    pharmacyId = "pharm_1",
                    name = "Amoxicillin",
                    brandName = "Amoxil Capsules",
                    dosageForm = "Capsule",
                    strength = "500mg",
                    category = "Antibiotic",
                    batchNumber = "BATCH-AMX-2026A",
                    expiryDate = "2027-11",
                    price = 3500.0,
                    priceFormatted = "₦3,500",
                    stockQuantity = 45,
                    isAvailable = true,
                    requiresPrescription = true,
                    indication = "Bacterial infections, respiratory tract infections, dental abscesses",
                    lastUpdatedTimestamp = now - 3600000L
                ),
                MedicationItem(
                    id = "med_2",
                    pharmacyId = "pharm_1",
                    name = "Amoxicillin + Clavulanic Acid",
                    brandName = "Augmentin Tablets",
                    dosageForm = "Film-coated Tablet",
                    strength = "625mg",
                    category = "Antibiotic",
                    batchNumber = "BATCH-AUG-8891B",
                    expiryDate = "2027-08",
                    price = 8500.0,
                    priceFormatted = "₦8,500",
                    stockQuantity = 18,
                    isAvailable = true,
                    requiresPrescription = true,
                    indication = "Complicated bacterial infections, sinusitis, otitis media",
                    lastUpdatedTimestamp = now - 3600000L
                ),
                MedicationItem(
                    id = "med_3",
                    pharmacyId = "pharm_1",
                    name = "Paracetamol",
                    brandName = "Panadol Extra",
                    dosageForm = "Tablet",
                    strength = "500mg/65mg",
                    category = "Analgesic & Antipyretic",
                    batchNumber = "BATCH-PAN-4431C",
                    expiryDate = "2028-03",
                    price = 950.0,
                    priceFormatted = "₦950",
                    stockQuantity = 120,
                    isAvailable = true,
                    requiresPrescription = false,
                    indication = "Mild to moderate pain, headache, fever reduction",
                    lastUpdatedTimestamp = now - 3600000L
                ),
                MedicationItem(
                    id = "med_4",
                    pharmacyId = "pharm_2",
                    name = "Amoxicillin",
                    brandName = "Amoxil Suspension",
                    dosageForm = "Oral Suspension",
                    strength = "250mg/5ml",
                    category = "Antibiotic",
                    batchNumber = "BATCH-AMX-SUS-901",
                    expiryDate = "2027-05",
                    price = 3200.0,
                    priceFormatted = "₦3,200",
                    stockQuantity = 22,
                    isAvailable = true,
                    requiresPrescription = true,
                    indication = "Pediatric respiratory and ear infections",
                    lastUpdatedTimestamp = now - 1800000L
                ),
                MedicationItem(
                    id = "med_5",
                    pharmacyId = "pharm_2",
                    name = "Metformin HCl",
                    brandName = "Glucophage Retard",
                    dosageForm = "Extended-release Tablet",
                    strength = "500mg",
                    category = "Antidiabetic",
                    batchNumber = "BATCH-MET-7712",
                    expiryDate = "2028-01",
                    price = 4200.0,
                    priceFormatted = "₦4,200",
                    stockQuantity = 38,
                    isAvailable = true,
                    requiresPrescription = true,
                    indication = "Type 2 diabetes glycemic control",
                    lastUpdatedTimestamp = now - 1800000L
                ),
                MedicationItem(
                    id = "med_6",
                    pharmacyId = "pharm_3",
                    name = "Artemether + Lumefantrine",
                    brandName = "Coartem Dispersible",
                    dosageForm = "Dispersible Tablet",
                    strength = "20mg/120mg",
                    category = "Antimalarial",
                    batchNumber = "BATCH-COA-3991D",
                    expiryDate = "2027-10",
                    price = 3900.0,
                    priceFormatted = "₦3,900",
                    stockQuantity = 14,
                    isAvailable = true,
                    requiresPrescription = false,
                    indication = "Acute uncomplicated Plasmodium falciparum malaria",
                    lastUpdatedTimestamp = now - 86400000L * 2
                ),
                MedicationItem(
                    id = "med_7",
                    pharmacyId = "pharm_4",
                    name = "Amlodipine Besylate",
                    brandName = "Norvasc Tablets",
                    dosageForm = "Tablet",
                    strength = "5mg",
                    category = "Antihypertensive",
                    batchNumber = "BATCH-AML-0182",
                    expiryDate = "2028-04",
                    price = 4800.0,
                    priceFormatted = "₦4,800",
                    stockQuantity = 55,
                    isAvailable = true,
                    requiresPrescription = true,
                    indication = "Essential hypertension, stable angina",
                    lastUpdatedTimestamp = now - 900000L
                )
            )
            dao.insertMedications(sampleMedications)

            // 4. Hospitals and Healthcare Facilities
            val sampleHospitals = listOf(
                HospitalProfile(
                    id = "hosp_1",
                    name = "Lagos University Teaching Hospital (LUTH)",
                    facilityType = "Hospital",
                    licenseNumber = "FMoH/TERTIARY/LAG/001",
                    address = "Idi-Araba, Surulere, Lagos",
                    distanceKm = 1.2,
                    emergencyPhone = "+234 1 800 5884",
                    ambulancePhone = "+234 803 999 0011",
                    operatingHours = "24/7 Full Tertiary & Emergency Trauma",
                    emergencyStatus = "ACCEPTING_PATIENTS",
                    emergencyStatusUpdatedAt = now - 600000L, // 10 mins ago
                    erWaitMinutes = 15,
                    availableIcuBeds = 6,
                    totalIcuBeds = 14,
                    availableGeneralBeds = 32,
                    hasOxygenSupply = true,
                    hasTraumaUnit = true,
                    traumaBaysReady = 4,
                    bloodBankStock = "O+ (16 units), A+ (8 units), B+ (6 units), O- (4 units)",
                    oxygenPlantOperational = true,
                    ambulanceAvailable = true,
                    ambulanceUnitsActive = 3,
                    servicesOffered = "Level 1 Trauma, Resuscitation, Cardiac ICU, Pediatric Emergency, Stroke Center",
                    isVerified = true,
                    latitude = 5.483,
                    longitude = 7.033
                ),
                HospitalProfile(
                    id = "hosp_2",
                    name = "St. Nicholas Hospital Emergency Centre",
                    facilityType = "Hospital",
                    licenseNumber = "FMoH/PRIVATE/LAG/4492",
                    address = "57 Campbell Street, Lagos Island",
                    distanceKm = 3.5,
                    emergencyPhone = "+234 1 280 1800",
                    ambulancePhone = "+234 802 111 2233",
                    operatingHours = "24/7 Emergency & Critical Care",
                    emergencyStatus = "ACCEPTING_PATIENTS",
                    emergencyStatusUpdatedAt = now - 1200000L, // 20 mins ago
                    erWaitMinutes = 8,
                    availableIcuBeds = 3,
                    totalIcuBeds = 8,
                    availableGeneralBeds = 18,
                    hasOxygenSupply = true,
                    hasTraumaUnit = true,
                    traumaBaysReady = 2,
                    bloodBankStock = "O+ (10 units), A+ (4 units), AB+ (2 units)",
                    oxygenPlantOperational = true,
                    ambulanceAvailable = true,
                    ambulanceUnitsActive = 2,
                    servicesOffered = "Dialysis Center, Cardiac Catheterization, Surgical ICU, Rapid Response Ambulance",
                    isVerified = true,
                    latitude = 5.488,
                    longitude = 7.039
                ),
                HospitalProfile(
                    id = "hosp_3",
                    name = "Medex Diagnostic & Specialist Clinic",
                    facilityType = "Other Healthcare Facility",
                    licenseNumber = "HEFAMAA/DIAG/2022/1903",
                    address = "22 Admiralty Way, Lekki Phase 1, Lagos",
                    distanceKm = 4.2,
                    emergencyPhone = "+234 1 293 8472",
                    ambulancePhone = "+234 809 333 4455",
                    operatingHours = "Mon-Sun: 07:00 - 22:00",
                    emergencyStatus = "TEMPORARILY_UNAVAILABLE",
                    emergencyStatusUpdatedAt = now - 7200000L, // 2 hours ago
                    erWaitMinutes = 45,
                    availableIcuBeds = 0,
                    totalIcuBeds = 2,
                    availableGeneralBeds = 10,
                    hasOxygenSupply = true,
                    hasTraumaUnit = false,
                    traumaBaysReady = 1,
                    bloodBankStock = "None (Outpatient Diagnostic Hub)",
                    oxygenPlantOperational = true,
                    ambulanceAvailable = true,
                    ambulanceUnitsActive = 1,
                    servicesOffered = "High-Resolution MRI, CT Angiography, Clinical Pathology, Walk-in Urgent Care",
                    isVerified = true,
                    latitude = 5.475,
                    longitude = 7.025
                )
            )
            dao.insertHospitals(sampleHospitals)

            // 5. Professional Verification Requests
            val sampleVerifications = listOf(
                VerificationRecord(
                    id = "ver_doc_1",
                    applicantName = "Dr. Adaobi Okeke, MD",
                    email = "dr.adaobi@healthtech.org",
                    phone = "+2348022334455",
                    facilityName = "St. Nicholas Hospital",
                    professionType = "DOCTOR",
                    regulatoryCouncil = "Medical and Dental Council of Nigeria (MDCN)",
                    licenseNumber = "MDCN/R/2016/39482",
                    certificateDocName = "MDCN_License_A_Okeke_2026.pdf",
                    specialization = "Cardiovascular Medicine",
                    status = "VERIFIED",
                    reviewerNotes = "MDCN primary source credentials verified through national registry.",
                    submissionTimestamp = now - 86400000L * 15,
                    reviewedTimestamp = now - 86400000L * 14,
                    verifiedBadgeIssued = true
                ),
                VerificationRecord(
                    id = "ver_pharm_1",
                    applicantName = "Pharm. Emeka Eze, B.Pharm",
                    email = "emeka.eze@healthtech.org",
                    phone = "+2348033445566",
                    facilityName = "CarePoint Community Pharmacy",
                    professionType = "PHARMACIST",
                    regulatoryCouncil = "Pharmacy Council of Nigeria (PCN)",
                    licenseNumber = "PCN/REG/2018/51029",
                    certificateDocName = "PCN_Annual_License_Eze_2026.pdf",
                    specialization = "Clinical Pharmacology",
                    status = "VERIFIED",
                    reviewerNotes = "PCN certified pharmacist in good standing.",
                    submissionTimestamp = now - 86400000L * 20,
                    reviewedTimestamp = now - 86400000L * 19,
                    verifiedBadgeIssued = true
                ),
                VerificationRecord(
                    id = "ver_doc_pending",
                    applicantName = "Dr. Tunde Fashola, MBBS",
                    email = "dr.fashola@lagoshealth.org",
                    phone = "+2348055667788",
                    facilityName = "Ikeja General Hospital",
                    professionType = "DOCTOR",
                    regulatoryCouncil = "Medical and Dental Council of Nigeria (MDCN)",
                    licenseNumber = "MDCN/R/2021/77412",
                    certificateDocName = "MDCN_Certificate_TFashola.pdf",
                    specialization = "General Internal Medicine",
                    status = "PENDING",
                    reviewerNotes = "Awaiting verification response from MDCN e-portal.",
                    submissionTimestamp = now - 86400000L * 2,
                    verifiedBadgeIssued = false
                ),
                VerificationRecord(
                    id = "ver_pharm_pending",
                    applicantName = "Pharm. Fatima Bello, PharmD",
                    email = "fatima.bello@lifelinecare.ng",
                    phone = "+2348066778899",
                    facilityName = "LifeLine Retail Pharmacy",
                    professionType = "PHARMACIST",
                    regulatoryCouncil = "Pharmacy Council of Nigeria (PCN)",
                    licenseNumber = "PCN/REG/2023/11490",
                    certificateDocName = "PCN_Certificate_FBello.pdf",
                    specialization = "Community Pharmacy & Vaccine Administration",
                    status = "PENDING",
                    reviewerNotes = "Document uploaded, pending administrative review.",
                    submissionTimestamp = now - 86400000L * 1,
                    verifiedBadgeIssued = false
                )
            )
            dao.insertVerifications(sampleVerifications)

            // 6. Verified Medical Education Content
            val sampleArticles = listOf(
                MedicalArticle(
                    id = "art_1",
                    title = "Responsible Antibiotic Usage and Antimicrobial Resistance (AMR)",
                    authorName = "Pharm. Emeka Eze, B.Pharm",
                    authorRole = "Pharmacist",
                    authorLicenseNo = "PCN/REG/2018/51029",
                    isVerifiedProfessional = true,
                    isPeerReviewed = true,
                    reviewingBody = "National Clinical Review Board",
                    category = "Medication safety",
                    summary = "Why completing antibiotic courses matters and how improper dosing fuels dangerous drug resistance.",
                    content = "Antibiotics are miracle medicines that treat bacterial infections, but misuse has triggered global antimicrobial resistance. Never demand antibiotics for viral illnesses like common colds or flu. Always complete the prescribed regimen even if symptoms improve early.",
                    keyTakeaways = "• Never share prescribed antibiotics with others\n• Viral infections do not respond to antibiotics\n• Always complete the full treatment schedule to prevent resistant bacterial mutations",
                    publishedDate = "Yesterday",
                    readTimeMinutes = 4,
                    likesCount = 38,
                    isBookmarked = false,
                    isFlagged = false,
                    isApprovedByAdmin = true
                ),
                MedicalArticle(
                    id = "art_2",
                    title = "Understanding Blood Pressure Readings and Silent Hypertension",
                    authorName = "Dr. Adaobi Okeke, MD",
                    authorRole = "Doctor",
                    authorLicenseNo = "MDCN/R/2016/39482",
                    isVerifiedProfessional = true,
                    isPeerReviewed = true,
                    reviewingBody = "Cardiological Society Standards Committee",
                    category = "Chronic disease education",
                    summary = "Hypertension often shows zero early symptoms. Learn normal systolic/diastolic ranges and routine monitoring intervals.",
                    content = "Hypertension is widely known as the silent killer because high pressure inside arteries causes no obvious pain or discomfort until target organ damage occurs in the heart, kidneys, or brain. A reading consistently above 130/80 mmHg requires lifestyle modification and clinical evaluation.",
                    keyTakeaways = "• Systolic < 120 mmHg and Diastolic < 80 mmHg is optimal\n• Reduce daily dietary sodium intake below 2 grams\n• Regular 30-minute moderate aerobic exercise reduces arterial stiffness",
                    publishedDate = "3 days ago",
                    readTimeMinutes = 5,
                    likesCount = 64,
                    isBookmarked = false,
                    isFlagged = false,
                    isApprovedByAdmin = true
                ),
                MedicalArticle(
                    id = "art_3",
                    title = "Pediatric Fever Management and Warning Red Flags",
                    authorName = "Dr. Adaobi Okeke, MD",
                    authorRole = "Doctor",
                    authorLicenseNo = "MDCN/R/2016/39482",
                    isVerifiedProfessional = true,
                    isPeerReviewed = true,
                    reviewingBody = "Pediatric Clinical Review Board",
                    category = "Preventive health",
                    summary = "Safe fever reduction techniques for children under 5, dosing precautions, and when to seek urgent emergency care.",
                    content = "Fever is the immune system's physiological response to pathogens. Keep the child hydrated, avoid overdressing, and administer weight-appropriate antipyretics. Red flags warranting immediate emergency room presentation include lethargy, neck stiffness, petechial rashes, or seizures.",
                    keyTakeaways = "• Calculate paracetamol dosage by weight, never age alone\n• Never give aspirin to children due to Reye's syndrome risk\n• Seek urgent ER care if infant under 3 months has temperature >= 38°C",
                    publishedDate = "5 days ago",
                    readTimeMinutes = 4,
                    likesCount = 52,
                    isBookmarked = false,
                    isFlagged = false,
                    isApprovedByAdmin = true
                )
            )
            dao.insertArticles(sampleArticles)

            // 7. Audit Logs for Admin Transparency
            val sampleAuditLogs = listOf(
                AuditLogRecord(
                    id = "log_001",
                    timestamp = now - 86400000L * 14,
                    actorName = "Platform Compliance Administrator",
                    actorEmail = "admin@healthtech.gov.ng",
                    actorRole = "ADMIN",
                    actionType = "VERIFICATION_APPROVED",
                    targetId = "ver_doc_1",
                    targetType = "VERIFICATION",
                    description = "Approved MDCN credentials for Dr. Adaobi Okeke following license verification.",
                    ipAddress = "197.210.64.12"
                ),
                AuditLogRecord(
                    id = "log_002",
                    timestamp = now - 86400000L * 19,
                    actorName = "Platform Compliance Administrator",
                    actorEmail = "admin@healthtech.gov.ng",
                    actorRole = "ADMIN",
                    actionType = "VERIFICATION_APPROVED",
                    targetId = "ver_pharm_1",
                    targetType = "VERIFICATION",
                    description = "Approved PCN credentials for Pharm. Emeka Eze and accredited CarePoint pharmacy outlet.",
                    ipAddress = "197.210.64.12"
                ),
                AuditLogRecord(
                    id = "log_003",
                    timestamp = now - 600000L,
                    actorName = "Lagos University Teaching Hospital (LUTH)",
                    actorEmail = "emergency@luth.gov.ng",
                    actorRole = "HOSPITAL",
                    actionType = "EMERGENCY_STATUS_UPDATED",
                    targetId = "hosp_1",
                    targetType = "HOSPITAL",
                    description = "Refreshed ER status to ACCEPTING_PATIENTS with 6 available ICU beds.",
                    ipAddress = "105.112.98.4"
                )
            )
            dao.insertAuditLogs(sampleAuditLogs)

            // 8. Medicine Authenticity & Anti-Counterfeiting Database
            val sampleAuthenticityChecks = listOf(
                MedicineAuthenticityRecord(
                    id = "auth_001",
                    drugName = "Artemether + Lumefantrine 20/120mg",
                    brandName = "Coartem",
                    manufacturer = "Novartis Pharma AG",
                    batchNumber = "BATCH-COA-3991D",
                    scratchCode = "948271039482",
                    regulatoryPin = "NAFDAC-A4-0192",
                    verificationStatus = "AUTHENTIC_VERIFIED",
                    verificationProvider = "Mobile Authentication Service (MAS) / Regulatory Agency",
                    checkedTimestamp = now - 18000000L,
                    alertNotes = "Authentic product verified with manufacturer database. Expiry: 2027-10."
                ),
                MedicineAuthenticityRecord(
                    id = "auth_002",
                    drugName = "Amoxicillin / Clavulanate 625mg",
                    brandName = "Augmentin",
                    manufacturer = "GlaxoSmithKline",
                    batchNumber = "BATCH-AUG-8891B",
                    scratchCode = "381920491823",
                    regulatoryPin = "NAFDAC-04-1294",
                    verificationStatus = "AUTHENTIC_VERIFIED",
                    verificationProvider = "Mobile Authentication Service (MAS) / Regulatory Agency",
                    checkedTimestamp = now - 36000000L,
                    alertNotes = "Authentic batch registered in regulatory portal."
                ),
                MedicineAuthenticityRecord(
                    id = "auth_003",
                    drugName = "Counterfeit Antimalarial Alert",
                    brandName = "Suspect Artesunate 50mg",
                    manufacturer = "Unverified Underground Lab",
                    batchNumber = "BATCH-FAKE-9912",
                    scratchCode = "000011112222",
                    regulatoryPin = "INVALID-PIN",
                    verificationStatus = "POTENTIAL_COUNTERFEIT",
                    verificationProvider = "Regulatory Drug Quality Surveillance Warning",
                    checkedTimestamp = now - 72000000L,
                    alertNotes = "WARNING: Code does not match any registered pharmaceutical lot. Contains zero active pharmaceutical ingredient."
                )
            )
            sampleAuthenticityChecks.forEach { dao.insertAuthenticityCheck(it) }

            // 9. Sample Prescription Request
            val samplePrescription = PrescriptionRequestRecord(
                id = "rx_req_101",
                patientId = "usr_patient_1",
                patientName = "Felix Increase",
                patientPhone = "+2348011223344",
                patientEmail = "increasecjfelix@gmail.com",
                targetPharmacyId = "pharm_1",
                targetPharmacyName = "CarePoint Community Pharmacy",
                prescribingDoctor = "Dr. Adaobi Okeke (MDCN/R/2016/39482)",
                diagnosisSummary = "Acute bacterial pharyngitis",
                medicationsList = "1. Amoxicillin 500mg Caps (TDS x 7 days)\n2. Paracetamol 500mg Tabs (PRN)",
                prescriptionDocumentUrl = "prescription_felix_inc_092026.pdf",
                status = "ACCEPTED",
                notesFromPharmacist = "Prescription verified with prescriber. Order ready for dispensary pickup or courier delivery.",
                submittedTimestamp = now - 14400000L
            )
            dao.insertPrescription(samplePrescription)
        }
    }
}

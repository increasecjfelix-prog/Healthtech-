package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object AuthSecurity {

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return Base64.getEncoder().encodeToString(saltBytes)
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = "$salt:$password".toByteArray(Charsets.UTF_8)
        val hash = md.digest(combined)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val calculated = hashPassword(password, salt)
        return MessageDigest.isEqual(calculated.toByteArray(), expectedHash.toByteArray())
    }

    fun maskLicenseNumber(licenseNumber: String): String {
        if (licenseNumber.length <= 6) return licenseNumber
        val prefix = licenseNumber.take(4)
        val suffix = licenseNumber.takeLast(4)
        return "$prefix/****-$suffix"
    }

    fun redactDocumentUrlForRole(docUrl: String, userRole: String): String {
        return if (userRole.equals("ADMIN", ignoreCase = true)) {
            docUrl
        } else {
            "[CONFIDENTIAL REGULATORY DOCUMENT - AUDITED]"
        }
    }
}

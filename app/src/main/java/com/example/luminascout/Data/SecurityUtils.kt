package com.example.luminascout

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtils {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    fun createSalt(): String {
        val salt = ByteArray(SALT_LENGTH)

        SecureRandom().nextBytes(salt)

        return Base64.encodeToString(
            salt,
            Base64.NO_WRAP
        )
    }

    fun hashPassword(
        password: String,
        saltString: String
    ): String {

        val salt = Base64.decode(
            saltString,
            Base64.NO_WRAP
        )

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        val factory = SecretKeyFactory.getInstance(
            "PBKDF2WithHmacSHA256"
        )

        val hash = factory
            .generateSecret(spec)
            .encoded

        return Base64.encodeToString(
            hash,
            Base64.NO_WRAP
        )
    }

    fun verifyPassword(
        password: String,
        storedHash: String,
        storedSalt: String
    ): Boolean {

        return hashPassword(
            password,
            storedSalt
        ) == storedHash
    }
}
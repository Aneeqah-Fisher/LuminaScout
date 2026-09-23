package com.example.luminascout

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityUtilsTest {

    @Test
    fun passwordHashIsCreated() {

        val salt = SecurityUtils.createSalt()

        val hash = SecurityUtils.hashPassword(
            "Lumina123!",
            salt
        )

        assertTrue(hash.isNotEmpty())
    }

    @Test
    fun correctPasswordIsAccepted() {

        val password = "Lumina123!"

        val salt = SecurityUtils.createSalt()

        val hash = SecurityUtils.hashPassword(
            password,
            salt
        )

        assertTrue(
            SecurityUtils.verifyPassword(
                password,
                hash,
                salt
            )
        )
    }

    @Test
    fun incorrectPasswordIsRejected() {

        val salt = SecurityUtils.createSalt()

        val hash = SecurityUtils.hashPassword(
            "Lumina123!",
            salt
        )

        assertFalse(
            SecurityUtils.verifyPassword(
                "WrongPassword!",
                hash,
                salt
            )
        )
    }

    @Test
    fun differentSaltsProduceDifferentHashes() {

        val password = "Lumina123!"

        val salt1 = SecurityUtils.createSalt()
        val salt2 = SecurityUtils.createSalt()

        val hash1 = SecurityUtils.hashPassword(
            password,
            salt1
        )

        val hash2 = SecurityUtils.hashPassword(
            password,
            salt2
        )

        assertNotEquals(
            hash1,
            hash2
        )
    }
}
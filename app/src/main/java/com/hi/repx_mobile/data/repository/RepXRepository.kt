package com.hi.repx_mobile.data.repository

import com.hi.repx_mobile.data.database.dao.UserDao
import com.hi.repx_mobile.data.database.entities.User
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class RepXRepository(
    private val userDao: UserDao,

    ) {
    // User Operations
    suspend fun registerUser(email: String, password: String, displayName: String): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception("Email already registered"))
            } else {
                val passwordHash = hashPassword(password)
                val user =
                    User(email = email, passwordHash = passwordHash, displayName = displayName)
                val userId = userDao.insert(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user == null) {
                Result.failure(Exception("User not found"))
            } else if (user.passwordHash != hashPassword(password)) {
                Result.failure(Exception("Invalid password"))
            } else {
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: Long) {
        userDao.deleteById(userId)
    }

    fun getUserFlow(userId: Long): Flow<User?> = userDao.getUserByIdFlow(userId)

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

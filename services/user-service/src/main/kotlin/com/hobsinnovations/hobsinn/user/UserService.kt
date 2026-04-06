package com.hobsinnovations.hobsinn.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(request: CreateUserRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }
        if (userRepository.existsByPhone(request.phone)) {
            throw IllegalArgumentException("Phone already exists")
        }

        val user = User(
            name = request.name,
            email = request.email,
            phone = request.phone,
            password = passwordEncoder.encode(request.password),
            role = request.role,
            accountStatus = request.accountStatus,
            ecoPoints = request.ecoPoints,
            kgCollected = request.kgCollected,
            badges = request.badges.toMutableSet()
        )
        return userRepository.save(user)
    }

    fun getUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun searchUsers(role: UserRole?, status: AccountStatus?, query: String?): List<User> {
        return userRepository.findAll().filter { user ->
            val roleMatches = role == null || user.role == role
            val statusMatches = status == null || user.accountStatus == status
            val queryMatches = query.isNullOrBlank() ||
                user.name.contains(query, ignoreCase = true) ||
                user.email.contains(query, ignoreCase = true) ||
                user.phone.contains(query, ignoreCase = true)
            roleMatches && statusMatches && queryMatches
        }
    }

    fun updateUser(id: Long, request: UpdateUserRequest): User? {
        val user = userRepository.findById(id).orElse(null) ?: return null

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            email = request.email ?: user.email,
            phone = request.phone ?: user.phone,
            password = request.password?.let { passwordEncoder.encode(it) } ?: user.password,
            role = request.role ?: user.role,
            accountStatus = request.accountStatus ?: user.accountStatus,
            ecoPoints = request.ecoPoints ?: user.ecoPoints,
            kgCollected = request.kgCollected ?: user.kgCollected,
            badges = request.badges?.toMutableSet() ?: user.badges,
            updatedAt = LocalDateTime.now()
        )
        return userRepository.save(updatedUser)
    }

    fun deleteUser(id: Long): Boolean {
        if (!userRepository.existsById(id)) return false
        userRepository.deleteById(id)
        return true
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getSummary(): UserSummary {
        val users = userRepository.findAll()
        return UserSummary(
            totalUsers = users.size,
            adminCount = users.count { it.role == UserRole.ADMIN || it.role == UserRole.ADMINISTRATOR },
            providerCount = users.count { it.role == UserRole.PROVIDER },
            customerCount = users.count { it.role == UserRole.CUSTOMER || it.role == UserRole.HOUSEHOLD },
            ambassadorCount = users.count { it.role == UserRole.AMBASSADOR },
            activeAccounts = users.count { it.accountStatus == AccountStatus.ACTIVE },
            suspendedAccounts = users.count { it.accountStatus == AccountStatus.SUSPENDED },
            deactivatedAccounts = users.count { it.accountStatus == AccountStatus.DEACTIVATED }
        )
    }
}

data class CreateUserRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: UserRole = UserRole.CUSTOMER,
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,
    val ecoPoints: Long = 0,
    val kgCollected: Double = 0.0,
    val badges: Set<String> = emptySet()
)

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val role: UserRole? = null,
    val accountStatus: AccountStatus? = null,
    val ecoPoints: Long? = null,
    val kgCollected: Double? = null,
    val badges: Set<String>? = null
)

data class UserSummary(
    val totalUsers: Int,
    val adminCount: Int,
    val providerCount: Int,
    val customerCount: Int,
    val ambassadorCount: Int,
    val activeAccounts: Int,
    val suspendedAccounts: Int,
    val deactivatedAccounts: Int
)
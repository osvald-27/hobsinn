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
            role = request.role
        )
        return userRepository.save(user)
    }

    fun getUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }

    fun updateUser(id: Long, request: UpdateUserRequest): User? {
        val user = userRepository.findById(id).orElse(null) ?: return null

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            phone = request.phone ?: user.phone,
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
}

data class CreateUserRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: UserRole = UserRole.CUSTOMER
)

data class UpdateUserRequest(
    val name: String? = null,
    val phone: String? = null
)
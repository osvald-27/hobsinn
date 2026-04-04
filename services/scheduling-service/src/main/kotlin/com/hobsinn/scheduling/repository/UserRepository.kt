package com.hobsinn.scheduling.repository

import com.hobsinn.scheduling.domain.User
import com.hobsinn.scheduling.domain.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByPhoneNumber(phoneNumber: String): User?
    fun existsByPhoneNumber(phoneNumber: String): Boolean
    fun findAllByRole(role: UserRole): List<User>
}

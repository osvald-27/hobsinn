package com.hobsinnovations.hobsinn.user

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false, unique = true)
    val phone: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.CUSTOMER,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class UserRole {
    CUSTOMER, PROVIDER, ADMIN
}
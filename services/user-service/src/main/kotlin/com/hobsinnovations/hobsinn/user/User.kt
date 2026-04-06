package com.hobsinnovations.hobsinn.user

import com.fasterxml.jackson.annotation.JsonIgnore
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

    @JsonIgnore
    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole = UserRole.CUSTOMER,

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "eco_points", nullable = false)
    var ecoPoints: Long = 0,

    @Column(name = "kg_collected", nullable = false)
    var kgCollected: Double = 0.0,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_badges", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "badge")
    var badges: MutableSet<String> = mutableSetOf()
) {
    fun toResponse() = UserResponse(
        id = id,
        name = name,
        email = email,
        phone = phone,
        role = role,
        accountStatus = accountStatus,
        createdAt = createdAt,
        updatedAt = updatedAt,
        ecoPoints = ecoPoints,
        kgCollected = kgCollected,
        badges = badges.toList()
    )
}

data class UserResponse(
    val id: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val accountStatus: AccountStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val ecoPoints: Long,
    val kgCollected: Double,
    val badges: List<String>
)

enum class UserRole {
    CUSTOMER, PROVIDER, ADMIN, HOUSEHOLD, PICKUP, AMBASSADOR, ADMINISTRATOR
}

enum class AccountStatus {
    ACTIVE, SUSPENDED, DEACTIVATED
}
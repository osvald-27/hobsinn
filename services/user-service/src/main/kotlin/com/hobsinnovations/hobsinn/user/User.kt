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
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var ecoPoints: Long = 0,

    @Column(nullable = false)
    var kgCollected: Double = 0.0,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_badges", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "badge")
    var badges: MutableSet<String> = mutableSetOf()
)

enum class UserRole {
    CUSTOMER, PROVIDER, ADMIN
}
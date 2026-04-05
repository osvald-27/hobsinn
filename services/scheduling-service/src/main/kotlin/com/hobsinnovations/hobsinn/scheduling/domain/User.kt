package com.hobsinnovations.hobsinn.scheduling.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

enum class UserRole { HOUSEHOLD, PICKUP, AMBASSADOR, ADMINISTRATOR }

@Entity
@Table(name = "users")
class User(

    @Id
    @Column(name = "user_id")
    val userId: UUID = UUID.randomUUID(),

    @Column(name = "phone_number", nullable = false, unique = true)
    val phoneNumber: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: UserRole,

    @Column(name = "preferred_language", nullable = false)
    var preferredLanguage: String = "en",

    @Column(name = "badge_count", nullable = false)
    var badgeCount: Int = 0,

    @Column(name = "rating_score", nullable = false)
    var ratingScore: BigDecimal = BigDecimal("1.0"),

    @Column(name = "star_rating", nullable = false)
    var starRating: BigDecimal = BigDecimal("6.0"),

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun toStarRating(): Int = (ratingScore.toDouble() * 6).toInt().coerceIn(1, 6)
}

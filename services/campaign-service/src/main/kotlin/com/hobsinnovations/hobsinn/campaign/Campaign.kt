package com.hobsinnovations.hobsinn.campaign

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "campaigns")
data class Campaign(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, length = 1000)
    val description: String,

    @Column(nullable = false)
    val startDate: LocalDateTime,

    @Column(nullable = false)
    val endDate: LocalDateTime,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)